<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html><html lang="zh-CN"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>内容管理 · 校园新闻</title>
<style>
*{box-sizing:border-box}body{margin:0;background:#f3f6f9;color:#19354b;font:16px/1.6 system-ui,"Microsoft YaHei",sans-serif}header,main{max-width:1100px;margin:auto;padding:28px}header{display:flex;justify-content:space-between;gap:24px;flex-wrap:wrap;border-bottom:1px solid #d8e3ec}a{color:#165d89}h1{margin:8px 0;font-size:32px}.intro{color:#526b7b}.cards{display:grid;grid-template-columns:repeat(auto-fit,minmax(235px,1fr));gap:20px;margin:32px 0}.card{display:block;background:white;border:1px solid #dce6ef;border-radius:12px;padding:26px;text-decoration:none;box-shadow:0 8px 24px #102d4307}.card strong{font-size:21px;display:block;margin-bottom:10px}.card span{color:#526b7b}a:focus{outline:3px solid #6eaccf;outline-offset:4px}
</style></head><body><header><strong>校园新闻 · 内容管理</strong><div><c:out value="${sessionScope.username}"/>　<a href="${pageContext.request.contextPath}/servlets/LoginServlet.htm?type=logout">退出登录</a></div></header>
<main><h1>管理校园内容</h1><p class="intro">发布新闻与通知，维护栏目和后台账号。</p><nav class="cards" aria-label="管理功能">
<a class="card" href="${pageContext.request.contextPath}/servlets/news/list.htm"><strong>新闻动态</strong><span>查看、发布和编辑校园新闻</span></a>
<a class="card" href="${pageContext.request.contextPath}/servlets/notice/list.htm"><strong>通知公告</strong><span>维护通知内容与发布日期</span></a>
<a class="card" href="${pageContext.request.contextPath}/servlets/topmenu/list.htm"><strong>顶部导航</strong><span>管理门户的主要栏目</span></a>
<a class="card" href="${pageContext.request.contextPath}/servlets/leftmenu/list.htm"><strong>侧边栏目</strong><span>整理栏目下的内容入口</span></a>
<a class="card" href="${pageContext.request.contextPath}/servlets/user/list.htm"><strong>后台账号</strong><span>管理用户资料、密码和启用状态</span></a>
</nav><a href="${pageContext.request.contextPath}/index.html">查看新闻首页 →</a></main></body></html>
