package com.citywy.view;
 
import com.citywy.domain.User;
import com.citywy.service.UserService;
import com.citywy.util.PublicUtil;
import com.citywy.util.UserUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

 
/**
 * Servlet implementation class LoginServlet
 */
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    /**
     * Default constructor. 
     */
    public LoginServlet() {
    }
 
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
 
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=utf-8");
		String type=PublicUtil.getParam(request,"type",null);
		if("logout".equals(type)){
			HttpSession session=request.getSession();
			session.invalidate();
			request.getRequestDispatcher("/login.jsp").forward(request, response);
			return;
		}
		User user=new User();
		user.setName(request.getParameter("uname"));
		user.setPassword(request.getParameter("upwd"));
		UserService u=new UserService();
		try {
			boolean b=u.UserLogin(user);
			if(b){
				//1.(Cookie)如果验证账号密码正确，更新cookie中最后一次登录时间
				boolean b1=false;
				Cookie [] ck=request.getCookies();
				if(ck!=null){
					for(Cookie cookie:ck){
						String cname=cookie.getName();
						if("lasttime".equals(cname)){
							System.out.println("上次登录："+cookie.getValue().replace("#"," "));
							//更新时间，把当前日期更新到cookie中
							SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd#hh:mm:ss");
							String nowTime=sdf.format(new Date());
							cookie.setValue(nowTime);
							cookie.setMaxAge(7*3600*24);//7天
							response.addCookie(cookie);
							b1=true;
							break;
						}
					}
				}
				//2.cookie中没找到最后一次登录时间记录，添加
				if(!b1){
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd#hh:mm:ss");
					String nowTime=sdf.format(new Date());
					Cookie cook=new Cookie("lasttime",nowTime);
					cook.setMaxAge(7*3600*24);
					response.addCookie(cook);
				}
				//3.保存登录信息Session
				HttpSession session=request.getSession();
				User loginUser = UserUtil.getUserByName(user.getName());
				session.setAttribute("loginuser", loginUser);
				session.setAttribute("username", loginUser.getName());
//				session.setMaxInactiveInterval(-1); session有效时间(单位秒)，默认30min，小于等于0时永不超时

				//4.保存访问数+1
				String nums=(String)this.getServletContext().getAttribute("nums");
				if(nums==null){
					//说明这是第一个人
					this.getServletContext().setAttribute("nums", "1");
				}else{
					this.getServletContext().setAttribute("nums", (Integer.parseInt(nums)+1)+"");
				}
				//request.getRequestDispatcher("/servlets/IndexServlet.htm").forward(request, response);
				response.sendRedirect("/servlets/index.htm");
			}else{
				request.setAttribute("message", "用户名或者密码错误！");
				request.getRequestDispatcher("/login.jsp").forward(request, response);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
 
}