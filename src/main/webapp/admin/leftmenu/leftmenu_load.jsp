<%@page import="java.util.Map"%>
<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    String base = request.getScheme()+"://"+request.getServerName()+(request.getServerPort()!=80?":"+request.getServerPort():"")+request.getContextPath()+"/";
    Map<String,String> menus = (Map<String,String>) request.getAttribute("menus");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <base href="<%=base%>">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
    <meta charset="utf-8" />
    <title>添加、修改侧边栏</title>
    <meta name="description" content="Common form elements and layouts" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
    <!-- bootstrap & fontawesome -->
    <link rel="stylesheet" href="admin/assets/css/bootstrap.min.css" />
    <link rel="stylesheet" href="admin/assets/css/font-awesome.min.css" />
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
        <jsp:param name="now" value="leftmenu_load"/>
    </jsp:include>
    <div class="main-content">
        <div class="breadcrumbs" id="breadcrumbs">
            <script type="text/javascript">
                try{ace.settings.check('breadcrumbs' , 'fixed')}catch(e){}
            </script>
            <ul class="breadcrumb">
                <li>
                    <i class="ace-icon fa fa-home home-icon"></i>
                    <a href="#">首页</a>
                </li>
                <li>
                    <a href="#">添加、修改侧边栏</a>
                </li>
            </ul>
        </div>
        <div class="page-content">
            <div class="page-content-area">
                <div class="row">
                    <div class="col-xs-12">
                        <!-- PAGE CONTENT BEGINS -->
                        <form action="${pageContext.request.contextPath}/servlets/leftmenu/modify.htm" method="post" class="form-horizontal">
                            <div class="col-sm-9">
                                <div class="form-group">
                                    <label class="col-sm-3 control-label">侧边栏标题:</label>
                                    <div class="col-sm-9">
                                        <input type="text" class="form-control" value="${pages.leftmenuName }" name="leftmenuName">
                                        <small class="text-warning">必填项</small>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label class="col-sm-3 control-label">侧边栏内容:</label>
                                    <div class="col-sm-9">
                                        <input type="text" class="form-control" value="${pages.leftmenuContent }" name="leftmenuContent">
                                        <small class="text-warning">必填项</small>
                                    </div>
                                </div>
                            </div>
                            <div class="col-sm-11 control-label">
                                <!-- 修改时传递的ID -->
                                <input type="hidden" value="${pages.leftmenuID}" name="leftmenuID">
                                <!-- 上传图片 -->
                                <input type="file" style="display:none;" name="fileToPic" id="fileToPic">
                                <!-- 栏目 -->
                                <input type="hidden" name="Emenu" id="d_memu">
                                <!-- 地址传递 -->
                                <input type="hidden" value="" name="locationhref" id="locationhref">
                                <div class="clearfix form-group">
                                    <div class="col-md-offset-3 col-md-9">
                                        <button class="btn" type="reset">
                                            <i class="ace-icon fa fa-undo bigger-110"></i>
                                            重置
                                        </button>
                                        &nbsp; &nbsp; &nbsp;
                                        <button class="btn btn-info" type="submit" id="submit">
                                            <i class="ace-icon fa fa-check bigger-110"></i>
                                            提交
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    $(document).ready(function(){
        //栏目加载
        window.setTimeout("lmpd(1)",50);
        // window.setTimeout("lmpd(2)",1000);

        //地址传值
        var locationhref = location.href;
        if(locationhref.indexOf('?')!=-1){
            locationhref = locationhref.substring(locationhref.indexOf('?'));
            $("#locationhref").val(locationhref);
        }else{
            $("#locationhref").val("");
        }


    });

</script>
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
<!-- ace scripts -->
<script src="admin/assets/js/ace-elements.min.js"></script>
<script src="admin/assets/js/ace.min.js"></script>
<!-- CAIKE common js Start-->
<script src="admin/assets/js/plugin.js"></script>
<script src="admin/assets/js/my/file-upload.js"></script>
<script src="admin/assets/js/my/load-menu.js"></script>
<script type="text/javascript" src="/editor/kindeditor/kindeditor.js"></script>
<script type="text/javascript">
    //编辑器初始化
    var editor=null;
    KindEditor.ready(function(K){
        editor=K.create("#d_content",{
            urlType:"domain",
            cssPath:"<%=base%>editor/kindeditor/plugins/code/prettify.css",
            uploadJson:ink.home+"admin/ajax/ckeditor.htm",
            fileManagerJson:null,
            allowFileManager:false
        });
    });
</script>
<!-- CAIKE common js END-->
</body>
</html>
