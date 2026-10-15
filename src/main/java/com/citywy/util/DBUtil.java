package com.citywy.util;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** 每次调用持有并归还自己的 JDBC 资源，不共享 Connection、Statement 或 ResultSet。 */
public final class DBUtil {
    private final DataSource dataSource;

    public DBUtil(DataSource dataSource) { this.dataSource = Objects.requireNonNull(dataSource, "dataSource"); }

    public List<Map<String, Object>> executeQuery(String sql, Object... parameters) {
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, parameters);
            try (ResultSet rows = statement.executeQuery()) {
                List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
                ResultSetMetaData metadata = rows.getMetaData();
                while (rows.next()) {
                    Map<String, Object> row = new LinkedHashMap<String, Object>();
                    for (int column = 1; column <= metadata.getColumnCount(); column++) {
                        row.put(metadata.getColumnLabel(column), columnValue(rows, metadata, column));
                    }
                    result.add(row);
                }
                return result;
            }
        } catch (SQLException failure) {
            throw new DatabaseException("query", failure);
        }
    }

    public int executeUpdate(String sql, Object... parameters) {
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, parameters);
            return statement.executeUpdate();
        } catch (SQLException failure) {
            throw new DatabaseException("write", failure);
        }
    }

    private static void bind(PreparedStatement statement, Object[] parameters) throws SQLException {
        if (parameters == null) throw new IllegalArgumentException("Use an empty parameter array instead of null");
        for (int index = 0; index < parameters.length; index++) statement.setObject(index + 1, parameters[index]);
    }

    private static Object columnValue(ResultSet rows, ResultSetMetaData metadata, int column) throws SQLException {
        // 统一 JDBC 边界，避免不同 MySQL 驱动把同一 DATETIME 映射为不同 Java 类型。
        if (metadata.getColumnType(column) == java.sql.Types.TIMESTAMP) return rows.getTimestamp(column);
        if (metadata.getColumnType(column) == java.sql.Types.DATE) return rows.getDate(column);
        return rows.getObject(column);
    }

    public static String containsLiteral(String term) {
        return "%" + term.replace("!", "!!").replace("%", "!%").replace("_", "!_") + "%";
    }

    public static String orderBy(String order, String direction, String primaryKey, String... allowedColumns) {
        boolean allowed = false;
        for (String column : allowedColumns) if (column.equals(order)) allowed = true;
        if (!allowed) throw new IllegalArgumentException("Unsupported sort column");
        if (!"ASC".equalsIgnoreCase(direction) && !"DESC".equalsIgnoreCase(direction)) {
            throw new IllegalArgumentException("Sort direction must be ASC or DESC");
        }
        String result = " ORDER BY `" + order + "` " + direction.toUpperCase(java.util.Locale.ROOT);
        return primaryKey.equals(order) ? result : result + ", `" + primaryKey + "` ASC";
    }
}
