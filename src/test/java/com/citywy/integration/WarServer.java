package com.citywy.integration;

import org.apache.catalina.startup.Tomcat;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

/** 独立 JVM 只带容器依赖；应用类和运行依赖必须由同一个 WAR 提供。 */
public final class WarServer {
    public static void main(String[] args) throws Exception {
        Path base = Paths.get(args[0]).toAbsolutePath();
        Tomcat server = new Tomcat();
        server.setBaseDir(base.toString());
        server.getHost().setAppBase(Files.createDirectories(base.resolve("webapps")).toString());
        server.setPort(0); server.getConnector();
        server.addWebapp(args[2], Paths.get(args[1]).toAbsolutePath().toString());
        try {
            server.start();
            Path pending = base.resolve("ready.pending");
            Files.write(pending, Integer.toString(server.getConnector().getLocalPort()).getBytes(StandardCharsets.UTF_8));
            Files.move(pending, base.resolve("ready.txt"), StandardCopyOption.ATOMIC_MOVE);
            while (System.in.read() != -1) { }
        } finally { server.stop(); server.destroy(); }
    }
}
