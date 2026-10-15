package com.citywy.view;

import com.citywy.storage.ImageStorage;
import com.citywy.storage.UploadException;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/uploads/*")
public final class ImageServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = request.getPathInfo();
        try {
            ImageStorage storage = (ImageStorage) getServletContext().getAttribute(ImageStorage.CONTEXT_KEY);
            byte[] bytes = storage.read(path == null ? null : path.substring(1));
            response.setContentType("image/png");
            response.setHeader("X-Content-Type-Options", "nosniff");
            response.setContentLength(bytes.length);
            response.getOutputStream().write(bytes);
        } catch (UploadException error) { response.sendError(error.getStatus(), error.getMessage()); }
    }
}
