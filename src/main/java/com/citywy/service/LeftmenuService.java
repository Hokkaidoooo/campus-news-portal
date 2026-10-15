package com.citywy.service;

import com.citywy.domain.Leftmenu;
import com.citywy.util.DBConnectionPool;
import javax.sql.DataSource;
import java.util.Date;
import java.util.List;

public final class LeftmenuService extends ContentService<Leftmenu> {
    public LeftmenuService() { this(DBConnectionPool.getInstance().getDataSource()); }
    public LeftmenuService(DataSource source) { super(source, "leftmenu", false); }
    public Leftmenu get(String id) { return find(id); }
    public int getRowCount(String condition) { return count(condition); }
    public List<Leftmenu> getLeftmenuList(String condition, int pageNow, int pageSize, String order, String sort) {
        return page(condition, pageNow, pageSize, order, sort);
    }
    public List<Leftmenu> getLeftmenuByContent(String content, String order, String sort) { return byContent(content, order, sort); }
    public boolean addLeftmenu(Leftmenu value) { return add(value.getLeftmenuName(), value.getLeftmenuContent()); }
    public boolean updLeftmenu(Leftmenu value) { return update(value.getLeftmenuID(), value.getLeftmenuName(), value.getLeftmenuContent()); }
    public boolean delLeftmenu(String id) { return delete(id); }

    @Override protected Leftmenu map(String id, String name, String content, Date created) {
        Leftmenu value = new Leftmenu();
        value.setLeftmenuID(id); value.setLeftmenuName(name); value.setLeftmenuContent(content); value.setCreateTime(created);
        return value;
    }
}
