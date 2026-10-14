package com.citywy.service;

import com.citywy.domain.Leftmenu;
import com.citywy.domain.Leftmenu;
import com.citywy.util.DBUtil;

import java.sql.SQLException;
import java.util.*;

public class LeftmenuService {
    /**
     * 根据id获取
     * @param id
     * @return
     */
    public Leftmenu get(String id) {
        Leftmenu leftmenu = new Leftmenu();
        List<Map<String,Object>> maps = null;
        try {
            maps = DBUtil.executeQuery("select * from leftmenu where leftmenuID='"+id+"'",null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(maps!=null && maps.size()>0){
            Map map = maps.get(0);
            leftmenu.setLeftmenuID(map.get("leftmenuID").toString());
            leftmenu.setLeftmenuName(map.get("leftmenuName").toString());
            leftmenu.setLeftmenuContent(map.get("leftmenuContent").toString());
            leftmenu.setCreateTime((Date)map.get("createTime"));
        }
        return leftmenu;
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
                maps = DBUtil.executeQuery("select count(*) from leftmenu where "+condition,null);
            }else{
                maps = DBUtil.executeQuery("select count(*) from leftmenu", null);
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
    public List<Leftmenu> getLeftmenuList(String condition, int pageNow, int pageSize, String order, String sort){
        List<Leftmenu> list=new ArrayList<>();
        //查询分页语句
        String sql=null;
        if(condition!=null){
            sql="select * from leftmenu where "+condition+" order by "+order+" "+sort+" limit "+(pageNow-1)*pageSize+","+pageSize;
        }else{
            sql="select * from leftmenu order by "+order+" "+sort+" limit "+(pageNow-1)*pageSize+","+pageSize;
        }
        List<Map<String, Object>> datas = null;
        try {
            datas = DBUtil.executeQuery(sql, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Leftmenu leftmenu =null;
        try {
            if(datas!=null&&datas.size()>0)
            for (Map<String,Object> map:datas){
                leftmenu =new Leftmenu();
                leftmenu.setLeftmenuID(map.get("leftmenuID").toString());
                leftmenu.setLeftmenuName(map.get("leftmenuName").toString());
                leftmenu.setLeftmenuContent(map.get("leftmenuContent").toString());
                leftmenu.setCreateTime((Date)map.get("createTime"));
                list.add(leftmenu);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Leftmenu> getLeftmenuByContent(String condition, String order, String sort) {
        List<Leftmenu> list = new ArrayList<>();
        // 构建查询语句
        String sql = null;
        if (condition != null) {
            sql = "SELECT * FROM leftmenu WHERE " + condition + " ORDER BY " + order + " " + sort;
        } else {
            sql = "SELECT * FROM leftmenu ORDER BY " + order + " " + sort;
        }

        List<Map<String, Object>> datas = null;
        try {
            datas = DBUtil.executeQuery(sql, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Leftmenu leftmenu = null;
        try {
            if (datas != null && datas.size() > 0) {
                for (Map<String, Object> map : datas) {
                    leftmenu = new Leftmenu();
                    leftmenu.setLeftmenuID(map.get("leftmenuID").toString());
                    leftmenu.setLeftmenuName(map.get("leftmenuName").toString());
                    leftmenu.setLeftmenuContent(map.get("leftmenuContent").toString());
                    leftmenu.setCreateTime((Date) map.get("createTime"));
                    list.add(leftmenu);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 添加
     * @param leftmenu
     * @return
     */
    public boolean addLeftmenu(Leftmenu leftmenu){
        Map<String,Object> maps = new HashMap<>();
        maps.put("leftmenuID",UUID.randomUUID().toString().replaceAll("-",""));
        maps.put("leftmenuName", leftmenu.getLeftmenuName());
        maps.put("leftmenuContent", leftmenu.getLeftmenuContent());
        maps.put("createTime",new Date());
        int num=0;
        try {
            num = DBUtil.insert("leftmenu",maps);
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
     * @param leftmenu
     * @return
     */
    public boolean updLeftmenu(Leftmenu leftmenu){
        Map<String,Object> valueMap = new HashMap<String, Object>();
        valueMap.put("leftmenuID", leftmenu.getLeftmenuID());
        valueMap.put("leftmenuName", leftmenu.getLeftmenuName());
        valueMap.put("leftmenuContent", leftmenu.getLeftmenuContent());
        Map<String, Object> whereMap = new HashMap<>();
        whereMap.put("leftmenuID", leftmenu.getLeftmenuID());
        int num=0;
        try {
            num = DBUtil.update("leftmenu",valueMap,whereMap);
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
    public boolean delLeftmenu(String id){
        boolean b=false;
        if(!"".equals(id)){
            Map<String, Object> whereMap = new HashMap<>();
            whereMap.put("leftmenuID",id);
            int affectRowCount = 0;
            try {
                affectRowCount = DBUtil.delete("leftmenu", whereMap);
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
