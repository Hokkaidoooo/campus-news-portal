package com.citywy.service;

import com.citywy.domain.Notice;
import com.citywy.domain.Notice;
import com.citywy.util.DBUtil;

import java.sql.SQLException;
import java.util.*;

public class NoticeService {
    /**
     * 根据id获取
     * @param id
     * @return
     */
    public Notice get(String id) {
        Notice notice = new Notice();
        List<Map<String,Object>> maps = null;
        try {
            maps = DBUtil.executeQuery("select * from notice where noticeID='"+id+"'",null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(maps!=null && maps.size()>0){
            Map map = maps.get(0);
            notice.setNoticeID(map.get("noticeID").toString());
            notice.setNoticeName(map.get("noticeName").toString());
            notice.setNoticeContent(map.get("noticeContent").toString());
            notice.setCreateTime((Date)map.get("createTime"));
        }
        return notice;
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
                maps = DBUtil.executeQuery("select count(*) from notice where "+condition,null);
            }else{
                maps = DBUtil.executeQuery("select count(*) from notice", null);
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
    public List<Notice> getNoticeList(String condition, int pageNow, int pageSize, String order, String sort){
        List<Notice> list=new ArrayList<>();
        //查询分页语句
        String sql=null;
        if(condition!=null){
            sql="select * from notice where "+condition+" order by "+order+" "+sort+" limit "+(pageNow-1)*pageSize+","+pageSize;
        }else{
            sql="select * from notice order by "+order+" "+sort+" limit "+(pageNow-1)*pageSize+","+pageSize;
        }
        List<Map<String, Object>> datas = null;
        try {
            datas = DBUtil.executeQuery(sql, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Notice notice =null;
        try {
            if(datas!=null&&datas.size()>0)
            for (Map<String,Object> map:datas){
                notice =new Notice();
                notice.setNoticeID(map.get("noticeID").toString());
                notice.setNoticeName(map.get("noticeName").toString());
                notice.setNoticeContent(map.get("noticeContent").toString());
                notice.setCreateTime((Date)map.get("createTime"));
                list.add(notice);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public List<Notice> getNoticeAll(String condition, String order, String sort) {
        List<Notice> list = new ArrayList<>();
        // 构建查询语句
        String sql = null;
        if (condition != null) {
            sql = "SELECT * FROM notice WHERE " + condition + " ORDER BY " + order + " " + sort;
        } else {
            sql = "SELECT * FROM notice ORDER BY " + order + " " + sort;
        }

        List<Map<String, Object>> datas = null;
        try {
            datas = DBUtil.executeQuery(sql, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Notice notice = null;
        try {
            if (datas != null && datas.size() > 0) {
                for (Map<String, Object> map : datas) {
                    notice = new Notice();
                    notice.setNoticeID(map.get("noticeID").toString());
                    notice.setNoticeName(map.get("noticeName").toString());
                    notice.setNoticeContent(map.get("noticeContent").toString());
                    notice.setCreateTime((Date) map.get("createTime"));
                    list.add(notice);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    /**
     * 添加
     * @param notice
     * @return
     */
    public boolean addNotice(Notice notice){
        Map<String,Object> maps = new HashMap<>();
        maps.put("noticeID",UUID.randomUUID().toString().replaceAll("-",""));
        maps.put("noticeName", notice.getNoticeName());
        maps.put("noticeContent", notice.getNoticeContent());
        maps.put("createTime",new Date());
        int num=0;
        try {
            num = DBUtil.insert("notice",maps);
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
     * @param notice
     * @return
     */
    public boolean updNotice(Notice notice){
        Map<String,Object> valueMap = new HashMap<String, Object>();
        valueMap.put("noticeID", notice.getNoticeID());
        valueMap.put("noticeName", notice.getNoticeName());
        valueMap.put("noticeContent", notice.getNoticeContent());
        Map<String, Object> whereMap = new HashMap<>();
        whereMap.put("noticeID", notice.getNoticeID());
        int num=0;
        try {
            num = DBUtil.update("notice",valueMap,whereMap);
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
    public boolean delNotice(String id){
        boolean b=false;
        if(!"".equals(id)){
            Map<String, Object> whereMap = new HashMap<>();
            whereMap.put("noticeID",id);
            int affectRowCount = 0;
            try {
                affectRowCount = DBUtil.delete("notice", whereMap);
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
