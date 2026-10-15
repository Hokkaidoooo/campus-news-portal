package com.citywy.view;

import com.citywy.domain.User;
import com.citywy.service.UserService;
import com.citywy.util.PageUtil;
import com.citywy.util.PublicUtil;
import com.citywy.util.UserUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=utf-8");
        String uri = request.getRequestURI();
        String type = uri.substring(uri.lastIndexOf("/") + 1, uri.lastIndexOf("."));
        //跳转到对应的增删改查
        if (type != null) {
            if ("load".equals(type)) {
                load(request, response);
            } else if ("modify".equals(type)) {
                modify(request, response);
            } else if ("list".equals(type)) {
                list(request, response);
            } else if ("drop".equals(type)) {
                drop(request, response);
            }else if ("view".equals(type)) {
                view(request, response);
            }
        }
    }

    private void view(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer id = PublicUtil.getParam(request, "id", 0);
        UserService userService = new UserService();
        User user = userService.get(id);
        Map<Integer, String> menus = new HashMap<Integer, String>();
        request.setAttribute("pages", user);
        request.setAttribute("menus", menus);
        request.getRequestDispatcher("/admin/user/user_view.jsp").forward(request, response);
    }

    /**
     * 加载页面，包括新页面和修改页面
     */
    public void load(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer id = PublicUtil.getParam(request, "id", 0);
        UserService userService = new UserService();
        User user = id == 0 ? new User() : userService.get(id);
        if (user == null) { response.sendError(404, "记录不存在"); return; }
        Map<Integer, String> menus = new HashMap<Integer, String>();
        request.setAttribute("pages", user);
        request.setAttribute("menus", menus);
        request.getRequestDispatcher("/admin/user/user_load.jsp").forward(request, response);
    }

    /**
     * 增加和修改
     *
     * @param request
     * @param response
     */
    public void modify(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Integer id = PublicUtil.getParam(request, "id", 0);
        UserService userService = new UserService();
        String pointName = PublicUtil.getParam(request, "name", "");
        String uname = PublicUtil.getParam(request, "uname", "");
        String password = PublicUtil.getParam(request, "password", "");
        String role = PublicUtil.getParam(request, "role", "");
        String valid = PublicUtil.getParam(request, "valid", "");
        User user = new User();
        user.setName(pointName);
        user.setUname(uname);
        user.setPassword(password);
        user.setRole(role);
        user.setValid(valid);
        boolean b = false;
        String message = "";
        String locationhref = "";
        if (id>0) {
            user.setId(id);
            b = userService.updUser(user);
            if (b) {
                message = "修改成功";
            }
            //修改返回时返回之前的筛选条件
            locationhref = WebPaths.returnQuery(request.getParameter("locationhref"));
            //正则去掉id=xxx&
            if (locationhref.contains("id=")) {
                String regex = "";
                if (locationhref.contains("&")) {
                    regex = "id=[0-9]*&";
                } else {
                    regex = "id=[0-9]*";
                }
                Pattern pat = Pattern.compile(regex);
                Matcher matcher = pat.matcher(locationhref);
                while (matcher.find()) {
                    String temp = locationhref.substring(matcher.start(), matcher.end());
                    locationhref = locationhref.replaceAll(temp, "");
                }
            }
        } else {
            b = userService.addUser(user);
            if (b) {
                message = "添加成功！";
            }
        }
        //为了传递修改成功的参数，我们这里都用转发，其实springmvc中默认也是转发
        request.setAttribute("message", message);
        request.getRequestDispatcher("/servlets/user/list.htm" + locationhref).forward(request, response);
    }

    /**
     * 查询列表
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void list(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserService userService = new UserService();
        //获取参数
        int pageNow = PublicUtil.getParam(request, "x_now", 1);
        int pageSize = PublicUtil.getParam(request, "x_size", 25);
        String x_order = PublicUtil.getParam(request, "x_order", "id");//根据什么字段排序
        String x_sort = PublicUtil.getParam(request, "x_sort", "desc");
        String x_name = PublicUtil.getParam(request, "x_name", null);
        String x_name1 = PublicUtil.getParam(request, "x_name1", null);
        String x_name2 = PublicUtil.getParam(request, "x_name2", null);
        String x_name3 = PublicUtil.getParam(request, "x_name3", null);

        //查询条件
        Map<String,String> condition = new HashMap<>();
        if (x_name1 != null && !"".equals(x_name1)) {
            condition.put("role", x_name1);
        }
        if (x_name2 != null && !"".equals(x_name2)) {
            condition.put("name", x_name2);
        }
        if (x_name3 != null && !"".equals(x_name3)) {
            condition.put("uname", x_name3);
        }

        int rowcount = userService.countFiltered(condition);
        int pageCount = (rowcount - 1) / pageSize + 1;

        List<User> list = userService.listFiltered(condition, pageNow, pageSize, x_order, x_sort);

        //分页参数
        PageUtil pageUtil = new PageUtil(pageNow, pageSize, pageCount, rowcount, x_order, x_sort, x_name, x_name1, x_name2, x_name3);

        request.setAttribute("list", list);
        request.setAttribute("pageutil", pageUtil);
        request.getRequestDispatcher("/admin/user/user_list.jsp").forward(request, response);
    }

    /**
     * 删除单个ID内容
     *
     * @param request
     * @param response
     */
    public void drop(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer id = PublicUtil.getParam(request, "id", 0);
        if (id>0) {
            UserService userService = new UserService();
            boolean b = userService.delUser(id);
            if (b) {
                response.sendRedirect(request.getContextPath() + "/servlets/user/list.htm");
            }
        }
    }
}
