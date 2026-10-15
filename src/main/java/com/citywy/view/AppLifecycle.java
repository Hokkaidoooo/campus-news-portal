package com.citywy.view;

import com.citywy.storage.ImageStorage;
import com.citywy.util.DBConnectionPool;
import java.nio.file.*;
import java.sql.Connection;
import javax.servlet.*;

public final class AppLifecycle implements ServletContextListener {
    @Override public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        String configured = System.getProperty("NEWS_UPLOAD_DIR", System.getenv("NEWS_UPLOAD_DIR"));
        if (configured == null || configured.trim().isEmpty()) throw new IllegalStateException("必须配置 NEWS_UPLOAD_DIR 独立上传目录");
        Path upload = Paths.get(configured).toAbsolutePath().normalize();
        String webRoot = context.getRealPath("/");
        if (webRoot != null && upload.startsWith(Paths.get(webRoot).toAbsolutePath().normalize()))
            throw new IllegalStateException("上传目录必须位于应用部署目录之外");
        try {
            context.setAttribute(ImageStorage.CONTEXT_KEY, new ImageStorage(upload));
            try (Connection connection = DBConnectionPool.getInstance().getDataSource().getConnection()) {
                if (!connection.isValid(3)) throw new IllegalStateException("数据库连接验证失败");
            }
        } catch (Exception error) {
            DBConnectionPool.shutdown();
            throw new IllegalStateException("应用初始化失败", error);
        }
    }
    @Override public void contextDestroyed(ServletContextEvent event) { DBConnectionPool.shutdown(); }
}
