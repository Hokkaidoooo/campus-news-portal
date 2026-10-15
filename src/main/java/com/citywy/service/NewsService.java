package com.citywy.service;

import com.citywy.domain.News;
import com.citywy.util.DBConnectionPool;
import javax.sql.DataSource;
import java.util.Date;
import java.util.List;

public final class NewsService extends ContentService<News> {
    public NewsService() { this(DBConnectionPool.getInstance().getDataSource()); }
    public NewsService(DataSource source) { super(source, "news", false); }
    public News get(String id) { return find(id); }
    public int getRowCount(String condition) { return count(condition); }
    public List<News> getNewsList(String condition, int pageNow, int pageSize, String order, String sort) {
        return page(condition, pageNow, pageSize, order, sort);
    }
    public List<News> getNewsAll(String condition, String order, String sort) { return all(condition, order, sort); }
    public boolean addNews(News value) { return add(value.getNewsName(), value.getNewsContent()); }
    public boolean updNews(News value) { return update(value.getNewsID(), value.getNewsName(), value.getNewsContent()); }
    public boolean delNews(String id) { return delete(id); }

    @Override protected News map(String id, String name, String content, Date created) {
        News value = new News();
        value.setNewsID(id); value.setNewsName(name); value.setNewsContent(content); value.setCreateTime(created);
        return value;
    }
}
