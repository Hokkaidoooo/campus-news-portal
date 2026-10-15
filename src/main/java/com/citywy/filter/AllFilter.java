package com.citywy.filter;

import com.citywy.domain.User;
import com.citywy.service.UserService;
import java.io.IOException;
import java.util.function.IntFunction;
import javax.servlet.*;
import javax.servlet.http.*;

/** 后台页面、管理路由和辅助上传共用这一个访问控制入口。 */
public final class AllFilter implements Filter {
    private final IntFunction<User> findUser;
    private BackendAccessPolicy policy;

    public AllFilter() { this(id -> new UserService().get(id)); }
    AllFilter(IntFunction<User> findUser) { this.findUser = findUser; }

    @Override public void init(FilterConfig config) {
        policy = new BackendAccessPolicy(config.getInitParameter("user-management-role"),
                config.getInitParameter("content-editor-role"));
    }
    @Override public void destroy() { }

    public static boolean requiresLogin(String path) {
        if (path.equals("/login.jsp") || path.equals("/servlets/LoginServlet.htm")) return false;
        if (path.equals("/WEB-INF/views/article.jsp")) return false;
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
            response.setHeader("Cache-Control", "no-store");
            HttpSession session = request.getSession(false);
            Object identity = session == null ? null : session.getAttribute("loginuser");
            if (!(identity instanceof User)) {
                response.sendError(401, "请先登录后台");
                return;
            }
            User current = findUser.apply(((User) identity).getId());
            if (current == null || !current.isActive()) {
                session.invalidate();
                response.sendError(401, "账号已停用或不存在，请重新登录");
                return;
            }
            session.setAttribute("loginuser", current);
            session.setAttribute("username", current.getName());
            boolean canManageUsers = policy.canManageUsers(current);
            if (!policy.canEditContent(current) || (BackendAccessPolicy.isUserManagement(path) && !canManageUsers)) {
                response.sendError(403, "当前账号没有此操作的权限");
                return;
            }
            request.setAttribute("canManageUsers", canManageUsers);
            request.setAttribute("backendRoles", policy.roles());
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
