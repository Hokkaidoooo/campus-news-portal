package com.citywy.integration;

import com.google.gson.*;
import org.junit.Test;
import static org.junit.Assert.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PortalIT {
    private static final String PASSWORD = "verification-password-20261015";
    private static final String SERVER_DEFAULT_CHARSET = "GB18030";
    private static String required(String name) {
        String value = System.getenv(name);
        if (value == null || value.isEmpty()) throw new IllegalStateException("集成测试需要 " + name);
        return value;
    }

    @Test public void sameWarWorksInRootAndNamedContextWithRealDatabaseAndBrowser() throws Exception {
        String adminUrl = required("NEWS_TEST_MYSQL_URL");
        String user = required("NEWS_TEST_MYSQL_USER"), password = required("NEWS_TEST_MYSQL_PASSWORD");
        String database = "news_test_" + UUID.randomUUID().toString().replace("-", "");
        String[] urlParts = adminUrl.split("\\?", 2);
        if (!urlParts[0].endsWith("/")) throw new IllegalArgumentException("测试 URL 必须指向服务器根目录，末尾为 /");
        String jdbc = urlParts[0] + database + (urlParts.length == 2 ? "?" + urlParts[1] : "");
        try (Connection admin = DriverManager.getConnection(adminUrl, user, password)) {
            try (Statement statement = admin.createStatement()) { statement.execute("CREATE DATABASE `" + database + "` CHARACTER SET utf8mb4"); }
            try {
                seed(jdbc, user, password);
                Set<Integer> ports = new HashSet<>();
                for (String context : Arrays.asList("", "/news")) ports.add(verifyDeployment(context, jdbc, user, password));
                assertEquals("两种上下文使用不同端口", 2, ports.size());
            } finally {
                // database 是本方法刚创建的随机标识，绝不从外部输入获取待删除库名。
                try (Statement statement = admin.createStatement()) { statement.execute("DROP DATABASE `" + database + "`"); }
            }
        }
    }

    private static void seed(String jdbc, String user, String password) throws Exception {
        try (Connection connection = DriverManager.getConnection(jdbc, user, password); Statement statement = connection.createStatement()) {
            String schema = new String(Files.readAllBytes(Paths.get("sql/schema.sql")), StandardCharsets.UTF_8).replaceAll("(?m)^--.*$", "");
            for (String command : schema.split(";")) if (!command.trim().isEmpty()) statement.execute(command);
            statement.executeUpdate("INSERT INTO news(newsID,newsName,newsContent) VALUES('verified-news','已验证新闻','真实测试库新闻内容')");
            statement.executeUpdate("INSERT INTO notice(noticeID,noticeName,noticeContent) VALUES('verified-notice','已验证通知','真实测试库通知内容')");
            statement.executeUpdate("INSERT INTO topmenu(topmenuName,topmenuContent) VALUES('已验证栏目','1')");
            try (PreparedStatement insert = connection.prepareStatement("INSERT INTO leftmenu(leftmenuID,leftmenuName,leftmenuContent) VALUES(?,?,?)")) {
                insert.setString(1,"verified-left"); insert.setString(2,"带引号栏目"); insert.setString(3,"O'Reilly"); insert.executeUpdate();
            }
        }
        Path log = Files.createTempFile(Paths.get("target"), "account-admin-", ".log");
        String classpath = Paths.get("target/campus-news/WEB-INF/classes") + File.pathSeparator
                + Paths.get("target/campus-news/WEB-INF/lib") + File.separator + "*";
        ProcessBuilder create = new ProcessBuilder(Paths.get(System.getProperty("java.home"),"bin","java").toString(),
                "-cp", classpath, "com.citywy.util.AccountAdmin", "--create-admin");
        create.environment().put("NEWS_JDBC_URL", jdbc); create.environment().put("NEWS_DB_USER", user); create.environment().put("NEWS_DB_PASSWORD", password);
        create.environment().put("NEWS_ADMIN_NAME", "verification_admin"); create.environment().put("NEWS_ADMIN_PASSWORD", PASSWORD);
        Process process = create.redirectErrorStream(true).redirectOutput(log.toFile()).start();
        if (!process.waitFor(25, TimeUnit.SECONDS)) { process.destroyForcibly(); fail("创建初始账号的 CLI 超时"); }
        assertEquals("账号创建 CLI 失败，见 " + log, 0, process.exitValue());
    }

    private int verifyDeployment(String context, String jdbc, String user, String password) throws Exception {
        Path base = Files.createTempDirectory(Paths.get("target"), "portal-it-");
        Path upload = Files.createDirectory(base.resolve("uploads"));
        List<String> classpath = new ArrayList<>();
        classpath.add(Paths.get("target/test-classes").toString());
        for (String entry : System.getProperty("surefire.test.class.path").split(java.util.regex.Pattern.quote(File.pathSeparator))) {
            String name = new File(entry).getName();
            if (name.startsWith("tomcat-embed-") || name.startsWith("tomcat-annotations-api-") || name.startsWith("ecj-")) classpath.add(entry);
        }
        ProcessBuilder start = new ProcessBuilder(Paths.get(System.getProperty("java.home"),"bin","java").toString(),
                "-Djava.awt.headless=true", "-Dfile.encoding=" + SERVER_DEFAULT_CHARSET, "-cp", String.join(File.pathSeparator, classpath), WarServer.class.getName(),
                base.toString(), "target/campus-news.war", context);
        start.environment().put("NEWS_JDBC_URL", jdbc); start.environment().put("NEWS_DB_USER", user); start.environment().put("NEWS_DB_PASSWORD", password);
        start.environment().put("NEWS_UPLOAD_DIR", upload.toAbsolutePath().toString());
        start.redirectErrorStream(true).redirectOutput(base.resolve("server.log").toFile());
        Process server = start.start();
        Throwable primaryFailure = null;
        try {
            Path ready = base.resolve("ready.txt");
            long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(45);
            while (!Files.exists(ready) && server.isAlive() && System.nanoTime() < deadline) Thread.sleep(50);
            assertTrue("Tomcat 未就绪，见 " + base.resolve("server.log"), Files.exists(ready));
            int port = Integer.parseInt(new String(Files.readAllBytes(ready), StandardCharsets.UTF_8));
            String origin = "http://127.0.0.1:" + port + context;
            Http client = new Http(origin);
            verifyPublicAndDenied(client);
            Reply login = client.form("/servlets/LoginServlet.htm", "uname=verification_admin&upwd=" + PASSWORD);
            assertEquals(302, login.status); assertEquals(context + "/servlets/index.htm", new URL(new URL(origin), login.location).getPath());
            assertEquals(200, client.get("/servlets/index.htm").status);
            assertEquals(200, client.get("/servlets/news/list.htm").status);
            for (String kind : Arrays.asList("news", "notice", "topmenu", "leftmenu", "user"))
                assertEquals("新建页面必须可用 " + kind, 200, client.get("/servlets/" + kind + "/load.htm").status);
            verifyRolesAndRevocation(client, jdbc, user, password);
            verifyUploads(client, upload);
            Reply add = client.form("/servlets/news/modify.htm", "newsName=" + encode("新增验收新闻") + "&newsContent=" + encode("实际写入") + "&newsID=");
            assertEquals(200, add.status);
            assertTrue(client.get("/api/data?dataType=news").text().contains("新增验收新闻"));
            assertEquals(302, client.get("/servlets/LoginServlet.htm?type=logout").status);
            assertEquals(401, client.get("/admin/index.jsp").status);
            Path browserLog = base.resolve("browser.log");
            Process browser = new ProcessBuilder("node", "tools/browser-smoke.mjs", origin, base.resolve("browser").toString())
                    .redirectErrorStream(true).redirectOutput(browserLog.toFile()).start();
            if (!browser.waitFor(90, TimeUnit.SECONDS)) { browser.destroyForcibly(); fail("浏览器验收超时"); }
            assertEquals("浏览器验收失败，见 " + browserLog, 0, browser.exitValue());
            System.out.println("PORTAL_VERIFIED context=" + context + " port=" + port + " evidence=" + base);
            return port;
        } catch (Exception | AssertionError failure) {
            primaryFailure = failure;
            throw failure;
        } finally {
            try {
                stopServer(server, base);
            } catch (Exception | AssertionError failure) {
                if (primaryFailure == null) throw failure;
                primaryFailure.addSuppressed(failure);
            }
        }
    }

    private static void stopServer(Process server, Path base) throws Exception {
        server.getOutputStream().close();
        if (!server.waitFor(20, TimeUnit.SECONDS)) { server.destroyForcibly(); throw new IllegalStateException("测试 Tomcat 未正常关闭"); }
        assertEquals("Tomcat 退出状态异常，见 " + base.resolve("server.log"), 0, server.exitValue());
        String log = new String(Files.readAllBytes(base.resolve("server.log")), java.nio.charset.Charset.forName(SERVER_DEFAULT_CHARSET));
        assertFalse("停止后不能有连接池线程访问已卸载应用", log.contains("this web application instance has been stopped already"));
        assertFalse("容器不能掩盖线程类加载失败", log.contains("NoClassDefFoundError"));
    }

    private static void verifyPublicAndDenied(Http client) throws Exception {
        for (String file : Arrays.asList("index.html", "index2.html", "newslist.html", "noticelist.html", "js/lunbo.js")) {
            Reply page = client.get("/" + file);
            assertEquals(200, page.status);
            try (java.util.zip.ZipFile war = new java.util.zip.ZipFile("target/campus-news.war");
                 InputStream input = war.getInputStream(war.getEntry(file));
                 ByteArrayOutputStream bytes = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[8192];
                for (int count; (count = input.read(buffer)) != -1;) bytes.write(buffer, 0, count);
                assertArrayEquals("静态响应必须与被部署 WAR 的字节相同 " + file, bytes.toByteArray(), page.body);
            }
        }
        assertTrue(client.get("/api/data?dataType=news").text().contains("已验证新闻"));
        for (String kind : Arrays.asList("news", "notice")) {
            String query = "?type=" + kind + "&id=verified-" + kind;
            Reply detail = client.get("/api/article" + query);
            assertEquals(200, detail.status);
            JsonObject article = JsonParser.parseString(detail.text()).getAsJsonObject();
            assertEquals(kind, article.get("type").getAsString());
            assertEquals("verified-" + kind, article.get("id").getAsString());
            assertTrue(article.get("content").getAsString().startsWith("真实测试库"));
            Reply page = client.get("/article" + query);
            assertEquals(200, page.status);
            assertTrue(page.text().contains(article.get("content").getAsString()));
        }
        for (String route : Arrays.asList("/api/article", "/article")) {
            for (String query : Arrays.asList("", "?type=news", "?type=user&id=1", "?type=news&id=", "?type=news&id=a&id=b",
                    "?type=news&type=notice&id=verified-news", "?type=news&id=" + encode("x' OR 1=1 --"))) {
                assertEquals("正文非法参数必须返回 400 " + query, 400, client.get(route + query).status);
            }
            assertEquals(404, client.get(route + "?type=news&id=missing-record").status);
            assertEquals(404, client.get(route + "?type=notice&id=verified-news").status);
            assertEquals(405, client.form(route, "type=news&id=verified-news").status);
        }
        assertEquals(404, client.get("/WEB-INF/views/article.jsp").status);
        JsonArray menu = JsonParser.parseString(client.get("/api/data?dataType=leftmenu&leftmenuContent=" + encode("O'Reilly")).text()).getAsJsonArray();
        assertEquals(1, menu.size());
        List<String> protectedPaths;
        try (java.util.stream.Stream<Path> files = Files.walk(Paths.get("src/main/webapp/admin"))) {
            protectedPaths = files.filter(p -> p.toString().endsWith(".jsp"))
                    .map(p -> "/" + Paths.get("src/main/webapp").relativize(p).toString().replace('\\','/')).collect(Collectors.toList());
        }
        protectedPaths.addAll(Arrays.asList("/servlets/index.htm", "/servlets/news/list.htm", "/servlets/user/list.htm",
                "/servlets/notice/list.htm", "/servlets/topmenu/list.htm", "/servlets/leftmenu/list.htm"));
        for (String path : protectedPaths) assertEquals("未登录必须拒绝 " + path, 401, client.get(path).status);
        for (String kind : Arrays.asList("news", "notice", "topmenu", "leftmenu", "user"))
            for (String action : Arrays.asList("modify", "drop")) {
                String path = "/servlets/" + kind + "/" + action + ".htm";
                assertEquals("未登录不能写入 " + path, 401, client.form(path, "id=forbidden").status);
                assertEquals("GET 也不能绕过鉴权 " + path, 401, client.get(path).status);
            }
        for (String editor : Arrays.asList("pic", "picedi"))
            assertEquals(401, client.form("/admin/ajax/" + editor + ".htm", "anything=forbidden").status);
    }

    private static void verifyUploads(Http client, Path upload) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ImageIO.write(new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB), "png", bytes);
        byte[] png = bytes.toByteArray();
        for (String route : Arrays.asList("pic.htm", "picedi.htm")) {
            Reply reply = client.multipart("/admin/ajax/" + route, "valid.png", png);
            assertEquals(200, reply.status);
            String url = route.equals("pic.htm") ? reply.text() : JsonParser.parseString(reply.text()).getAsJsonObject().get("url").getAsString();
            Reply image = client.absoluteGet(url);
            assertEquals(200, image.status); assertArrayEquals(png, image.body);
        }
        Reply callback = client.multipart("/admin/ajax/picedi.htm?CKEditorFuncNum=7", "callback.png", png);
        assertEquals(200, callback.status); assertTrue(callback.text().contains("callFunction(7,"));
        assertEquals(400, client.multipart("/admin/ajax/picedi.htm?CKEditorFuncNum=bad", "callback.png", png).status);
        assertEquals(400, client.multipart("/admin/ajax/pic.htm", "../escape.png", png).status);
        assertEquals(415, client.multipart("/admin/ajax/pic.htm", "fake.png", "fake".getBytes(StandardCharsets.UTF_8)).status);
        assertEquals(413, client.multipart("/admin/ajax/pic.htm", "large.png", new byte[1024 * 1024 + 1]).status);
        try (java.util.stream.Stream<Path> files = Files.list(upload)) { assertEquals(3, files.count()); }
    }

    private static void verifyRolesAndRevocation(Http manager, String jdbc, String dbUser, String dbPassword) throws Exception {
        String name = "verification_editor";
        assertEquals(200, manager.form("/servlets/user/modify.htm", accountForm(0, name, "用户", "有效", PASSWORD)).status);
        int id;
        try (Connection connection = DriverManager.getConnection(jdbc, dbUser, dbPassword);
             PreparedStatement query = connection.prepareStatement("SELECT id FROM `user` WHERE name = ?")) {
            query.setString(1, name);
            try (ResultSet rows = query.executeQuery()) { assertTrue(rows.next()); id = rows.getInt(1); assertFalse(rows.next()); }
        }
        Http editor = new Http(manager.base);
        assertEquals(302, editor.form("/servlets/LoginServlet.htm", "uname=" + name + "&upwd=" + PASSWORD).status);
        Reply dashboard = editor.get("/servlets/index.htm");
        assertEquals(200, dashboard.status);
        assertFalse(dashboard.text().contains("/servlets/user/list.htm"));
        assertEquals(200, editor.form("/servlets/news/modify.htm",
                "newsID=&newsName=" + encode("普通编辑已验证") + "&newsContent=" + encode("保留内容编辑能力")).status);
        for (String action : Arrays.asList("list", "load", "modify", "drop", "view")) {
            String path = "/servlets/user/" + action + ".htm";
            assertEquals("普通用户不能通过 GET 管理账号", 403, editor.get(path + "?id=" + id).status);
            assertEquals("普通用户不能通过 POST 修改自身角色", 403,
                    editor.form(path, accountForm(id, name, "管理员", "有效", "")).status);
        }
        assertEquals(403, editor.get("/admin/user/user_load.jsp").status);
        assertEquals(403, editor.get("/admin/user/user_list.jsp").status);
        try (Connection connection = DriverManager.getConnection(jdbc, dbUser, dbPassword);
             PreparedStatement query = connection.prepareStatement("SELECT role, valid FROM `user` WHERE id = ?")) {
            query.setInt(1, id);
            try (ResultSet rows = query.executeQuery()) { assertTrue(rows.next()); assertEquals("用户", rows.getString(1)); assertEquals("有效", rows.getString(2)); }
        }
        assertEquals(200, manager.form("/servlets/user/modify.htm", accountForm(id, name, "用户", "无效", "")).status);
        assertEquals("停用必须撤销既有会话", 401, editor.get("/servlets/news/list.htm").status);
        assertEquals(401, editor.form("/servlets/LoginServlet.htm", "uname=" + name + "&upwd=" + PASSWORD).status);
        assertEquals(200, manager.form("/servlets/user/modify.htm", accountForm(id, name, "用户", "有效", "")).status);
        assertEquals("恢复账号不能复活被撤销的会话", 401, editor.get("/admin/index.jsp").status);
        assertEquals(302, editor.form("/servlets/LoginServlet.htm", "uname=" + name + "&upwd=" + PASSWORD).status);
        assertEquals(200, manager.form("/servlets/user/modify.htm", accountForm(id, name, "管理员", "有效", "")).status);
        assertEquals(200, editor.get("/servlets/user/list.htm").status);
        assertEquals(200, manager.form("/servlets/user/modify.htm", accountForm(id, name, "用户", "有效", "")).status);
        assertEquals("降权必须立即影响既有会话", 403, editor.get("/servlets/user/list.htm").status);
        assertEquals(200, editor.get("/servlets/news/list.htm").status);
        assertEquals(302, manager.get("/servlets/user/drop.htm?id=" + id).status);
        assertEquals("删除必须撤销既有会话", 401, editor.get("/servlets/news/list.htm").status);
    }

    private static String accountForm(int id, String name, String role, String valid, String password) throws Exception {
        return "id=" + id + "&name=" + encode(name) + "&uname=" + encode("测试编辑") + "&role=" + encode(role)
                + "&valid=" + encode(valid) + "&password=" + encode(password);
    }

    private static String encode(String value) throws Exception { return URLEncoder.encode(value, "UTF-8"); }
    private static final class Reply {
        final int status; final byte[] body; final String location;
        Reply(int status, byte[] body, String location) { this.status=status; this.body=body; this.location=location; }
        String text() { return new String(body, StandardCharsets.UTF_8); }
    }
    private static final class Http {
        final String base; String cookie;
        Http(String base) { this.base=base; }
        Reply get(String path) throws Exception { return request(new URL(base+path), "GET", null, null); }
        Reply absoluteGet(String path) throws Exception { return request(new URL(new URL(base), path), "GET", null, null); }
        Reply form(String path, String body) throws Exception { return request(new URL(base+path), "POST", "application/x-www-form-urlencoded", body.getBytes(StandardCharsets.UTF_8)); }
        Reply multipart(String path, String name, byte[] bytes) throws Exception {
            String boundary="verification-boundary";
            ByteArrayOutputStream body=new ByteArrayOutputStream();
            body.write(("--"+boundary+"\r\nContent-Disposition: form-data; name=\"upload\"; filename=\""+name+"\"\r\nContent-Type: image/png\r\n\r\n").getBytes(StandardCharsets.UTF_8));
            body.write(bytes); body.write(("\r\n--"+boundary+"--\r\n").getBytes(StandardCharsets.UTF_8));
            return request(new URL(base+path),"POST","multipart/form-data; boundary="+boundary,body.toByteArray());
        }
        Reply request(URL url, String method, String type, byte[] body) throws Exception {
            HttpURLConnection connection=(HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(5000); connection.setReadTimeout(10000); connection.setInstanceFollowRedirects(false); connection.setRequestMethod(method);
            if(cookie!=null)connection.setRequestProperty("Cookie",cookie);
            if(body!=null){connection.setDoOutput(true);connection.setRequestProperty("Content-Type",type);try(OutputStream out=connection.getOutputStream()){out.write(body);}}
            int status=connection.getResponseCode();
            String setCookie=connection.getHeaderField("Set-Cookie");if(setCookie!=null)cookie=setCookie.split(";",2)[0];
            InputStream input=status>=400?connection.getErrorStream():connection.getInputStream();
            ByteArrayOutputStream bytes=new ByteArrayOutputStream();
            if(input!=null)try(InputStream stream=input){byte[] buffer=new byte[8192];for(int count;(count=stream.read(buffer))!=-1;)bytes.write(buffer,0,count);}
            Reply reply=new Reply(status,bytes.toByteArray(),connection.getHeaderField("Location"));connection.disconnect();return reply;
        }
    }
}
