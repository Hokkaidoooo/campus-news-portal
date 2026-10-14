package com.citywy.service;

import com.citywy.domain.News;
import com.citywy.domain.News;
import com.citywy.util.DBUtil;

import java.sql.SQLException;
import java.util.*;

public class NewsService {
    /**
     * 根据id获取
     * @param id
     * @return
     */
    public News get(String id) {
        News news = new News();
        List<Map<String,Object>> maps = null;
        try {
            maps = DBUtil.executeQuery("select * from news where newsID='"+id+"'",null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(maps!=null && maps.size()>0){
            Map map = maps.get(0);
            news.setNewsID(map.get("newsID").toString());
            news.setNewsName(map.get("newsName").toString());
            news.setNewsContent(map.get("newsContent").toString());
            news.setCreateTime((Date)map.get("createTime"));
        }
        return news;
    }


    /**
     * 获取共计多少条数据
     */
    public int getRowCount(String condition){
        //计算一共多少条
        int rowcount=0;
        List<Map<String, Object>> maps = null;
        try {
            if(condition!=null){
                maps = DBUtil.executeQuery("select count(*) from news where "+condition,null);
            }else{
                maps = DBUtil.executeQuery("select count(*) from news", null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            for (Map<String, Object> map:maps){
                rowcount=  Integer.parseInt(map.get("count(*)").toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowcount;
    }

    /**
     * 动态点分页
     * @param condition where条件
     * @param pageNow  当前第一页
     * @param pageSize 每页都少条
     * @param order 按什么列排序
     * @param sort  排序方式 asc desc
     * @return
     */
    public List<News> getNewsList(String condition, int pageNow, int pageSize, String order, String sort){
        List<News> list=new ArrayList<>();
        //查询分页语句
        String sql=null;
        if(condition!=null){
            sql="select * from news where "+condition+" order by "+order+" "+sort+" limit "+(pageNow-1)*pageSize+","+pageSize;
        }else{
            sql="select * from news order by "+order+" "+sort+" limit "+(pageNow-1)*pageSize+","+pageSize;
        }
        List<Map<String, Object>> datas = null;
        try {
            datas = DBUtil.executeQuery(sql, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        News news =null;
        try {
            if(datas!=null&&datas.size()>0)
            for (Map<String,Object> map:datas){
                news =new News();
                news.setNewsID(map.get("newsID").toString());
                news.setNewsName(map.get("newsName").toString());
                news.setNewsContent(map.get("newsContent").toString());
                news.setCreateTime((Date)map.get("createTime"));
                list.add(news);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<News> getNewsAll(String condition, String order, String sort) {
        List<News> list = new ArrayList<>();
        // 构建查询语句
        String sql = null;
        if (condition != null) {
            sql = "SELECT * FROM news WHERE " + condition + " ORDER BY " + order + " " + sort;
        } else {
            sql = "SELECT * FROM news ORDER BY " + order + " " + sort;
        }

        List<Map<String, Object>> datas = null;
        try {
            datas = DBUtil.executeQuery(sql, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        News news = null;
        try {
            if (datas != null && datas.size() > 0) {
                for (Map<String, Object> map : datas) {
                    news = new News();
                    news.setNewsID(map.get("newsID").toString());
                    news.setNewsName(map.get("newsName").toString());
                    news.setNewsContent(map.get("newsContent").toString());
                    news.setCreateTime((Date) map.get("createTime"));
                    list.add(news);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    /**
     * 添加
     * @param news
     * @return
     */
    public boolean addNews(News news){
        Map<String,Object> maps = new HashMap<>();
        maps.put("newsID",UUID.randomUUID().toString().replaceAll("-",""));
        maps.put("newsName", news.getNewsName());
        maps.put("newsContent", news.getNewsContent());
        maps.put("createTime",new Date());
        int num=0;
        try {
            num = DBUtil.insert("news",maps);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (num>0){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 修改
     * @param news
     * @return
     */
    public boolean updNews(News news){
        Map<String,Object> valueMap = new HashMap<String, Object>();
        valueMap.put("newsID", news.getNewsID());
        valueMap.put("newsName", news.getNewsName());
        valueMap.put("newsContent", news.getNewsContent());
        Map<String, Object> whereMap = new HashMap<>();
        whereMap.put("newsID", news.getNewsID());
        int num=0;
        try {
            num = DBUtil.update("news",valueMap,whereMap);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (num>0){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 删除单独某一条
     */
    public boolean delNews(String id){
        boolean b=false;
        if(!"".equals(id)){
            Map<String, Object> whereMap = new HashMap<>();
            whereMap.put("newsID",id);
            int affectRowCount = 0;
            try {
                affectRowCount = DBUtil.delete("news", whereMap);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            if(affectRowCount>0){
                b=true;
            }
        }
        return b;
    }
}
