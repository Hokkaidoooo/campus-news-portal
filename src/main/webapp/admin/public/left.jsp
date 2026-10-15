<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!-- #section:basics/sidebar -->
<div id="sidebar" class="sidebar responsive" data-home="<c:url value='/'/>"
     data-current-menu="<c:out value='${param.now}'/>" data-message="<c:out value='${requestScope.message}'/>">
    <script type="text/javascript">
        try {
            ace.settings.check('sidebar', 'fixed')
        } catch (e) {
        }
    </script>

    <div class="sidebar-shortcuts" id="sidebar-shortcuts">
        <div class="sidebar-shortcuts-large" id="sidebar-shortcuts-large">

            <!-- /section:basics/sidebar.layout.shortcuts -->
        </div>

        <div class="sidebar-shortcuts-mini" id="sidebar-shortcuts-mini">

        </div>
    </div><!-- /.sidebar-shortcuts -->

    <ul class="nav nav-list">
        <li class="" id="index">
            <a href="${pageContext.request.contextPath}/servlets/index.htm">
                <i class="menu-icon fa fa-tachometer"></i>
                <span class="menu-text"> 首页 </span>
            </a>
            <b class="arrow"></b>
        </li>

        <li class="">
            <a href="${pageContext.request.contextPath}/servlets/news/list.htm">
                <i class="menu-icon fa fa-tachometer"></i>
                <span class="menu-text"> 动态管理 </span>
            </a>
            <b class="arrow"></b>
        </li>
        <li class="">
            <a href="${pageContext.request.contextPath}/servlets/notice/list.htm">
                <i class="menu-icon fa fa-tachometer"></i>
                <span class="menu-text"> 公告管理 </span>
            </a>
            <b class="arrow"></b>
        </li>
        <li class="">
            <a href="${pageContext.request.contextPath}/servlets/topmenu/list.htm">
                <i class="menu-icon fa fa-tachometer"></i>
                <span class="menu-text"> 导航栏管理 </span>
            </a>
            <b class="arrow"></b>
        </li>
        <li class="">
            <a href="${pageContext.request.contextPath}/servlets/leftmenu/list.htm">
                <i class="menu-icon fa fa-tachometer"></i>
                <span class="menu-text"> 侧边栏管理 </span>
            </a>
            <b class="arrow"></b>
        </li>
        <c:if test="${canManageUsers}">
        <li class="">
            <a href="${pageContext.request.contextPath}/servlets/user/list.htm">
                <i class="menu-icon fa fa-tag"></i>
                <span class="menu-text">用户管理</span>

                <b class="arrow "></b>
            </a>

            <b class="arrow"></b>
        </li>
        </c:if>
    </ul><!-- /.nav-list -->

    <!-- #section:basics/sidebar.layout.minimize -->
    <div class="sidebar-toggle sidebar-collapse" id="sidebar-collapse">
<%--        <i class="ace-icon fa fa-angle-double-left" data-icon1="ace-icon fa fa-angle-double-left"--%>
<%--           data-icon2="ace-icon fa fa-angle-double-right"></i>--%>
    </div>
    <script src="<c:url value='/admin/assets/js/jquery.min.js'/>"></script>
    <script src="<c:url value='/admin/assets/js/bootbox.min.js'/>"></script>
    <script src="<c:url value='/admin/assets/js/my/public.js'/>"></script>
    <!-- /section:basics/sidebar.layout.minimize -->
    <script type="text/javascript">
        try {
            ace.settings.check('sidebar', 'collapsed')
        } catch (e) {
        }
        var ckdialog;
        $(document).ready(function () {
            //设置左侧导航被选中
            var sidebar = document.getElementById('sidebar');
            var now = $(document.getElementById(sidebar.getAttribute('data-current-menu')));
            now.addClass("active");
            now.parent().css('display', 'block');
            now.parent().parent().addClass("active");
            //数据弹窗
            var tip = sidebar.getAttribute('data-message');
            if (tip) {
                cktc_tanchuang(tip);
            }
            $(document).on('click', '.bootbox', function () {
                ckdialog.modal('hide');
            });
        });

        function cktc_tanchuang(cktc_tip) {
            ckdialog = bootbox.dialog({
                message: $('<p class="text-center mb-0" style="font-size:15px;"></p>').text(cktc_tip),
                closeButton: false
            });
        }
        //新建ink对象，保存ink.home,
        var ink = {};
        ink.home = document.getElementById('sidebar').getAttribute('data-home');
    </script>
</div>
