package com.citywy.view;

import com.citywy.domain.Leftmenu;
import com.citywy.domain.News;
import com.citywy.domain.Notice;
import com.citywy.domain.Topmenu;
import com.citywy.service.LeftmenuService;
import com.citywy.service.NewsService;
import com.citywy.service.NoticeService;
import com.citywy.service.TopmenuService;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/data")
public class DataServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String dataType = request.getParameter("dataType");

        if ("topmenu".equals(dataType)) {
            listTopmenu(request, response);
        } else if ("news".equals(dataType)) {
            listNews(request, response);
        } else if ("notice".equals(dataType)) {
            listNotice(request, response);
        } else if ("leftmenu".equals(dataType)) {
            getLeftmenuByContent(request, response);
        }
        else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Invalid dataType parameter");
        }
    }

    private void listTopmenu(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        TopmenuService topmenuService = new TopmenuService();

        List<Topmenu> topmenus = topmenuService.getTopmenuAll(null, "id", "ASC");

        Gson gson = new Gson();
        String jsonData = gson.toJson(topmenus);
        try (PrintWriter out = response.getWriter()) {
            out.println(jsonData);
        }
    }

    private void listNews(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        NewsService newsService = new NewsService();

        List<News> news = newsService.getNewsAll(null, "newsID", "ASC");

        Gson gson = new Gson();
        String jsonData = gson.toJson(news);
        try (PrintWriter out = response.getWriter()) {
            out.println(jsonData);
        }
    }

    private void listNotice(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        NoticeService noticeService = new NoticeService();

        List<Notice> notices = noticeService.getNoticeAll(null, "noticeID", "ASC");

        Gson gson = new Gson();
        String jsonData = gson.toJson(notices);
        try (PrintWriter out = response.getWriter()) {
            out.println(jsonData);
        }
    }

    private void getLeftmenuByContent(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String leftmenuContent = request.getParameter("leftmenuContent");
        if (leftmenuContent == null) { response.sendError(400, "缺少 leftmenuContent"); return; }

        LeftmenuService leftmenuService = new LeftmenuService();

        List<Leftmenu> leftmenus = leftmenuService.getLeftmenuByContent(leftmenuContent, "leftmenuID", "ASC");

        Gson gson = new Gson();
        String jsonData = gson.toJson(leftmenus);

        try (PrintWriter out = response.getWriter()) {
            out.println(jsonData);
        }
    }


}
