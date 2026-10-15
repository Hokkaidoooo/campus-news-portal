<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%
    String base = request.getScheme()+"://"+request.getServerName()+(request.getServerPort()!=80?":"+request.getServerPort():"")+request.getContextPath()+"/";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <base href="<%=base%>">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
    <meta charset="utf-8" />
    <title>首页导航栏管理</title>
    <meta name="description" content="Common form elements and layouts" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
    <!-- bootstrap & fontawesome -->
    <link rel="stylesheet" href="admin/assets/css/bootstrap.min.css" />
    <link rel="stylesheet" href="admin/assets/css/font-awesome.min.css" />
    <!-- page specific plugin styles -->
    <link rel="stylesheet" href="admin/assets/css/colorbox.css" />
    <!-- page specific plugin styles -->
    <link rel="stylesheet" href="admin/assets/css/jquery-ui.custom.min.css" />
    <link rel="stylesheet" href="admin/assets/css/chosen.css" />
    <link rel="stylesheet" href="admin/assets/css/datepicker.css" />
    <link rel="stylesheet" href="admin/assets/css/bootstrap-timepicker.css" />
    <link rel="stylesheet" href="admin/assets/css/daterangepicker.css" />
    <link rel="stylesheet" href="admin/assets/css/bootstrap-datetimepicker.css" />
    <link rel="stylesheet" href="admin/assets/css/colorpicker.css" />
    <!-- text fonts -->
    <link rel="stylesheet" href="admin/assets/css/ace-fonts.css" />
    <!-- ace styles -->
    <link rel="stylesheet" href="admin/assets/css/ace.min.css" id="main-ace-style" />
    <!--[if lte IE 9]>
    <link rel="stylesheet" href="admin/assets/css/ace-part2.min.css" />
    <![endif]-->
    <link rel="stylesheet" href="admin/assets/css/ace-skins.min.css" />
    <link rel="stylesheet" href="admin/assets/css/ace-rtl.min.css" />
    <!--[if lte IE 9]>
    <link rel="stylesheet" href="admin/assets/css/ace-ie.min.css" />
    <![endif]-->
    <!-- inline styles related to this page -->
    <!-- ace settings handler -->
    <script src="admin/assets/js/ace-extra.min.js"></script>
    <!-- HTML5shiv and Respond.js for IE8 to support HTML5 elements and media queries -->
    <!--[if lte IE 8]>
    <script src="admin/assets/js/html5shiv.min.js"></script>
    <script src="admin/assets/js/respond.min.js"></script>
    <![endif]-->
</head>

<body class="no-skin">
<%@include file="../public/top.jsp" %>
<div class="main-container" id="main-container">
    <jsp:include page="../public/left.jsp" flush="true">
        <jsp:param name="now" value="topmenu_list"/>
    </jsp:include>
    <div class="main-content">
        <div class="breadcrumbs" id="breadcrumbs">
            <script type="text/javascript">
                try{ace.settings.check('breadcrumbs' , 'fixed')}catch(e){}
            </script>
            <ul class="breadcrumb">
                <li><i class="ace-icon fa fa-home home-icon"></i><a href="/">首页</a></li>
                <li><a href="${pageContext.request.contextPath}/servlets/topmenu/list.htm">导航栏管理</a></li>
            </ul>
        </div>
        <div class="page-content">
            <div class="page-content-area">
                <div class="row">
                    <div class="col-xs-12">
                        <!-- DataTable Begin -->
                        <div class="row">
                            <div class="col-xs-12">
                                <div class="table-header">
                                    导航栏管理列表
                                </div>
                                <div class="widget-main">
                                    <div class="form-inline">
                                        <div class="form-group" style="display: none">
                                            <label>每页(条):</label>
                                            <select class="form-control" id="fy_pagesize" onchange="fyjz()">
                                                <option value="5">5</option>
                                                <option value="10">10</option>
                                                <option value="25">25</option>
                                                <option value="50">50</option>
                                            </select>
                                        </div>
                                        <div class="form-group" style="display: none">
                                            <label>排序列:</label>
                                            <select class="form-control" id="fy_order" onchange="fyjz()">
                                                <option value="id">默认排序</option>
                                            </select>
                                        </div>
                                        <div class="form-group" style="display: none">
                                            <label>升降序:</label>
                                            <select class="form-control" id="fy_sort" onchange="fyjz()">
                                                <option value="desc">降序</option>
                                                <option value="asc">升序</option>
                                            </select>
                                        </div>
                                        <div class="form-group">
                                            <label></label>
                                            <input type="text" class="form-control" id="fy_name" value="${pageutil.x_name }" onkeyup="fyhc(event)" placeholder="输入搜索关键字">
                                        </div>
                                        <button type="button" class="btn btn-info btn-sm" onclick="fyhc(null)">
                                            <i class="ace-icon fa fa-search bigger-110"></i>搜索
                                        </button>
                                        <button type="button" class="btn btn-info btn-sm" onclick="window.location.href='${pageContext.request.contextPath}/servlets/topmenu/load.htm'">
                                            <i class="ace-icon fa fa-plus-circle bigger-110"></i>添加
                                        </button>
                                    </div>
                                </div>
                                <!-- Table Begin-->
                                <div>
                                    <table id="sample-table-2" class="table table-striped table-bordered table-hover">
                                        <thead>
                                        <tr>
                                            <th class="center">
                                                <label class="position-relative">
                                                    <input type="checkbox" class="ace" />
                                                    <span class="lbl"></span>
                                                </label>
                                            </th>
                                            <th>导航栏编号</th>
                                            <th>导航栏标题</th>
                                            <th>导航栏内容</th>
                                            <th>创建时间</th>
                                            <th class="hidden-480">操作</th>
                                        </tr>
                                        </thead>

                                        <tbody>
                                        <c:if test="${fn:length(list)>0}">
                                            <c:forEach items="${list}" var="topmenu">
                                                <tr>
                                                    <td class="center">
                                                        <label class="position-relative">
                                                            <input type="checkbox" class="ace" />
                                                            <span class="lbl"></span>
                                                        </label>
                                                    </td>
                                                    <td>${topmenu.id }</td>
                                                    <td>${topmenu.topmenuName }</td>
                                                    <td>
                                                        ${topmenu.topmenuContent}
                                                    </td>
                                                    <td class="hidden-480">
                                                        <fmt:formatDate value="${topmenu.createTime }" pattern="yyyy-MM-dd HH:mm:ss"/>
                                                    </td>
                                                    <td>
                                                        <div class="">
                                                            <a class="blue" href="javascript:fybj('${topmenu.id }')">
                                                                <i class="ace-icon fa fa-pencil bigger-130"></i> 修改
                                                            </a>
                                                            &nbsp;
                                                            <a class="red" href="javascript:fysc('${topmenu.id }')">
                                                                <i class="ace-icon fa fa-trash-o bigger-130"></i> 删除
                                                            </a>
                                                        </div>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </c:if>
                                        </tbody>
                                    </table>
                                </div>
                                <!-- Table END -->
                            </div>
                        </div>
                        <!-- DataTable END -->
                    </div>
                </div>
                <!-- 分页 -->
                <%@include file="../public/page.jsp"%>
            </div>
        </div>
    </div>
    <a href="#" id="btn-scroll-up" class="btn-scroll-up btn btn-sm btn-inverse">
        <i class="ace-icon fa fa-angle-double-up icon-only bigger-110"></i>
    </a>
</div>
<!--[if !IE]> -->
<script type="text/javascript">
    window.jQuery || document.write("<script src='admin/assets/js/jquery.min.js'>"+"<"+"/script>");
</script>
<!-- <![endif]-->
<!--[if IE]>
<script type="text/javascript">
    window.jQuery || document.write("<script src='admin/assets/js/jquery1x.min.js'>"+"<"+"/script>");
</script>
<![endif]-->
<script type="text/javascript">
    if('ontouchstart' in document.documentElement) document.write("<script src='admin/assets/js/jquery.mobile.custom.min.js'>"+"<"+"/script>");
</script>
<script src="admin/assets/js/bootstrap.min.js"></script>
<!-- page specific plugin scripts -->
<script src="admin/assets/js/jquery.colorbox-min.js"></script>
<!-- page specific plugin scripts -->
<!--[if lte IE 8]>
<script src="admin/assets/js/excanvas.min.js"></script>
<![endif]-->
<script src="admin/assets/js/jquery-ui.custom.min.js"></script>
<script src="admin/assets/js/jquery.ui.touch-punch.min.js"></script>
<script src="admin/assets/js/chosen.jquery.min.js"></script>
<script src="admin/assets/js/fuelux/fuelux.spinner.min.js"></script>
<script src="admin/assets/js/date-time/bootstrap-datepicker.min.js"></script>
<script src="admin/assets/js/date-time/bootstrap-timepicker.min.js"></script>
<script src="admin/assets/js/date-time/moment.min.js"></script>
<script src="admin/assets/js/date-time/daterangepicker.min.js"></script>
<script src="admin/assets/js/date-time/bootstrap-datetimepicker.min.js"></script>
<script src="admin/assets/js/bootstrap-colorpicker.min.js"></script>
<script src="admin/assets/js/jquery.knob.min.js"></script>
<script src="admin/assets/js/jquery.autosize.min.js"></script>
<script src="admin/assets/js/jquery.inputlimiter.1.3.1.min.js"></script>
<script src="admin/assets/js/jquery.maskedinput.min.js"></script>
<script src="admin/assets/js/bootstrap-tag.min.js"></script>
<script src="admin/assets/js/typeahead.jquery.min.js"></script>
<!-- 自定义js -->
<script src="admin/assets/js/my/page-list.js"></script>
<!-- inline scripts related to this page -->
<script type="text/javascript">
    jQuery(function($) {
        var $overflow = '';
        var colorbox_params = {
            rel: 'colorbox',
            reposition:true,
            scalePhotos:true,
            scrolling:false,
            opacity:0.2,
            previous:'<i class="ace-icon fa fa-arrow-left"></i>',
            next:'<i class="ace-icon fa fa-arrow-right"></i>',
            close:'&times;',
            current:'{current} of {total}',
            maxWidth:'100%',
            maxHeight:'100%',
            onOpen:function(){
                $overflow = document.body.style.overflow;
                document.body.style.overflow = 'hidden';
            },
            onClosed:function(){
                document.body.style.overflow = $overflow;
            },
            onComplete:function(){
                $.colorbox.resize();
            }
        };
        $('[data-rel="colorbox"]').colorbox(colorbox_params);
        $("#cboxLoadingGraphic").html("<i class='ace-icon fa fa-spinner orange'></i>");
    })
</script>
<!-- ace scripts -->
<script src="admin/assets/js/ace-elements.min.js"></script>
<script src="admin/assets/js/ace.min.js"></script>
<script type="text/javascript">
    pubSelected("fy_pagesize",'${pageutil.x_pageSize}');
    pubSelected("fy_order",'${pageutil.x_order}');
    pubSelected("fy_sort",'${pageutil.x_sort}');
    fysz('${pageutil.x_pageNow}',"servlets/topmenu/drop.htm","servlets/topmenu/load.htm","servlets/topmenu/list.htm");
</script>
</body>
</html>
