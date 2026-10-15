package com.citywy.service;

import com.citywy.domain.User;
import com.citywy.util.DBConnectionPool;
import com.citywy.util.DBUtil;
import com.citywy.util.PasswordHashes;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class UserService {
    private static final String PUBLIC_COLUMNS = "id, name, uname, role, valid";
    private final DBUtil database;

    public UserService() { this(DBConnectionPool.getInstance().getDataSource()); }
    public UserService(DataSource source) { database = new DBUtil(source); }

    public boolean UserLogin(User credentials) throws SQLException {
        if (credentials == null || credentials.getName() == null || credentials.getPassword() == null) return false;
        List<Map<String, Object>> users = database.executeQuery("SELECT password, valid FROM `user` WHERE name = ?", credentials.getName());
        if (users.isEmpty()) return false;
        if (users.size() != 1) throw new IllegalStateException("Duplicate login identity");
        Map<String, Object> user = users.get(0);
        if (!"有效".equals(user.get("valid"))) return false;
        return PasswordHashes.verify(credentials.getPassword(), (String) user.get("password"));
    }

    public User getUserByName(String name) {
        return one(database.executeQuery("SELECT " + PUBLIC_COLUMNS + " FROM `user` WHERE name = ?", name));
    }

    public User get(long id) {
        requireId(id);
        return one(database.executeQuery("SELECT " + PUBLIC_COLUMNS + " FROM `user` WHERE id = ?", id));
    }

    public int getRowCount(String condition) {
        return countSearch(Search.keyword(condition));
    }

    public int countFiltered(Map<String, String> filters) {
        return countSearch(Search.filters(filters));
    }

    private int countSearch(Search search) {
        List<Map<String, Object>> rows = database.executeQuery("SELECT COUNT(*) AS total FROM `user`" + search.clause, search.parameters.toArray());
        return Math.toIntExact(((Number) rows.get(0).get("total")).longValue());
    }

    public List<User> getUserList(String condition, int pageNow, int pageSize, String order, String sort) {
        return listSearch(Search.keyword(condition), pageNow, pageSize, order, sort);
    }

    public List<User> listFiltered(Map<String, String> filters, int pageNow, int pageSize, String order, String sort) {
        return listSearch(Search.filters(filters), pageNow, pageSize, order, sort);
    }

    private List<User> listSearch(Search search, int pageNow, int pageSize, String order, String sort) {
        if (pageNow < 1 || pageSize < 1) throw new IllegalArgumentException("Page and size must be positive");
        String sql = "SELECT " + PUBLIC_COLUMNS + " FROM `user`" + search.clause
                + DBUtil.orderBy(order, sort, "id", "id", "name", "uname", "role", "valid") + " LIMIT ? OFFSET ?";
        List<Object> parameters = new ArrayList<Object>(search.parameters);
        parameters.add(pageSize); parameters.add(Math.multiplyExact(pageNow - 1, pageSize));
        List<Map<String, Object>> rows = database.executeQuery(sql, parameters.toArray());
        List<User> users = new ArrayList<User>();
        for (Map<String, Object> row : rows) users.add(map(row));
        return users;
    }

    public boolean addUser(User user) {
        requireUser(user);
        String hash = PasswordHashes.hash(user.getPassword());
        return database.executeUpdate("INSERT INTO `user` (name, uname, password, role, valid) VALUES (?, ?, ?, ?, ?)",
                user.getName(), user.getUname(), hash, user.getRole(), user.getValid()) == 1;
    }

    public boolean updUser(User user) {
        requireUser(user); requireId(user.getId());
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            return database.executeUpdate("UPDATE `user` SET name = ?, uname = ?, role = ?, valid = ? WHERE id = ?",
                    user.getName(), user.getUname(), user.getRole(), user.getValid(), user.getId()) == 1;
        }
        return database.executeUpdate("UPDATE `user` SET name = ?, uname = ?, password = ?, role = ?, valid = ? WHERE id = ?",
                user.getName(), user.getUname(), PasswordHashes.hash(user.getPassword()), user.getRole(), user.getValid(), user.getId()) == 1;
    }

    public boolean delUser(long id) {
        requireId(id);
        return database.executeUpdate("DELETE FROM `user` WHERE id = ?", id) == 1;
    }

    private User one(List<Map<String, Object>> rows) {
        if (rows.isEmpty()) return null;
        if (rows.size() != 1) throw new IllegalStateException("User identity is not unique");
        return map(rows.get(0));
    }

    private User map(Map<String, Object> row) {
        User user = new User();
        user.setId(((Number) row.get("id")).intValue());
        user.setName((String) row.get("name")); user.setUname((String) row.get("uname"));
        user.setRole((String) row.get("role")); user.setValid((String) row.get("valid"));
        return user;
    }

    private static final class Search {
        private static final List<String> FILTER_COLUMNS = java.util.Arrays.asList("role", "name", "uname");
        private final String clause;
        private final List<Object> parameters;

        private Search(String clause, List<Object> parameters) { this.clause = clause; this.parameters = parameters; }

        private static Search keyword(String keyword) {
            if (keyword == null || keyword.isEmpty()) return new Search("", java.util.Collections.emptyList());
            Object term = DBUtil.containsLiteral(keyword);
            return new Search(" WHERE (name LIKE ? ESCAPE '!' OR uname LIKE ? ESCAPE '!')", java.util.Arrays.asList(term, term));
        }

        private static Search filters(Map<String, String> filters) {
            if (filters == null) throw new IllegalArgumentException("Filter map is required");
            for (String column : filters.keySet()) {
                if (!FILTER_COLUMNS.contains(column)) throw new IllegalArgumentException("Unsupported user filter column");
            }
            List<String> clauses = new ArrayList<String>();
            List<Object> parameters = new ArrayList<Object>();
            for (String column : FILTER_COLUMNS) {
                String term = filters.get(column);
                if (term == null || term.isEmpty()) continue;
                clauses.add("`" + column + "` LIKE ? ESCAPE '!'");
                parameters.add(DBUtil.containsLiteral(term));
            }
            return new Search(clauses.isEmpty() ? "" : " WHERE " + String.join(" AND ", clauses), parameters);
        }
    }

    private void requireId(long id) { if (id < 1) throw new IllegalArgumentException("User ID must be positive"); }
    private void requireUser(User user) {
        if (user == null || user.getName() == null || user.getName().isEmpty()) throw new IllegalArgumentException("User name is required");
    }
}
