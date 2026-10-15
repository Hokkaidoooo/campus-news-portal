package com.citywy.view;

import com.citywy.domain.News;
import com.citywy.domain.Notice;
import com.citywy.service.NewsService;
import com.citywy.service.NoticeService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.function.BiFunction;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** 同一个查询同时服务公开正文页面与 JSON，正文始终按普通文本解释。 */
@WebServlet({"/article", "/api/article"})
public final class ArticleServlet extends HttpServlet {
    private static final Gson JSON = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").create();
    private final BiFunction<String, String, Article> find;

    public ArticleServlet() { this(ArticleServlet::findArticle); }
    ArticleServlet(BiFunction<String, String, Article> find) { this.find = find; }

    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String type;
        String id;
        try {
            type = singleParameter(request, "type");
            id = singleParameter(request, "id");
            if (!("news".equals(type) || "notice".equals(type)) || !id.matches("[A-Za-z0-9_-]{1,40}")) {
                throw new IllegalArgumentException("不支持的内容类型或标识");
            }
        } catch (IllegalArgumentException invalid) {
            render(request, response, null, 400, "阅读地址不正确");
            return;
        }
        Article article = find.apply(type, id);
        render(request, response, article, article == null ? 404 : 200, article == null ? "内容不存在" : null);
    }

    private static String singleParameter(HttpServletRequest request, String name) {
        String[] values = request.getParameterValues(name);
        if (values == null || values.length != 1 || values[0] == null) {
            throw new IllegalArgumentException("正文地址必须包含唯一的 " + name);
        }
        return values[0];
    }

    private static void render(HttpServletRequest request, HttpServletResponse response,
                               Article article, int status, String error) throws IOException, ServletException {
        response.setStatus(status);
        response.setCharacterEncoding("UTF-8");
        if ("/api/article".equals(request.getServletPath())) {
            response.setContentType("application/json");
            response.getWriter().write(JSON.toJson(error == null ? article : Collections.singletonMap("error", error)));
            return;
        }
        request.setAttribute("article", article);
        request.setAttribute("articleError", error);
        request.getRequestDispatcher("/WEB-INF/views/article.jsp").forward(request, response);
    }

    private static Article findArticle(String type, String id) {
        if ("news".equals(type)) {
            News news = new NewsService().get(id);
            return news == null ? null : new Article(type, news.getNewsID(), news.getNewsName(), news.getNewsContent(), news.getCreateTime());
        }
        Notice notice = new NoticeService().get(id);
        return notice == null ? null : new Article(type, notice.getNoticeID(), notice.getNoticeName(), notice.getNoticeContent(), notice.getCreateTime());
    }

    public static final class Article {
        private final String type;
        private final String id;
        private final String title;
        private final String content;
        private final Date createTime;

        Article(String type, String id, String title, String content, Date createTime) {
            this.type = type; this.id = id; this.title = title; this.content = content;
            this.createTime = new Date(createTime.getTime());
        }
        public String getType() { return type; }
        public String getId() { return id; }
        public String getTitle() { return title; }
        public String getContent() { return content; }
        public Date getCreateTime() { return new Date(createTime.getTime()); }
    }
}
