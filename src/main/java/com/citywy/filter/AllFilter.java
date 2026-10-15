package com.citywy.filter;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;

/** 后台页面、管理路由和辅助上传共用这一个访问控制入口。 */
public final class AllFilter implements Filter {
    @Override public void init(FilterConfig config) { }
    @Override public void destroy() { }

    public static boolean requiresLogin(String path) {
        if (path.equals("/login.jsp") || path.equals("/servlets/LoginServlet.htm")) return false;
        if (path.startsWith("/servlets/") || path.startsWith("/admin/ajax/")) return true;
        if (path.endsWith(".jsp")) return true;
        return path.startsWith("/admin/") && !path.startsWith("/admin/assets/");
    }

    @Override public void doFilter(ServletRequest input, ServletResponse output, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) input;
        HttpServletResponse response = (HttpServletResponse) output;
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("X-Content-Type-Options", "nosniff");
        String path = request.getServletPath() + (request.getPathInfo() == null ? "" : request.getPathInfo());
        if (requiresLogin(path)) {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("loginuser") == null) {
                response.sendError(401, "请先登录后台");
                return;
            }
            response.setHeader("Cache-Control", "no-store");
            if ("cross-site".equals(request.getHeader("Sec-Fetch-Site"))) {
                response.sendError(403, "后台不接受跨站请求");
                return;
            }
        }
        try {
            chain.doFilter(request, response);
        } catch (IllegalArgumentException error) {
            request.getServletContext().log("请求参数不合法", error);
            if (response.isCommitted()) throw error;
            response.resetBuffer();
            response.sendError(400, "请求参数不合法");
        }
    }
}
