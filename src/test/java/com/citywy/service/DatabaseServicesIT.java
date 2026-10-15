package com.citywy.service;

import com.citywy.domain.Leftmenu;
import com.citywy.domain.News;
import com.citywy.domain.Notice;
import com.citywy.domain.Topmenu;
import com.citywy.domain.User;
import com.citywy.util.DBConnectionPool;
import com.citywy.util.DBUtil;
import com.citywy.util.DatabaseException;
import com.citywy.util.PasswordHashes;
import com.citywy.util.PasswordMigration;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.*;

public class DatabaseServicesIT {
    private static String server;
    private static String username;
    private static String password;
    private static String databaseName;
    private static boolean ownsDatabase;
    private static HikariDataSource source;
    private static DBUtil database;

    @BeforeClass
    public static void setUp() throws Exception {
        server = required("NEWS_TEST_MYSQL_URL");
        username = required("NEWS_TEST_MYSQL_USER");
        password = required("NEWS_TEST_MYSQL_PASSWORD");
        if (!server.matches("jdbc:mysql://[^/]+/")) throw new IllegalArgumentException("Use a dedicated test server URL without a database name");
        Class.forName("com.mysql.cj.jdbc.Driver");
        databaseName = "news_test_" + UUID.randomUUID().toString().replace("-", "");
        try (Connection connection = DriverManager.getConnection(server, username, password); Statement sql = connection.createStatement()) {
            sql.execute("CREATE DATABASE `" + databaseName + "` DEFAULT CHARACTER SET utf8mb4");
            ownsDatabase = true;
        }
        source = DBConnectionPool.createDataSource(server + databaseName + "?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8", username, password);
        database = new DBUtil(source);
        String schema = new String(Files.readAllBytes(Paths.get("sql/schema.sql")), StandardCharsets.UTF_8).replaceAll("(?m)^\\s*--.*$", "");
        try (Connection connection = source.getConnection(); Statement sql = connection.createStatement()) {
            for (String statement : schema.split(";")) if (!statement.trim().isEmpty()) sql.execute(statement);
        }
        UserService users = new UserService(source);
        assertTrue(users.addUser(user("alpha", "alpha-secret")));
        assertTrue(users.addUser(user("beta", "beta-secret")));
        Leftmenu value = new Leftmenu(); value.setLeftmenuName("100%_!导航"); value.setLeftmenuContent("院长's栏目");
        assertTrue(new LeftmenuService(source).addLeftmenu(value));
    }

    @AfterClass
    public static void cleanUp() throws Exception {
        if (source != null) source.close();
        if (ownsDatabase && databaseName != null && databaseName.matches("news_test_[0-9a-f]{32}")) {
            try (Connection connection = DriverManager.getConnection(server, username, password); Statement sql = connection.createStatement()) {
                sql.execute("DROP DATABASE `" + databaseName + "`");
            }
        }
    }

    @Test
    public void quotedValuesAndWildcardsStayDataAndCrudRoundTrips() {
        LeftmenuService menus = new LeftmenuService(source);
        assertEquals(1, menus.getLeftmenuByContent("院长's栏目", "leftmenuID", "ASC").size());
        assertEquals(0, menus.getLeftmenuByContent("x' OR 1=1 --", "leftmenuID", "ASC").size());
        assertEquals(1, menus.getLeftmenuList("%_!", 1, 10, "leftmenuName", "ASC").size());
        assertThrows(IllegalArgumentException.class, () -> menus.getLeftmenuList(null, 1, 10, "leftmenuID; DROP TABLE user", "ASC"));
        assertNull(menus.get("x' OR 1=1 --"));

        NewsService news = new NewsService(source);
        News article = new News(); article.setNewsName("测试'新闻"); article.setNewsContent("正文");
        assertTrue(news.addNews(article));
        News stored = news.getNewsAll("测试'新闻", "newsID", "ASC").get(0);
        stored.setNewsContent("修改后的正文"); assertTrue(news.updNews(stored));
        assertEquals("修改后的正文", news.get(stored.getNewsID()).getNewsContent());
        assertTrue(news.delNews(stored.getNewsID())); assertNull(news.get(stored.getNewsID()));

        NoticeService notices = new NoticeService(source);
        Notice notice = new Notice(); notice.setNoticeName("测试通知"); notice.setNoticeContent("通知正文");
        assertTrue(notices.addNotice(notice));
        assertEquals(1, notices.getNoticeAll("测试通知", "noticeID", "ASC").size());
        TopmenuService top = new TopmenuService(source);
        Topmenu menu = new Topmenu(); menu.setTopmenuName("测试顶栏"); menu.setTopmenuContent("顶栏内容");
        assertTrue(top.addTopmenu(menu));
        assertTrue(Integer.parseInt(top.getTopmenuAll("测试顶栏", "id", "ASC").get(0).getId()) > 0);
    }

    @Test
    public void concurrentLoginAndMenuReadsDoNotShareJdbcState() throws Exception {
        UserService users = new UserService(source);
        LeftmenuService menus = new LeftmenuService(source);
        ExecutorService threads = Executors.newFixedThreadPool(6);
        try {
            List<Callable<Boolean>> operations = new ArrayList<Callable<Boolean>>();
            for (int i = 0; i < 24; i++) {
                final String name = i % 2 == 0 ? "alpha" : "beta";
                operations.add(() -> users.UserLogin(user(name, name + "-secret"))
                        && name.equals(users.getUserByName(name).getName())
                        && users.getUserByName(name).getPassword() == null
                        && menus.getLeftmenuByContent("院长's栏目", "leftmenuID", "ASC").size() == 1);
            }
            for (Future<Boolean> result : threads.invokeAll(operations)) assertTrue(result.get());
        } finally { threads.shutdownNow(); }
        assertPoolIsIdle();
        assertTrue(source.getHikariPoolMXBean().getTotalConnections() <= 10);
    }

    @Test
    public void userWritesHashPasswordsAndMigrationIsExplicit() throws Exception {
        UserService users = new UserService(source);
        User value = user("o'reilly", "literal-secret");
        assertTrue(users.addUser(value));
        User publicUser = users.getUserByName("o'reilly");
        assertNull(publicUser.getPassword());
        assertTrue(users.UserLogin(value));
        assertFalse(users.UserLogin(user("o'reilly", "wrong")));
        publicUser.setUname("新的显示名");
        assertTrue(users.updUser(publicUser));
        assertTrue(users.UserLogin(value));
        publicUser.setPassword("new-secret"); assertTrue(users.updUser(publicUser));
        assertFalse(users.UserLogin(value)); assertTrue(users.UserLogin(user("o'reilly", "new-secret")));
        assertEquals(0, users.getRowCount("' OR 1=1 --"));
        assertTrue(users.getUserList("o'reilly", 1, 10, "name", "ASC").get(0).getPassword() == null);
        java.util.Map<String, String> filters = new java.util.LinkedHashMap<String, String>();
        filters.put("role", "管理"); filters.put("name", "o'reill"); filters.put("uname", "新的显示");
        assertEquals(1, users.countFiltered(filters));
        assertEquals("o'reilly", users.listFiltered(filters, 1, 10, "id", "ASC").get(0).getName());
        filters.put("uname", "其他人"); assertEquals(0, users.countFiltered(filters));
        filters.put("password", "x"); assertThrows(IllegalArgumentException.class, () -> users.countFiltered(filters));

        database.executeUpdate("INSERT INTO `user` (name, password, role, valid) VALUES (?, ?, ?, ?)", "legacy", "legacy-secret", "管理员", "有效");
        assertThrows(IllegalStateException.class, () -> users.UserLogin(user("legacy", "legacy-secret")));
        assertEquals(1, PasswordMigration.migrate(source));
        assertTrue(users.UserLogin(user("legacy", "legacy-secret")));
        assertEquals(0, PasswordMigration.migrate(source));
        database.executeUpdate("UPDATE `user` SET password = ? WHERE name = ?", "$2a$12$broken", "legacy");
        assertThrows(IllegalStateException.class, () -> users.UserLogin(user("legacy", "legacy-secret")));
        assertThrows(IllegalStateException.class, () -> PasswordMigration.migrate(source));
        database.executeUpdate("UPDATE `user` SET password = ? WHERE name = ?", PasswordHashes.hash("legacy-secret"), "legacy");
        publicUser.setValid("无效"); publicUser.setPassword(null); assertTrue(users.updUser(publicUser));
        assertFalse(users.UserLogin(user("o'reilly", "new-secret")));
    }

    @Test
    public void databaseErrorsReachCallerAndReturnConnections() throws Exception {
        database.executeUpdate("RENAME TABLE leftmenu TO leftmenu_unavailable");
        try {
            assertThrows(DatabaseException.class, () -> new LeftmenuService(source).getRowCount(null));
        } finally { database.executeUpdate("RENAME TABLE leftmenu_unavailable TO leftmenu"); }
        assertPoolIsIdle();
    }

    private static void assertPoolIsIdle() throws Exception {
        assertEquals("业务调用结束后不得仍有被借出的连接", 0, source.getHikariPoolMXBean().getActiveConnections());
    }

    private static User user(String name, String password) {
        User result = new User(); result.setName(name); result.setUname(name); result.setPassword(password); result.setRole("管理员"); result.setValid("有效");
        return result;
    }
    private static String required(String name) {
        String value = System.getenv(name);
        if (value == null || value.isEmpty()) throw new IllegalArgumentException("Missing test setting " + name);
        return value;
    }
}
