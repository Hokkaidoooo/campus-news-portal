package com.citywy.view;

import com.citywy.domain.User;
import com.citywy.service.UserService;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public final class LoginServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if ("logout".equals(request.getParameter("type"))) { logout(request, response); return; }
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if ("logout".equals(request.getParameter("type"))) { logout(request, response); return; }
        String name = request.getParameter("uname"), password = request.getParameter("upwd");
        if (name == null || name.trim().isEmpty() || password == null || password.isEmpty()) {
            response.sendError(400, "请输入用户名和密码");
            return;
        }
        User submitted = new User();
        submitted.setName(name); submitted.setPassword(password);
        UserService service = new UserService();
        try {
            if (!service.UserLogin(submitted)) {
                response.setStatus(401);
                request.setAttribute("message", "用户名或密码错误，或账号已停用。");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
                return;
            }
            User user = service.getUserByName(name);
            HttpSession session = request.getSession();
            request.changeSessionId();
            session.setAttribute("loginuser", user);
            session.setAttribute("username", user.getName());
            session.setMaxInactiveInterval(1800);
            response.sendRedirect(request.getContextPath() + "/servlets/index.htm");
        } catch (SQLException error) {
            throw new ServletException("登录数据库操作失败", error);
        }
    }

    private void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }
}
