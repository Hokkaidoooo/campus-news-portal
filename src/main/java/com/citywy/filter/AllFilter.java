package com.citywy.filter;
 
import java.io.IOException;
import java.util.regex.Pattern;
 
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
public class AllFilter implements Filter {
	//要检查的session的名称
	private String sessionKey;
	//不拦截的URL正则表达式
	private Pattern excepUrlPattern;
	//检查不通过，转发的URl
	private String forwardUrl;

	/**
	 * 随着Filter对象的创建而立即创建，因为AllFilter只创建一次，所以init只是创建一次。
	 * @param cfg
	 * @throws ServletException
	 */
	@Override
	public void init(FilterConfig cfg) throws ServletException {
		sessionKey=cfg.getInitParameter("sessionKey");
		String excepUrlRegex = cfg.getInitParameter("excepUrlRegex");
		if(excepUrlRegex!=null && !"".equals(excepUrlRegex)){
			//将给定正则表达式编译成一个模式
			excepUrlPattern=Pattern.compile(excepUrlRegex);
		}
		forwardUrl=cfg.getInitParameter("redirectUrl");
	}

	@Override
	public void destroy() {
		
	}
 
	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
//		System.out.println("执行过滤器");
		HttpServletRequest request=(HttpServletRequest)req;
		HttpServletResponse response=(HttpServletResponse)res;
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("UTF-8");
		String servletPath = request.getServletPath();
		//如果请求路径与forwardurl相同，或请求的路径是排除的URL时，则直接放行
		System.out.println(forwardUrl+"--"+servletPath+"--"+excepUrlPattern.matcher(servletPath).matches());
		if(servletPath.equals(forwardUrl)||excepUrlPattern.matcher(servletPath).matches()){
			chain.doFilter(req, res);
			return;
		}
		Object sessionobj=request.getSession().getAttribute(sessionKey);
		if(sessionobj==null){
			request.setAttribute("err", "您还没有登录！");
			request.getRequestDispatcher(forwardUrl).forward(request, response);
		}else{
			chain.doFilter(req, res);
		}
	}
	
}