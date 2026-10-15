package com.citywy.service;

import com.citywy.domain.Topmenu;
import com.citywy.util.DBConnectionPool;
import javax.sql.DataSource;
import java.util.Date;
import java.util.List;

public final class TopmenuService extends ContentService<Topmenu> {
    public TopmenuService() { this(DBConnectionPool.getInstance().getDataSource()); }
    public TopmenuService(DataSource source) { super(source, "topmenu", true); }
    public Topmenu get(String id) { return find(id); }
    public int getRowCount(String condition) { return count(condition); }
    public List<Topmenu> getTopmenuList(String condition, int pageNow, int pageSize, String order, String sort) {
        return page(condition, pageNow, pageSize, order, sort);
    }
    public List<Topmenu> getTopmenuAll(String condition, String order, String sort) { return all(condition, order, sort); }
    public boolean addTopmenu(Topmenu value) { return add(value.getTopmenuName(), value.getTopmenuContent()); }
    public boolean updTopmenu(Topmenu value) { return update(value.getId(), value.getTopmenuName(), value.getTopmenuContent()); }
    public boolean delTopmenu(String id) { return delete(id); }

    @Override protected Topmenu map(String id, String name, String content, Date created) {
        Topmenu value = new Topmenu();
        value.setId(id); value.setTopmenuName(name); value.setTopmenuContent(content); value.setCreateTime(created);
        return value;
    }
}
