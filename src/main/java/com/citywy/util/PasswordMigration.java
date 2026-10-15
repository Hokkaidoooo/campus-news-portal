package com.citywy.util;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** 只通过显式离线命令执行；应用启动和登录均不会自动升级旧明文。 */
public final class PasswordMigration {
    private PasswordMigration() { }

    public static void main(String[] arguments) throws SQLException {
        if (arguments.length != 1 || !"--migrate-plaintext".equals(arguments[0])) {
            throw new IllegalArgumentException("显式执行：PasswordMigration --migrate-plaintext");
        }
        try {
            int converted = migrate(DBConnectionPool.getInstance().getDataSource());
            System.out.println("密码迁移完成，已转换 " + converted + " 个账户；不输出密码或哈希。");
        } finally { DBConnectionPool.shutdown(); }
    }

    public static int migrate(DataSource source) throws SQLException {
        try (Connection connection = source.getConnection()) {
            // MySQL DDL 会独立提交。先扩容，密码数据更新另行放进一个事务。
            try (Statement statement = connection.createStatement()) {
                statement.execute("ALTER TABLE `user` MODIFY COLUMN password VARCHAR(255) NOT NULL");
            }
            connection.setAutoCommit(false);
            try {
                List<PasswordRow> pending = collect(connection);
                try (PreparedStatement update = connection.prepareStatement("UPDATE `user` SET password = ? WHERE id = ?")) {
                    for (PasswordRow row : pending) {
                        update.setString(1, PasswordHashes.hash(row.password)); update.setLong(2, row.id);
                        if (update.executeUpdate() != 1) throw new SQLException("Password migration lost a locked user row");
                    }
                }
                connection.commit();
                return pending.size();
            } catch (SQLException | RuntimeException failure) {
                try { connection.rollback(); } catch (SQLException rollback) { failure.addSuppressed(rollback); }
                throw failure;
            }
        }
    }

    private static List<PasswordRow> collect(Connection connection) throws SQLException {
        List<PasswordRow> pending = new ArrayList<PasswordRow>();
        try (PreparedStatement query = connection.prepareStatement("SELECT id, password FROM `user` FOR UPDATE"); ResultSet rows = query.executeQuery()) {
            while (rows.next()) {
                String password = rows.getString("password");
                if (PasswordHashes.looksEncoded(password)) PasswordHashes.requireSupportedHash(password);
                else pending.add(new PasswordRow(rows.getLong("id"), password));
            }
        }
        return pending;
    }

    private static final class PasswordRow {
        private final long id;
        private final String password;
        private PasswordRow(long id, String password) { this.id = id; this.password = password; }
    }
}
