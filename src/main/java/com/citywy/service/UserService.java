package com.citywy.service;
 
import com.citywy.domain.User;
import com.citywy.util.DBConnUtil;
import com.citywy.util.DBUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class UserService {
	/**
	 * 用户登录
	 * @return
	 * @throws SQLException 
	 */
	public boolean UserLogin(User user) throws SQLException{
		boolean b=false;
		String sql="select * from user where name=? and password=?";
		String [] parameters={user.getName(),user.getPassword()};
		List<Map<String, Object>> datas= DBConnUtil.executeQuery(sql, parameters);
		if(datas.size()>0){
			b=true;
		}
		return b;
	}

	/**
	 * 根据name返回User实例
	 * @return
	 */
	public User getUserByName(String name){
		String sql="select * from user where name=?";
		Object [] args={name};
		List<Map<String, Object>> datas = null;
		try {
			datas = DBConnUtil.executeQuery(sql,args);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Map<String,Object> map = datas.get(0);
		User u = new User();
		u.setId(Integer.parseInt(map.get("id").toString()));
		u.setName(map.get("name").toString());
		u.setUname(map.get("uname").toString());
		u.setPassword(map.get("password").toString());
		u.setRole(map.get("role").toString());
		u.setValid(map.get("valid").toString());
		return u;
	}
	/**
	 * 根据id获取用户详情页
	 * @param id
	 * @return
	 */
	public User get(long id) {
		User user = new User();
		List<Map<String,Object>> maps = null;
		try {
			maps = DBUtil.executeQuery("select * from user where id="+id,null);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(maps!=null && maps.size()>0){
			Map map = maps.get(0);
			user.setId(Integer.parseInt(map.get("id").toString()));
			user.setName(map.get("name").toString());
			user.setUname(map.get("uname").toString());
			user.setPassword(map.get("password").toString());
			user.setRole(map.get("role").toString());
			user.setValid(map.get("valid").toString());
		}
		return user;
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
				maps = DBUtil.executeQuery("select count(*) from user where "+condition,null);
			}else{
				maps = DBUtil.executeQuery("select count(*) from user", null);
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
	 * 用户分页
	 * @param condition where条件
	 * @param pageNow  当前第一页
	 * @param pageSize 每页都少条
	 * @param order 按什么列排序
	 * @param sort  排序方式 asc desc
	 * @return
	 */
	public List<User> getUserList(String condition,int pageNow,int pageSize,String order,String sort){
		List<User> list=new ArrayList<>();
		//查询分页语句
		String sql=null;
		if(condition!=null){
			sql="select * from user where "+condition+" order by "+order+" "+sort+" limit "+(pageNow-1)*pageSize+","+pageSize;
		}else{
			sql="select * from user order by "+order+" "+sort+" limit "+(pageNow-1)*pageSize+","+pageSize;
		}
		List<Map<String, Object>> datas = null;
		try {
			datas = DBUtil.executeQuery(sql, null);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		User user=null;
		try {
			for (Map<String,Object> map:datas){
				user=new User();
				user.setId(Integer.parseInt(map.get("id").toString()));
				user.setName(map.get("name").toString());
				user.setUname(map.get("uname").toString());
				user.setPassword(map.get("password").toString());
				user.setRole(map.get("role").toString());
				user.setValid(map.get("valid").toString());
				list.add(user);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 添加用户
	 * @param user
	 * @return
	 */
	public boolean addUser(User user){
		Map<String,Object> maps = new HashMap<>();
		maps.put("name",user.getName());
		maps.put("uname",user.getUname());
		maps.put("password",user.getPassword());
		maps.put("role",user.getRole());
		maps.put("valid",user.getValid());
		int num=0;
		try {
			num = DBUtil.insert("user",maps);
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
	 * 修改用户
	 * @param user
	 * @return
	 */
	public boolean updUser(User user){
		Map<String,Object> valueMap = new HashMap<String, Object>();
		valueMap.put("name",user.getName());
		valueMap.put("uname",user.getUname());
		valueMap.put("password",user.getPassword());
		valueMap.put("role",user.getRole());
		valueMap.put("valid",user.getValid());
		Map<String, Object> whereMap = new HashMap<>();
		whereMap.put("id",user.getId());
		int num=0;
		try {
			num = DBUtil.update("user",valueMap,whereMap);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (num>0){
			return true;
		}else{
			return false;
		}
	}/**
	 * 删除单独某一条
	 */
	public boolean delUser(long id){
		boolean b=false;
		if(id>0){
			Map<String, Object> whereMap = new HashMap<>();
			whereMap.put("id",id);
			int affectRowCount = 0;
			try {
				affectRowCount = DBUtil.delete("user", whereMap);
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