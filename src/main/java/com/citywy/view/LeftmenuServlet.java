package com.citywy.view;

import com.citywy.domain.Leftmenu;
import com.citywy.service.LeftmenuService;
import com.citywy.util.PageUtil;
import com.citywy.util.PublicUtil;

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

public class LeftmenuServlet extends HttpServlet {
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
        String id = PublicUtil.getParam(request, "id", "");
        LeftmenuService leftmenuService = new LeftmenuService();
        Leftmenu leftmenu = leftmenuService.get(id);
        Map<Integer, String> menus = new HashMap<Integer, String>();
        request.setAttribute("pages", leftmenu);
        request.setAttribute("menus", menus);
        request.getRequestDispatcher("/admin/leftmenu/leftmenu_view.jsp").forward(request, response);
    }

    /**
     * 加载页面，包括新页面和修改页面
     */
    public void load(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = PublicUtil.getParam(request, "id", "");
        LeftmenuService leftmenuService = new LeftmenuService();
        Leftmenu leftmenu = leftmenuService.get(id);
        Map<Integer, String> menus = new HashMap<Integer, String>();
        request.setAttribute("pages", leftmenu);
        request.setAttribute("menus", menus);
        request.getRequestDispatcher("/admin/leftmenu/leftmenu_load.jsp").forward(request, response);
    }

    /**
     * 增加和修改
     *
     * @param request
     * @param response
     */
    public void modify(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String id = PublicUtil.getParam(request, "leftmenuID", "");
        LeftmenuService leftmenuService = new LeftmenuService();
        String leftmenuName = PublicUtil.getParam(request, "leftmenuName", null);
        String leftmenuContent = PublicUtil.getParam(request, "leftmenuContent", null);
        Leftmenu leftmenu = new Leftmenu();
        leftmenu.setLeftmenuName(leftmenuName);
        leftmenu.setLeftmenuContent(leftmenuContent);
        boolean b = false;
        String message = "";
        String locationhref = "";
        if (!id.equals("")) {
            leftmenu.setLeftmenuID(id);
            b = leftmenuService.updLeftmenu(leftmenu);
            if (b) {
                message = "修改成功";
            }
            //修改返回时返回之前的筛选条件
            locationhref = request.getParameter("locationhref");
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
            b = leftmenuService.addLeftmenu(leftmenu);
            if (b) {
                message = "添加成功！";
            }
        }
        //为了传递修改成功的参数，我们这里都用转发，其实springmvc中默认也是转发
        request.setAttribute("message", message);
        request.getRequestDispatcher("/servlets/leftmenu/list.htm" + locationhref).forward(request, response);
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
        LeftmenuService leftmenuService = new LeftmenuService();
        //获取参数
        int pageNow = PublicUtil.getParam(request, "x_now", 1);
        int pageSize = PublicUtil.getParam(request, "x_size", 10);
        String x_order = PublicUtil.getParam(request, "x_order", "leftmenuID");//根据什么字段排序
        String x_sort = PublicUtil.getParam(request, "x_sort", "desc");
        String x_name = PublicUtil.getParam(request, "x_name", null);

        if (x_order.equals("id")) {
            x_order = "leftmenuID";
        }
        //查询条件
        String condition = null;
        if (x_name != null && !"".equals(x_name)) {
            condition = "leftmenuName like '%" + x_name + "%'";
        }

        int rowcount = leftmenuService.getRowCount(condition);
        int pageCount = (rowcount - 1) / pageSize + 1;

        List<Leftmenu> list = leftmenuService.getLeftmenuList(condition, pageNow, pageSize, x_order, x_sort);

        //分页参数
        PageUtil pageUtil = new PageUtil(pageNow, pageSize, pageCount, rowcount, x_order, x_sort, x_name,"","","");

        request.setAttribute("list", list);
        request.setAttribute("pageutil", pageUtil);
        request.getRequestDispatcher("/admin/leftmenu/leftmenu_list.jsp").forward(request, response);
    }

    /**
     * 删除单个ID内容
     *
     * @param request
     * @param response
     */
    public void drop(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = PublicUtil.getParam(request, "id", "");
        if (!"".equals(id)) {
            LeftmenuService leftmenuService = new LeftmenuService();
            boolean b = leftmenuService.delLeftmenu(id);
            if (b) {
                response.sendRedirect("/servlets/leftmenu/list.htm");
            }
        }
    }
}
