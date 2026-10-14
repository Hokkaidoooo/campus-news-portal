package com.citywy.view;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.citywy.util.PublicUtil;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.UUID;

public class CAjaxServlet extends HttpServlet {
    /**
     * 2.异步上传单图片
     * 下面的实例依赖于 FileUpload，所以一定要确保在您的 classpath 中有最新版本的 commons-fileupload.x.x.jar 文件。
     * FileUpload 依赖于 Commons IO，所以一定要确保在您的 classpath 中有最新版本的 commons-io-x.x.jar 文件。
     */
    // 上传配置
    private static final int MEMORY_THRESHOLD = 1024 * 1024 * 3;  // 3MB
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 40; // 40MB
    private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 50; // 50MB

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=utf-8");
        String uri = request.getRequestURI();
        String path = uri.substring(uri.lastIndexOf("/") + 1, uri.lastIndexOf("."));
        if (path.equals("menu")) {
            ajaxMenu(request, response);
        } else if (path.equals("pic")) {
            ajaxPicUpload(request, response);
        } else if (path.equals("picedi")) {
            ajaxPicEdiUpload(request, response);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * 1.异步加载栏目
     */
    public void ajaxMenu(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int level = 1;
        String scope = null;
        int pid = 0;
        level = Integer.parseInt(request.getParameter("level"));
        scope = request.getParameter("scope");
        pid = PublicUtil.getParam(request, "pid", 0);

//        List<Menu> ms = new ArrayList<Menu>();
//        if(level==1){
//            ms= new MenuService().find(level,scope);
//        }else{
//            ms= new MenuService().find(level,scope,pid);
//        }
        JSONArray res = new JSONArray();
        JSONObject json = null;
//        for(Menu m:ms){
//            if(m.isState()){	//状态为启用时执行
//                json = new JSONObject();
//                json.put("id",m.getId());
//                json.put("name",m.getName());
//                res.add(json);
//            }
//        }
        response.getWriter().write(res.toString());
    }

    private void ajaxPicUpload(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //防止中文乱码
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        String osName = System.getProperty("os.name").toLowerCase();
        //设置上传路径
        String path = "";
        if (osName.indexOf("window") >= 0) {
            path = "D:/opt/temp/uploadfile";//window下放在D盘...
        } else {
            path = "/data/uploadfile/images";//linux下放在/data...
        }
        PrintWriter writer = response.getWriter();
        // 检测是否为多媒体上传
        if (!ServletFileUpload.isMultipartContent(request)) {
            // 如果不是则停止
            writer.println("Error: 表单必须包含 enctype=multipart/form-data...");
            writer.flush();
            return;
        }
        // 配置上传参数
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // 设置内存临界值 - 超过后将产生临时文件并存储于临时目录中
        factory.setSizeThreshold(MEMORY_THRESHOLD);
        // 设置临时存储目录
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
        ServletFileUpload upload = new ServletFileUpload(factory);
        // 设置最大文件上传值
        upload.setFileSizeMax(MAX_FILE_SIZE);
        // 设置最大请求值 (包含文件和表单数据)
        upload.setSizeMax(MAX_REQUEST_SIZE);
        // 中文处理
        upload.setHeaderEncoding("UTF-8");
        //获取项目发布路径下的upload文件夹
//        String uploadPath = request.getServletContext().getRealPath("./") + File.separator + UPLOAD_DIRECTORY;
        // 如果目录不存在则创建
        File uploadDir = new File(path);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
        try {
            // 解析请求的内容提取文件数据
            List<FileItem> formItems = upload.parseRequest(request);
            if (formItems != null && formItems.size() > 0) {
                // 迭代表单数据
                for (FileItem item : formItems) {
                    // 如果实例表示一个简单的表单字段，返回true;如果表示上传的文件，则为False
                    if (!item.isFormField()) {
                        String name = item.getName();//文件名
                        String ext = name.substring(name.lastIndexOf(".") + 1).toLowerCase();//文件后缀，有需要可以在下面做文件类型判断，是否合法
                        String fileName = UUID.randomUUID() + "." + ext;//文件名，随机生成
                        //File.separator的作用就是保证在任何系统都可以正确表示斜线,windows中是\ linux中是/
                        String filePath = path + File.separator + fileName;
                        File storeFile = new File(filePath);
                        // 在控制台输出文件的上传路径
                        System.out.println("上传路径：" + filePath + "文件名：" + fileName);
                        // 保存文件到硬盘
                        item.write(storeFile);
                        //注意编码，不然返回前端会乱码！
                        //writer.println(new String("上传成功！".getBytes("utf-8"),"iso-8859-1"));
                        writer.println("static.citywy.com/pic/" + fileName);//返回前端文件在tomcat配置的路径 /
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // 跳转到 message.jsp,这里使用了异步上传，所以不用跳转页面
        //request.getRequestDispatcher("/message.jsp").forward(request, response);
    }

    /**
     * ckEditor编辑器上传图片专用
     *
     * @param request
     * @param response
     * @throws IOException
     */
    private void ajaxPicEdiUpload(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //防止中文乱码
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        String osName = System.getProperty("os.name").toLowerCase();
        //设置上传路径
        String path = "";
        if (osName.indexOf("window") >= 0) {
            path = "D:/opt/temp/uploadfile";//window下放在D盘...
        } else {
            path = "/data/uploadfile/images";//linux下放在/data...
        }
        // 检测是否为多媒体上传
        if (!ServletFileUpload.isMultipartContent(request)) {
            return;
        }
        // 配置上传参数
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // 设置内存临界值 - 超过后将产生临时文件并存储于临时目录中
        factory.setSizeThreshold(MEMORY_THRESHOLD);
        // 设置临时存储目录
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
        ServletFileUpload upload = new ServletFileUpload(factory);
        // 设置最大文件上传值
        upload.setFileSizeMax(MAX_FILE_SIZE);
        // 设置最大请求值 (包含文件和表单数据)
        upload.setSizeMax(MAX_REQUEST_SIZE);
        // 中文处理
        upload.setHeaderEncoding("UTF-8");
        //获取项目发布路径下的upload文件夹
//        String uploadPath = request.getServletContext().getRealPath("./") + File.separator + UPLOAD_DIRECTORY;
        // 如果目录不存在则创建
        File uploadDir = new File(path);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
        try {
            // 解析请求的内容提取文件数据
            List<FileItem> formItems = upload.parseRequest(request);
            if (formItems != null && formItems.size() > 0) {
                // 迭代表单数据
                for (FileItem item : formItems) {
                    // 如果实例表示一个简单的表单字段，返回true;如果表示上传的文件，则为False
                    if (!item.isFormField()) {
                        String name = item.getName();//文件名
                        String ext = name.substring(name.lastIndexOf(".") + 1).toLowerCase();//文件后缀，有需要可以在下面做文件类型判断，是否合法
                        String fileName = UUID.randomUUID() + "." + ext;//文件名，随机生成
                        //File.separator的作用就是保证在任何系统都可以正确表示斜线,windows中是\ linux中是/
                        String filePath = path + File.separator + fileName;
                        File storeFile = new File(filePath);
                        // 在控制台输出文件的上传路径
                        System.out.println("上传路径：" + filePath + "文件名：" + fileName);
                        // 保存文件到硬盘
                        item.write(storeFile);
                        //注意编码，不然返回前端会乱码！
                        //writer.println(new String("上传成功！".getBytes("utf-8"),"iso-8859-1"));
//                        writer.println("static.citywy.com/pic/"+fileName);//返回前端文件在tomcat配置的路径 /
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("url", filePath);
                        response.getWriter().write(jsonObject.toString());
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
