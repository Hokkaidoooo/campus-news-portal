<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%
    String base = request.getScheme() + "://" + request.getServerName() + (request.getServerPort() != 80 ? ":" + request.getServerPort() : "") + request.getContextPath() + "/";
    Object message = request.getAttribute("message");
    if (message == null) {
        message = request.getParameter("message");
    }
%>
<!-- #section:basics/sidebar -->
<div id="sidebar" class="sidebar responsive">
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
            <a href="/servlets/index.htm">
                <i class="menu-icon fa fa-tachometer"></i>
                <span class="menu-text"> 首页 </span>
            </a>
            <b class="arrow"></b>
        </li>

        <li class="">
            <a href="/servlets/news/list.htm">
                <i class="menu-icon fa fa-tachometer"></i>
                <span class="menu-text"> 动态管理 </span>
            </a>
            <b class="arrow"></b>
        </li>
        <li class="">
            <a href="/servlets/notice/list.htm">
                <i class="menu-icon fa fa-tachometer"></i>
                <span class="menu-text"> 公告管理 </span>
            </a>
            <b class="arrow"></b>
        </li>
        <li class="">
            <a href="/servlets/topmenu/list.htm">
                <i class="menu-icon fa fa-tachometer"></i>
                <span class="menu-text"> 导航栏管理 </span>
            </a>
            <b class="arrow"></b>
        </li>
        <li class="">
            <a href="/servlets/leftmenu/list.htm">
                <i class="menu-icon fa fa-tachometer"></i>
                <span class="menu-text"> 侧边栏管理 </span>
            </a>
            <b class="arrow"></b>
        </li>
        <li class="">
            <a href="/servlets/user/list.htm">
                <i class="menu-icon fa fa-tag"></i>
                <span class="menu-text">用户管理</span>

                <b class="arrow "></b>
            </a>

            <b class="arrow"></b>
        </li>
    </ul><!-- /.nav-list -->

    <!-- #section:basics/sidebar.layout.minimize -->
    <div class="sidebar-toggle sidebar-collapse" id="sidebar-collapse">
<%--        <i class="ace-icon fa fa-angle-double-left" data-icon1="ace-icon fa fa-angle-double-left"--%>
<%--           data-icon2="ace-icon fa fa-angle-double-right"></i>--%>
    </div>
    <script src="../admin/assets/js/jquery.min.js"></script>
    <script src="../admin/assets/js/bootbox.min.js"></script>
    <script src="../admin/assets/js/my/public.js"></script>
    <!-- /section:basics/sidebar.layout.minimize -->
    <script type="text/javascript">
        try {
            ace.settings.check('sidebar', 'collapsed')
        } catch (e) {
        }
        var ckdialog;
        $(document).ready(function () {
            //设置左侧导航被选中
            var now = $("#<%=request.getParameter("now")%>");
            now.addClass("active");
            now.parent().css('display', 'block');
            now.parent().parent().addClass("active");
            //数据弹窗
            var tip = "<%=message!=null?message.toString():""%>";
            if (tip) {
                cktc_tanchuang(tip);
            }
            $(document).on('click', '.bootbox', function () {
                ckdialog.modal('hide');
            });
        });

        function cktc_tanchuang(cktc_tip) {
            ckdialog = bootbox.dialog({
                message: '<p class="text-center mb-0" style="font-size:15px;">' + cktc_tip + '</p>',
                closeButton: false
            });
        }
        //新建ink对象，保存ink.home,
        var ink = {};
        ink.home = "<%=base%>";
    </script>
</div>