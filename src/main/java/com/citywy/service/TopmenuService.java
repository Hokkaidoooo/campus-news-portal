package com.citywy.service;

import com.citywy.domain.Topmenu;
import com.citywy.util.DBUtil;

import java.sql.SQLException;
import java.util.*;

public class TopmenuService {
    /**
     * 根据id获取
     * @param id
     * @return
     */
    public Topmenu get(String id) {
        Topmenu topmenu = new Topmenu();
        List<Map<String,Object>> maps = null;
        try {
            maps = DBUtil.executeQuery("select * from topmenu where id='"+id+"'",null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(maps!=null && maps.size()>0){
            Map map = maps.get(0);
            topmenu.setId(map.get("id").toString());
            topmenu.setTopmenuName(map.get("topmenuName").toString());
            topmenu.setTopmenuContent(map.get("topmenuContent").toString());
            topmenu.setCreateTime((Date)map.get("createTime"));
        }
        return topmenu;
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
                maps = DBUtil.executeQuery("select count(*) from topmenu where "+condition,null);
            }else{
                maps = DBUtil.executeQuery("select count(*) from topmenu", null);
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
    public List<Topmenu> getTopmenuList(String condition, int pageNow, int pageSize, String order, String sort){
        List<Topmenu> list=new ArrayList<>();
        //查询分页语句
        String sql=null;
        if(condition!=null){
            sql="select * from topmenu where "+condition+" order by "+order+" "+sort+" limit "+(pageNow-1)*pageSize+","+pageSize;
        }else{
            sql="select * from topmenu order by "+order+" "+sort+" limit "+(pageNow-1)*pageSize+","+pageSize;
        }
        List<Map<String, Object>> datas = null;
        try {
            datas = DBUtil.executeQuery(sql, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Topmenu topmenu =null;
        try {
            if(datas!=null&&datas.size()>0)
            for (Map<String,Object> map:datas){
                topmenu =new Topmenu();
                topmenu.setId(map.get("id").toString());
                topmenu.setTopmenuName(map.get("topmenuName").toString());
                topmenu.setTopmenuContent(map.get("topmenuContent").toString());
                topmenu.setCreateTime((Date)map.get("createTime"));
                list.add(topmenu);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public List<Topmenu> getTopmenuAll(String condition, String order, String sort) {
        List<Topmenu> list = new ArrayList<>();
        // 构建查询语句
        String sql = null;
        if (condition != null) {
            sql = "SELECT * FROM topmenu WHERE " + condition + " ORDER BY " + order + " " + sort;
        } else {
            sql = "SELECT * FROM topmenu ORDER BY " + order + " " + sort;
        }

        List<Map<String, Object>> datas = null;
        try {
            datas = DBUtil.executeQuery(sql, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Topmenu topmenu = null;
        try {
            if (datas != null && datas.size() > 0) {
                for (Map<String, Object> map : datas) {
                    topmenu = new Topmenu();
                    topmenu.setId(map.get("id").toString());
                    topmenu.setTopmenuName(map.get("topmenuName").toString());
                    topmenu.setTopmenuContent(map.get("topmenuContent").toString());
                    topmenu.setCreateTime((Date) map.get("createTime"));
                    list.add(topmenu);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 添加
     * @param topmenu
     * @return
     */
    public boolean addTopmenu(Topmenu topmenu){
        Map<String,Object> maps = new HashMap<>();

        maps.put("topmenuName", topmenu.getTopmenuName());
        maps.put("topmenuContent", topmenu.getTopmenuContent());
        maps.put("createTime",new Date());
        int num=0;
        try {
            num = DBUtil.insert("topmenu",maps);
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
     * @param topmenu
     * @return
     */
    public boolean updTopmenu(Topmenu topmenu){
        Map<String,Object> valueMap = new HashMap<String, Object>();
        valueMap.put("id", topmenu.getId());
        valueMap.put("topmenuName", topmenu.getTopmenuName());
        valueMap.put("topmenuContent", topmenu.getTopmenuContent());
        Map<String, Object> whereMap = new HashMap<>();
        whereMap.put("id", topmenu.getId());
        int num=0;
        try {
            num = DBUtil.update("topmenu",valueMap,whereMap);
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
    public boolean delTopmenu(String id){
        boolean b=false;
        if(!"".equals(id)){
            Map<String, Object> whereMap = new HashMap<>();
            whereMap.put("id",id);
            int affectRowCount = 0;
            try {
                affectRowCount = DBUtil.delete("topmenu", whereMap);
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
