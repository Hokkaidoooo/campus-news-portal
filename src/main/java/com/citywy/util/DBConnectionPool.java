package com.citywy.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

/** 应用只在此处创建数据源；服务和测试通过 DataSource 接口注入。 */
public final class DBConnectionPool {
    private static final long DRIVER_SHUTDOWN_TIMEOUT_MS = 3000;
    private static DBConnectionPool instance;
    private static volatile boolean jdbcInitialized;
    private final HikariDataSource dataSource;

    private DBConnectionPool() {
        dataSource = createDataSource(setting("NEWS_JDBC_URL", false),
                setting("NEWS_DB_USER", false), setting("NEWS_DB_PASSWORD", true));
    }

    public static synchronized DBConnectionPool getInstance() {
        if (instance == null) instance = new DBConnectionPool();
        return instance;
    }

    public DataSource getDataSource() { return dataSource; }
    public Connection getConnection() throws SQLException { return dataSource.getConnection(); }

    public static synchronized void shutdown() {
        try {
            if (instance != null) instance.dataSource.close();
        } catch (RuntimeException | Error failure) {
            try { releaseJdbcResources(); }
            catch (RuntimeException | Error cleanupFailure) { failure.addSuppressed(cleanupFailure); }
            throw failure;
        }
        // Hikari close 已等待连接关闭完成，之后才能卸载本应用的驱动资源。
        releaseJdbcResources();
        instance = null;
    }

    public static HikariDataSource createDataSource(String url, String user, String password) {
        if (url == null || !url.startsWith("jdbc:mysql://") || user == null || user.trim().isEmpty() || password == null) {
            throw new IllegalArgumentException("MySQL URL, user and explicit password are required");
        }
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        jdbcInitialized = true;
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);
        config.setMinimumIdle(1);
        config.setMaximumPoolSize(10);
        config.setConnectionTimeout(5000);
        config.setValidationTimeout(3000);
        config.setInitializationFailTimeout(5000);
        config.setIdleTimeout(300000);
        return new HikariDataSource(config);
    }

    private static void releaseJdbcResources() {
        if (!jdbcInitialized) return;
        ClassLoader owner = DBConnectionPool.class.getClassLoader();
        if (AbandonedConnectionCleanupThread.class.getClassLoader() == owner) stopDriverCleanup();
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            if (driver.getClass().getClassLoader() != owner) continue;
            try { DriverManager.deregisterDriver(driver); }
            catch (SQLException failure) { throw new IllegalStateException("Cannot deregister application JDBC driver", failure); }
        }
        jdbcInitialized = false;
    }

    private static void stopDriverCleanup() {
        AbandonedConnectionCleanupThread.checkedShutdown();
        long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(DRIVER_SHUTDOWN_TIMEOUT_MS);
        while (AbandonedConnectionCleanupThread.isAlive()) {
            if (System.nanoTime() >= deadline) throw new IllegalStateException("MySQL cleanup did not terminate before application shutdown");
            try { Thread.sleep(10); }
            catch (InterruptedException interrupted) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrupted while waiting for MySQL cleanup", interrupted);
            }
        }
    }

    private static String setting(String name, boolean allowEmpty) {
        String value = System.getProperty(name);
        if (value == null) value = System.getenv(name);
        if (value == null || (!allowEmpty && value.trim().isEmpty())) {
            throw new IllegalStateException("Missing database setting: " + name);
        }
        return value;
    }
}
