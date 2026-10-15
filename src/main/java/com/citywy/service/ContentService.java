package com.citywy.service;

import com.citywy.util.DBUtil;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** 四类内容共用查询、排序、分页和写入契约；具体服务只映射域对象。 */
abstract class ContentService<T> {
    private final DBUtil database;
    private final String table;
    private final String idColumn;
    private final String nameColumn;
    private final String contentColumn;
    private final boolean generatedId;

    ContentService(DataSource source, String table, boolean generatedId) {
        this.database = new DBUtil(source);
        this.table = table;
        this.generatedId = generatedId;
        this.idColumn = generatedId ? "id" : table + "ID";
        this.nameColumn = table + "Name";
        this.contentColumn = table + "Content";
    }

    protected abstract T map(String id, String name, String content, Date created);

    protected final T find(String id) {
        requireId(id);
        List<T> rows = mapRows(database.executeQuery(select() + " WHERE `" + idColumn + "` = ?", id));
        return rows.isEmpty() ? null : rows.get(0);
    }

    protected final int count(String keyword) {
        String sql = "SELECT COUNT(*) AS total FROM `" + table + "`";
        List<Map<String, Object>> rows = hasKeyword(keyword)
                ? database.executeQuery(sql + keywordClause(), DBUtil.containsLiteral(keyword)) : database.executeQuery(sql);
        return Math.toIntExact(((Number) rows.get(0).get("total")).longValue());
    }

    protected final List<T> page(String keyword, int page, int size, String order, String direction) {
        if (page < 1 || size < 1) throw new IllegalArgumentException("Page and size must be positive");
        int offset = Math.multiplyExact(page - 1, size);
        String sql = select() + (hasKeyword(keyword) ? keywordClause() : "") + ordering(order, direction) + " LIMIT ? OFFSET ?";
        return mapRows(hasKeyword(keyword) ? database.executeQuery(sql, DBUtil.containsLiteral(keyword), size, offset)
                : database.executeQuery(sql, size, offset));
    }

    protected final List<T> all(String keyword, String order, String direction) {
        String sql = select() + (hasKeyword(keyword) ? keywordClause() : "") + ordering(order, direction);
        return mapRows(hasKeyword(keyword) ? database.executeQuery(sql, DBUtil.containsLiteral(keyword)) : database.executeQuery(sql));
    }

    protected final List<T> byContent(String content, String order, String direction) {
        if (content == null) throw new IllegalArgumentException("Content value is required");
        return mapRows(database.executeQuery(select() + " WHERE `" + contentColumn + "` = ?" + ordering(order, direction), content));
    }

    protected final boolean add(String name, String content) {
        requireContent(name, content);
        if (generatedId) {
            return database.executeUpdate("INSERT INTO `" + table + "` (`" + nameColumn + "`, `" + contentColumn + "`) VALUES (?, ?)", name, content) == 1;
        }
        return database.executeUpdate("INSERT INTO `" + table + "` (`" + idColumn + "`, `" + nameColumn + "`, `" + contentColumn + "`) VALUES (?, ?, ?)",
                UUID.randomUUID().toString().replace("-", ""), name, content) == 1;
    }

    protected final boolean update(String id, String name, String content) {
        requireId(id); requireContent(name, content);
        return database.executeUpdate("UPDATE `" + table + "` SET `" + nameColumn + "` = ?, `" + contentColumn + "` = ? WHERE `" + idColumn + "` = ?", name, content, id) == 1;
    }

    protected final boolean delete(String id) {
        requireId(id);
        return database.executeUpdate("DELETE FROM `" + table + "` WHERE `" + idColumn + "` = ?", id) == 1;
    }

    private String select() {
        return "SELECT `" + idColumn + "` AS id, `" + nameColumn + "` AS name, `" + contentColumn + "` AS content, createTime FROM `" + table + "`";
    }
    private String keywordClause() { return " WHERE `" + nameColumn + "` LIKE ? ESCAPE '!'"; }
    private String ordering(String order, String direction) { return DBUtil.orderBy(order, direction, idColumn, idColumn, nameColumn, contentColumn, "createTime"); }
    private boolean hasKeyword(String keyword) { return keyword != null && !keyword.isEmpty(); }
    private void requireId(String id) { if (id == null || id.isEmpty()) throw new IllegalArgumentException("Content ID is required"); }
    private void requireContent(String name, String content) { if (name == null || content == null) throw new IllegalArgumentException("Name and content are required"); }

    private List<T> mapRows(List<Map<String, Object>> rows) {
        List<T> result = new ArrayList<T>();
        for (Map<String, Object> row : rows) result.add(map(row.get("id").toString(), (String) row.get("name"), (String) row.get("content"), (Date) row.get("createTime")));
        return result;
    }
}
