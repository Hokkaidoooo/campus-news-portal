<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!doctype html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${empty articleError ? article.title : articleError}"/> · 河海大学商学院</title>
    <link rel="stylesheet" href="<c:url value='/css/article.css'/>">
</head>
<body>
<header class="site-header">
    <a href="<c:url value='/index.html'/>">河海大学商学院</a>
    <nav aria-label="网站导航">
        <a href="<c:url value='/newslist.html'/>">最新动态</a>
        <a href="<c:url value='/noticelist.html'/>">通知公告</a>
    </nav>
</header>
<main>
    <c:choose>
        <c:when test="${not empty articleError}">
            <h1><c:out value="${articleError}"/></h1>
            <p>请核对地址，或返回列表选择其他内容。</p>
            <a href="<c:url value='/index.html'/>">返回新闻首页</a>
        </c:when>
        <c:otherwise>
            <article>
                <p class="category">${article.type == 'news' ? '最新动态' : '通知公告'}</p>
                <h1><c:out value="${article.title}"/></h1>
                <p class="published">发布时间：<fmt:formatDate value="${article.createTime}" pattern="yyyy-MM-dd HH:mm"/></p>
                <div class="article-content"><c:out value="${article.content}"/></div>
            </article>
            <p class="return-link">
                <c:choose>
                    <c:when test="${article.type == 'news'}"><a href="<c:url value='/newslist.html'/>">返回动态列表</a></c:when>
                    <c:otherwise><a href="<c:url value='/noticelist.html'/>">返回公告列表</a></c:otherwise>
                </c:choose>
            </p>
        </c:otherwise>
    </c:choose>
</main>
</body>
</html>
