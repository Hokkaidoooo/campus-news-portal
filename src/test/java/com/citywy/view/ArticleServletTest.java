package com.citywy.view;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import static org.junit.Assert.*;

public class ArticleServletTest {
    @Test public void badOrRepeatedParametersNeverReachStorage() throws Exception {
        for (String[] types : new String[][]{null, {"user"}, {"news", "notice"}}) assertInvalid(types, new String[]{"valid-id"});
        for (String[] ids : new String[][]{null, {""}, {" "}, {"../bad"}, {"a", "b"}, {new String(new char[41]).replace('\0', 'a')}}) {
            assertInvalid(new String[]{"news"}, ids);
        }
    }

    @Test public void missingRecordIs404InBothRepresentations() throws Exception {
        for (String path : new String[]{"/api/article", "/article"}) {
            Exchange exchange = new Exchange(path, new String[]{"notice"}, new String[]{"missing"});
            exchange.run(new ArticleServlet((type, id) -> null));
            assertEquals(404, exchange.status);
            if (path.equals("/api/article")) assertTrue(exchange.body.toString().contains("内容不存在"));
            else assertEquals("内容不存在", exchange.attributes.get("articleError"));
        }
    }

    @Test public void apiReturnsExactPlainTextWithStableFieldNames() throws Exception {
        Exchange exchange = new Exchange("/api/article", new String[]{"news"}, new String[]{"valid-id"});
        String content = "第一行\n<script>example()</script>\"&";
        exchange.run(new ArticleServlet((type, id) -> new ArticleServlet.Article(type, id, "标题\"<", content, new Date(0))));
        assertEquals(200, exchange.status);
        assertEquals("application/json", exchange.contentType);
        JsonObject json = JsonParser.parseString(exchange.body.toString()).getAsJsonObject();
        assertEquals("news", json.get("type").getAsString());
        assertEquals("valid-id", json.get("id").getAsString());
        assertEquals("标题\"<", json.get("title").getAsString());
        assertEquals(content, json.get("content").getAsString());
        assertTrue(json.get("createTime").getAsString().matches("\\d{4}-.*T.*(Z|[+-]\\d{2}:\\d{2})"));
    }

    @Test public void htmlUsesTheSameRecordAndOnePrivateView() throws Exception {
        ArticleServlet.Article article = new ArticleServlet.Article("notice", "id-1", "标题", "内容", new Date(0));
        Exchange exchange = new Exchange("/article", new String[]{"notice"}, new String[]{"id-1"});
        exchange.run(new ArticleServlet((type, id) -> article));
        assertEquals(200, exchange.status);
        assertEquals("/WEB-INF/views/article.jsp", exchange.forwarded);
        assertSame(article, exchange.attributes.get("article"));
    }

    @Test public void storageFailureDoesNotBecomeEmptyContentOr404() {
        Exchange exchange = new Exchange("/api/article", new String[]{"news"}, new String[]{"valid-id"});
        IllegalStateException failure = new IllegalStateException("database unavailable");
        assertSame(failure, assertThrows(IllegalStateException.class,
                () -> exchange.run(new ArticleServlet((type, id) -> { throw failure; }))));
    }

    private static void assertInvalid(String[] types, String[] ids) throws Exception {
        Exchange exchange = new Exchange("/api/article", types, ids);
        exchange.run(new ArticleServlet((type, id) -> { throw new AssertionError("无效请求不应查询数据库"); }));
        assertEquals(400, exchange.status);
    }

    @SuppressWarnings("unchecked") private static <T> T proxy(Class<T> type, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, handler);
    }

    private static final class Exchange {
        final String path;
        final String[] types, ids;
        final StringWriter body = new StringWriter();
        final Map<String, Object> attributes = new HashMap<>();
        int status;
        String contentType, forwarded;

        Exchange(String path, String[] types, String[] ids) { this.path = path; this.types = types; this.ids = ids; }

        void run(ArticleServlet servlet) throws Exception {
            HttpServletRequest request = proxy(HttpServletRequest.class, (object, method, args) -> {
                switch (method.getName()) {
                    case "getParameterValues": return args[0].equals("type") ? types : ids;
                    case "getServletPath": return path;
                    case "setAttribute": attributes.put((String) args[0], args[1]); return null;
                    case "getRequestDispatcher":
                        String destination = (String) args[0];
                        return proxy(RequestDispatcher.class, (dispatcher, operation, arguments) -> {
                            if (!operation.getName().equals("forward")) throw new AssertionError(operation.getName());
                            forwarded = destination; return null;
                        });
                    default: throw new AssertionError("Unexpected request method: " + method.getName());
                }
            });
            HttpServletResponse response = proxy(HttpServletResponse.class, (object, method, args) -> {
                switch (method.getName()) {
                    case "setStatus": status = (Integer) args[0]; return null;
                    case "setCharacterEncoding": return null;
                    case "setContentType": contentType = (String) args[0]; return null;
                    case "getWriter": return new PrintWriter(body);
                    default: throw new AssertionError("Unexpected response method: " + method.getName());
                }
            });
            servlet.doGet(request, response);
        }
    }
}
