<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="zh-CN"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>登录 · 校园新闻</title>
<style>
*{box-sizing:border-box}body{margin:0;background:#eff3f7;color:#173047;font:16px/1.6 system-ui,"Microsoft YaHei",sans-serif;min-height:100vh;display:grid;place-items:center;padding:24px}.login{width:100%;max-width:410px;background:white;padding:36px;border:1px solid #dce4ea;border-radius:16px;box-shadow:0 18px 50px #17304712}h1{font-size:26px;margin:8px 0 24px}.brand{font-size:13px;color:#46677e}label{display:block;margin:18px 0 6px}input,button{font:inherit;width:100%;padding:12px;border-radius:7px}input{border:1px solid #aebfcf}button{border:0;background:#175a86;color:white;margin:24px 0 12px;cursor:pointer}a{color:#175a86}.error{color:#a32d2d}input:focus,button:focus,a:focus{outline:3px solid #77b0d4;outline-offset:3px}
</style></head><body><main class="login"><p class="brand">校园新闻 · 内容管理</p><h1>登录后台</h1>
<c:if test="${not empty message}"><p class="error" role="alert"><c:out value="${message}"/></p></c:if>
<form action="${pageContext.request.contextPath}/servlets/LoginServlet.htm" method="post">
<label for="uname">用户名</label><input id="uname" name="uname" autocomplete="username" required>
<label for="upwd">密码</label><input id="upwd" name="upwd" type="password" autocomplete="current-password" required>
<button type="submit">登录</button></form><a href="${pageContext.request.contextPath}/index.html">返回新闻首页</a>
</main></body></html>
