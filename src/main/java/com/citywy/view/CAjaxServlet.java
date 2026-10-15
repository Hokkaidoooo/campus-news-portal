package com.citywy.view;

import com.citywy.storage.ImageStorage;
import com.citywy.storage.UploadException;
import com.google.gson.Gson;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.*;

@MultipartConfig(fileSizeThreshold=0, maxFileSize=ImageStorage.MAX_BYTES, maxRequestSize=ImageStorage.MAX_BYTES + 16384)
public final class CAjaxServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendError(405, "上传接口只接受 POST");
    }
    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String route = request.getPathInfo();
        if (!"/pic.htm".equals(route) && !"/picedi.htm".equals(route)) {
            response.sendError(404, "接口不存在");
            return;
        }
        if (request.getContentType() == null || !request.getContentType().toLowerCase(Locale.ROOT).startsWith("multipart/form-data")) {
            response.sendError(415, "需要 multipart/form-data");
            return;
        }
        Collection<Part> parts;
        try { parts = request.getParts(); }
        catch (IllegalStateException error) { response.sendError(413, "上传请求超过大小限制"); return; }
        try {
            String callback = request.getParameter("CKEditorFuncNum");
            if (callback != null && (!"/picedi.htm".equals(route) || !callback.matches("[0-9]{1,8}")))
                throw new UploadException(400, "无效的编辑器回调编号");
            List<Part> files = new ArrayList<>();
            for (Part part : parts) if (part.getSubmittedFileName() != null) files.add(part);
            if (files.size() != 1) throw new UploadException(400, "每次必须上传一张图片");
            Part file = files.get(0);
            ImageStorage storage = (ImageStorage) getServletContext().getAttribute(ImageStorage.CONTEXT_KEY);
            String id;
            try (InputStream input = file.getInputStream()) { id = storage.store(file.getSubmittedFileName(), file.getContentType(), input); }
            String url = request.getContextPath() + "/uploads/" + id;
            if ("/pic.htm".equals(route)) {
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write(url);
            } else {
                if (callback != null) {
                    response.setContentType("text/html;charset=UTF-8");
                    response.getWriter().write("<script>window.parent.CKEDITOR.tools.callFunction(" + callback + "," + new Gson().toJson(url) + ",\"\");</script>");
                    return;
                }
                Map<String,Object> result = new LinkedHashMap<>();
                result.put("uploaded", 1); result.put("fileName", id); result.put("url", url);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(new Gson().toJson(result));
            }
        } catch (UploadException error) { response.sendError(error.getStatus(), error.getMessage()); }
        finally { for (Part part : parts) part.delete(); }
    }
}
