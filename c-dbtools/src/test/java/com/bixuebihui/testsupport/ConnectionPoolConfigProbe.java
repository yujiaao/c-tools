package com.bixuebihui.testsupport;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 在 {@code test-compile} 阶段由 exec-maven-plugin 调用：探测本机 MySQL 是否可用，
 * 将 {@link com.bixuebihui.ConnectionManager} 使用的 {@code ConnectionPoolManager.xml}
 * 选为 MySQL 或 H2 模板副本。
 * <p>
 * Maven 属性：{@code connection.pool.profile} = auto | mysql | h2（模块默认 h2，且默认跳过本 main，见 pom）；
 * {@code testOutputDirectory} 由 Maven 传入，指向 {@code target/test-classes}。
 * </p>
 */
public final class ConnectionPoolConfigProbe {

    private ConnectionPoolConfigProbe() {
    }

    public static void main(String[] args) throws Exception {
        String outDir = args.length > 0 ? args[0] : System.getProperty("testOutputDirectory");
        if (outDir == null || outDir.isBlank()) {
            throw new IllegalStateException("Pass test output dir as arg[0] or set testOutputDirectory");
        }
        Path testClasses = Path.of(outDir);
        String profile = (args.length > 1 ? args[1] : System.getProperty("connection.pool.profile", "auto"))
                .trim()
                .toLowerCase();

        String chosen;
        switch (profile) {
            case "mysql":
                chosen = "mysql";
                break;
            case "h2":
                chosen = "h2";
                break;
            case "auto":
                chosen = probeMysqlAvailable(testClasses) ? "mysql" : "h2";
                break;
            default:
                System.err.println("[ConnectionPoolConfigProbe] unknown connection.pool.profile=" + profile + ", using auto");
                chosen = probeMysqlAvailable(testClasses) ? "mysql" : "h2";
        }

        String suffix = "mysql".equals(chosen) ? "mysql" : "h2";
        Path src = testClasses.resolve("ConnectionPoolManager-" + suffix + ".xml");
        Path dst = testClasses.resolve("ConnectionPoolManager.xml");
        if (!Files.isRegularFile(src)) {
            throw new IOException("Missing template: " + src);
        }
        Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("[ConnectionPoolConfigProbe] selected=" + chosen + " -> wrote " + dst.toAbsolutePath());
    }

    private static boolean probeMysqlAvailable(Path testClasses) throws IOException, ClassNotFoundException {
        Properties p = new Properties();
        Path propFile = testClasses.resolve("connection-pool-probe.properties");
        if (!Files.isRegularFile(propFile)) {
            try (InputStream in = ConnectionPoolConfigProbe.class.getClassLoader()
                    .getResourceAsStream("connection-pool-probe.properties")) {
                if (in == null) {
                    System.out.println("[ConnectionPoolConfigProbe] no connection-pool-probe.properties, fallback h2");
                    return false;
                }
                p.load(in);
            }
        } else {
            try (InputStream in = Files.newInputStream(propFile)) {
                p.load(in);
            }
        }

        String url = p.getProperty("mysql.url");
        String user = p.getProperty("mysql.user");
        String password = p.getProperty("mysql.password");
        if (url == null || user == null) {
            System.out.println("[ConnectionPoolConfigProbe] incomplete mysql.* in properties, fallback h2");
            return false;
        }

        Class.forName("com.mysql.cj.jdbc.Driver");
        DriverManager.setLoginTimeout(2);
        try (Connection ignored = DriverManager.getConnection(url, user, password == null ? "" : password)) {
            System.out.println("[ConnectionPoolConfigProbe] MySQL probe: OK");
            return true;
        } catch (SQLException e) {
            System.out.println("[ConnectionPoolConfigProbe] MySQL probe: failed (" + e.getMessage() + "), use h2");
            return false;
        }
    }
}
