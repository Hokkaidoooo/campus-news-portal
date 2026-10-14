package com.citywy.util;

import sun.java2d.pipe.SpanIterator;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * 没有连接池的工具类
 */
public class DBConnUtil {
    private static Connection conn=null;
    private static PreparedStatement pstmt=null;
    private static ResultSet rs=null;
    //连接数据库参数
    private static String url="";
    private static String username="";
    private static String password="";
    private static String driver="";
    //读取配置文件
    private static Properties pp=null;
    private static InputStream ins=null;
    //执行静态代码块
    static{
        try {
            pp=new Properties();
            ins=DBConnUtil.class.getClassLoader().getResourceAsStream("jdbc-mysql.properties");
            pp.load(ins);
            url=pp.getProperty("jdbc.url");
            username=pp.getProperty("jdbc.username");
            password=pp.getProperty("jdbc.password");
            driver=pp.getProperty("jdbc.driverClassName");
            Class.forName(driver);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                ins.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ins=null;
        }
    }

    /**
     * 获取连接
     * @return
     */
    public static Connection getConnection(){
        try {
            conn=DriverManager.getConnection(url,username,password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * 统一的增删改
     * TIP:自动提交，没有异常数据回滚的功能
     *
     * @param sql
     * @param parameters
     * @return boolean
     */
    public static int executeUpdate(String sql,Object [] parameters){
        int affectRowCount=0;
        try {
            conn=getConnection();
            pstmt=conn.prepareStatement(sql);
            if(parameters!=null && !"".equals(parameters)){
                for(int i=0;i<parameters.length;i++){
                    pstmt.setObject(i+1, parameters[i]);
                }
            }
            affectRowCount=pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            close(rs,pstmt,conn);
        }
        return affectRowCount;
    }

    /**
     * 执行数据库插入操作（简化的新增）
     *
     * @param valueMap  插入数据表中key为列名和value为列对应的值的Map对象
     * @param tableName 要插入的数据库的表名
     * @return 影响的行数
     * @throws SQLException SQL异常
     */
    public static int insert(String tableName, Map<String, Object> valueMap) throws SQLException {
        /**获取数据库插入的Map的键值对的值**/
        Set<String> keySet = valueMap.keySet();
        Iterator<String> iterator = keySet.iterator();
        /**要插入的字段sql，其实就是用key拼起来的**/
        StringBuilder columnSql = new StringBuilder();
        /**要插入的字段值，其实就是？**/
        StringBuilder unknownMarkSql = new StringBuilder();
        Object[] bindArgs = new Object[valueMap.size()];
        int i = 0;
        while (iterator.hasNext()) {
            String key = iterator.next();
            columnSql.append(i == 0 ? "" : ",");
            columnSql.append(key);

            unknownMarkSql.append(i == 0 ? "" : ",");
            unknownMarkSql.append("?");
            bindArgs[i] = valueMap.get(key);
            i++;
        }
        /**开始拼插入的sql语句**/
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ");
        sql.append(tableName);
        sql.append(" (");
        sql.append(columnSql);
        sql.append(" )  VALUES (");
        sql.append(unknownMarkSql);
        sql.append(" )");
        return executeUpdate(sql.toString(), bindArgs);
    }

    /**
     * 执行更新操作（简化的修改）
     *
     * @param tableName 表名
     * @param valueMap  要更改的值
     * @param whereMap  条件
     * @return 影响的行数
     * @throws SQLException SQL异常
     */
    public static int update(String tableName, Map<String, Object> valueMap, Map<String, Object> whereMap) throws SQLException {
        /**获取数据库插入的Map的键值对的值**/
        Set<String> keySet = valueMap.keySet();
        Iterator<String> iterator = keySet.iterator();
        /**开始拼插入的sql语句**/
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ");
        sql.append(tableName);
        sql.append(" SET ");

        /**要更改的的字段sql，其实就是用key拼起来的**/
        StringBuilder columnSql = new StringBuilder();
        int i = 0;
        List<Object> objects = new ArrayList<>();
        while (iterator.hasNext()) {
            String key = iterator.next();
            columnSql.append(i == 0 ? "" : ",");
            columnSql.append(key + " = ? ");
            objects.add(valueMap.get(key));
            i++;
        }
        sql.append(columnSql);

        /**更新的条件:要更改的的字段sql，其实就是用key拼起来的**/
        StringBuilder whereSql = new StringBuilder();
        int j = 0;
        if (whereMap != null && whereMap.size() > 0) {
            whereSql.append(" WHERE ");
            iterator = whereMap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                whereSql.append(j == 0 ? "" : " AND ");
                whereSql.append(key + " = ? ");
                objects.add(whereMap.get(key));
                j++;
            }
            sql.append(whereSql);
        }
        return executeUpdate(sql.toString(), objects.toArray());
    }

    /**
     * 执行删除操作（简化的删除）
     *
     * @param tableName 要删除的表名
     * @param whereMap  删除的条件
     * @return 影响的行数
     * @throws SQLException SQL执行异常
     */
    public static int delete(String tableName, Map<String, Object> whereMap) throws SQLException {
        /**准备删除的sql语句**/
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ");
        sql.append(tableName);

        /**更新的条件:要更改的的字段sql，其实就是用key拼起来的**/
        StringBuilder whereSql = new StringBuilder();
        Object[] bindArgs = null;
        if (whereMap != null && whereMap.size() > 0) {
            bindArgs = new Object[whereMap.size()];
            whereSql.append(" WHERE ");
            /**获取数据库插入的Map的键值对的值**/
            Set<String> keySet = whereMap.keySet();
            Iterator<String> iterator = keySet.iterator();
            int i = 0;
            while (iterator.hasNext()) {
                String key = iterator.next();
                whereSql.append(i == 0 ? "" : " AND ");
                whereSql.append(key + " = ? ");
                bindArgs[i] = whereMap.get(key);
                i++;
            }
            sql.append(whereSql);
        }
        return executeUpdate(sql.toString(), bindArgs);
    }


    /**
     * 执行查询
     *
     * @param sql      要执行的sql语句
     * @param bindArgs 绑定的参数
     * @return List<Map<String, Object>>结果集对象
     * @throws SQLException SQL执行异常
     */
    public static List<Map<String, Object>> executeQuery(String sql, Object[] bindArgs) throws SQLException {
        List<Map<String, Object>> datas = new ArrayList<>();
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            if (bindArgs != null) {
                /**设置sql占位符中的值**/
                for (int i = 0; i < bindArgs.length; i++) {
                    pstmt.setObject(i + 1, bindArgs[i]);
                }
            }
            /**执行sql语句，获取结果集**/
            rs = pstmt.executeQuery();
            datas = getDatas(rs);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            close(rs,pstmt,conn);
        }
        return datas;
    }


    /**
     * 将结果集对象封装成List<Map<String, Object>> 对象
     *
     * @param resultSet 结果集
     * @return 结果的封装
     * @throws SQLException
     */
    private static List<Map<String, Object>> getDatas(ResultSet resultSet) throws SQLException {
        List<Map<String, Object>> datas = new ArrayList<>();
        /**获取结果集的数据结构对象**/
        ResultSetMetaData metaData = resultSet.getMetaData();
        while (resultSet.next()) {
            Map<String, Object> rowMap = new HashMap<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
//                System.out.println("输出列名："+metaData.getColumnName(i));
                rowMap.put(metaData.getColumnName(i), resultSet.getObject(i));
            }
            datas.add(rowMap);
        }
//        System.out.println("成功查询到了" + datas.size() + "行数据");
        for (int i = 0; i < datas.size(); i++) {
            Map<String, Object> map = datas.get(i);
//            System.out.println("第" + (i + 1) + "行：" + map);
        }
        return datas;
    }

    /**
     * 关闭资源
     * @param rs
     * @param pstmt
     * @param conn
     */
    public static void close(ResultSet rs,PreparedStatement pstmt,Connection conn){
        if(rs!=null){
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(pstmt!=null){
            try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            pstmt=null;
        }
        if(conn!=null){
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            conn=null;
        }
    }
}
