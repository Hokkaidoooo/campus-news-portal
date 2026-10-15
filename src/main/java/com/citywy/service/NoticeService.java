package com.citywy.service;

import com.citywy.domain.Notice;
import com.citywy.util.DBConnectionPool;
import javax.sql.DataSource;
import java.util.Date;
import java.util.List;

public final class NoticeService extends ContentService<Notice> {
    public NoticeService() { this(DBConnectionPool.getInstance().getDataSource()); }
    public NoticeService(DataSource source) { super(source, "notice", false); }
    public Notice get(String id) { return find(id); }
    public int getRowCount(String condition) { return count(condition); }
    public List<Notice> getNoticeList(String condition, int pageNow, int pageSize, String order, String sort) {
        return page(condition, pageNow, pageSize, order, sort);
    }
    public List<Notice> getNoticeAll(String condition, String order, String sort) { return all(condition, order, sort); }
    public boolean addNotice(Notice value) { return add(value.getNoticeName(), value.getNoticeContent()); }
    public boolean updNotice(Notice value) { return update(value.getNoticeID(), value.getNoticeName(), value.getNoticeContent()); }
    public boolean delNotice(String id) { return delete(id); }

    @Override protected Notice map(String id, String name, String content, Date created) {
        Notice value = new Notice();
        value.setNoticeID(id); value.setNoticeName(name); value.setNoticeContent(content); value.setCreateTime(created);
        return value;
    }
}
