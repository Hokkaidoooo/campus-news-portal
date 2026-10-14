<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
    String base = request.getScheme()+"://"+request.getServerName()+(request.getServerPort()!=80?":"+request.getServerPort():"")+request.getContextPath()+"/";
    Object message = request.getAttribute("message");
    if(message!=null){
        out.print("<script type=\"text/javascript\">window.alert(\""+message.toString()+"\");window.location=\""+base+"\";</script>");
    }
%>
<!DOCTYPE html>
<html>
<head>
    <base href="<%=base%>">
    <title>登录-servlet demo</title>
    <meta http-equiv="content-type" content="text/html;charset=UTF-8">
    <meta name="description" content="用户登录" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
    <!-- bootstrap & fontawesome -->
    <link rel="stylesheet" href="../admin/assets/css/bootstrap.min.css" />
    <link rel="stylesheet" href="../admin/assets/css/font-awesome.min.css" />
    <!-- text fonts -->
    <link rel="stylesheet" href="../admin/assets/css/ace-fonts.css" />
    <!-- ace styles -->
    <link rel="stylesheet" href="../admin/assets/css/ace.min.css" />
    <!--[if lte IE 9]>
    <link rel="stylesheet" href="../admin/assets/css/ace-part2.min.css" />
    <![endif]-->
    <link rel="stylesheet" href="../admin/assets/css/ace-rtl.min.css" />
    <!--[if lte IE 9]>
    <link rel="stylesheet" href="../admin/assets/css/ace-ie.min.css" />
    <![endif]-->
    <link rel="stylesheet" href="../admin/assets/css/ace.onpage-help.css" />
    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
    <script src="../admin/assets/js/html5shiv.js"></script>
    <script src="../admin/assets/js/respond.min.js"></script>
    <![endif]-->
</head>
<body class="login-layout  blur-login">
<div class="main-container">
    <div class="main-content">
        <div class="row">
            <div class="col-sm-10 col-sm-offset-1">
                <div class="login-container">
                    <div style="height: 63px">&nbsp;</div>
                    <div class="center">
                        <h1>
                            <i class="ace-icon fa fa-leaf green"></i>
                            <span class="red"></span>
                            <span class="white" id="id-text2">用户登录</span>
                        </h1>
                        <h4 class="blue" id="id-company-text"> </h4>
                    </div>
                    <div class="space-6"></div>
                    <div class="position-relative">
                        <div id="login-box" class="login-box visible widget-box no-border">
                            <div class="widget-body">
                                <div class="widget-main">
                                    <h4 class="header blue lighter bigger">
                                        <i class="ace-icon fa fa-coffee green"></i>
                                        用户登录
                                    </h4>
                                    <div class="space-6"></div>
                                    <form onsubmit="return validateForm()" action="/servlets/LoginServlet.htm" method="post">
                                        <fieldset>
                                            <label class="block clearfix">
                                                    <span class="block input-icon input-icon-right">
                                                        <input type="text" class="form-control" name="uname" placeholder="请输入用户名" />
                                                        <i class="ace-icon fa fa-user"></i>
                                                    </span>
                                            </label>
                                            <label class="block clearfix">
                                                    <span class="block input-icon input-icon-right">
                                                        <input type="password" class="form-control" name="upwd" placeholder="请输入密码" />
                                                        <i class="ace-icon fa fa-lock"></i>
                                                    </span>
                                            </label>
                                            <label class="block clearfix">
                                                    <span class="block input-icon input-icon-right">
                                                        <input type="text" class="form-control" name="verificationCode" placeholder="请输入验证码" />
                                                        <i class="ace-icon fa fa-shield"></i>
                                                        <span class="verification-code" id="verificationCode"></span>
                                                    </span>
                                            </label>
                                            <div class="space"></div>
                                            <div class="clearfix" style="text-align:center;">
                                                <button type="submit" class="width-35 btn btn-sm btn-primary">
                                                    <i class="ace-icon fa fa-key"></i>
                                                    <span class="bigger-110">登录</span>
                                                </button>
                                            </div>
                                            <div class="space-4"></div>
                                        </fieldset>
                                    </form>
                                </div><!-- /.widget-main -->
                            </div><!-- /.widget-body -->
                        </div><!-- /.login-box -->
                    </div>
                </div>
            </div><!-- /.col -->
        </div><!-- /.row -->
    </div><!-- /.main-content -->
</div><!-- /.main-container -->

<!-- basic scripts -->

<!--[if !IE]> -->
<script type="text/javascript">
    window.jQuery || document.write("<script src='../admin/assets/js/jquery.min.js'>"+"<"+"/script>");
</script>

<!-- <![endif]-->

<!--[if IE]>
<script type="text/javascript">
    window.jQuery || document.write("<script src='../admin/assets/js/jquery1x.min.js'>"+"<"+"/script>");
</script>
<![endif]-->
<script type="text/javascript">
    if('ontouchstart' in document.documentElement) document.write("<script src='../admin/assets/js/jquery.mobile.custom.min.js'>"+"<"+"/script>");
</script>

<!-- inline scripts related to this page -->
<script type="text/javascript">
    jQuery(function($) {
        $(document).on('click', '.toolbar a[data-target]', function(e) {
            e.preventDefault();
            var target = $(this).data('target');
            $('.widget-box.visible').removeClass('visible');//hide others
            $(target).addClass('visible');//show target
        });
    });

    //you don't need this, just used for changing background
    jQuery(function($) {
        $('#btn-login-dark').on('click', function(e) {
            $('body').attr('class', 'login-layout');
            $('#id-text2').attr('class', 'white');
            $('#id-company-text').attr('class', 'blue');

            e.preventDefault();
        });
        $('#btn-login-light').on('click', function(e) {
            $('body').attr('class', 'login-layout light-login');
            $('#id-text2').attr('class', 'grey');
            $('#id-company-text').attr('class', 'blue');

            e.preventDefault();
        });
        $('#btn-login-blur').on('click', function(e) {
            $('body').attr('class', 'login-layout blur-login');
            $('#id-text2').attr('class', 'white');
            $('#id-company-text').attr('class', 'light-blue');
            e.preventDefault();
        });
    });

    function generateVerificationCode() {
        const characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        const codeLength = 6;
        let code = '';

        for (let i = 0; i < codeLength; i++) {
            const index = Math.floor(Math.random() * characters.length);
            code += characters.charAt(index);
        }

        return code;
    }

    function validateForm() {

        const userCode = document.forms[0]["verificationCode"].value;
        const generatedCode = $("#verificationCode").text();

        if (userCode !== generatedCode) {
            alert("验证码不正确，请重新输入。");
            return false;
        }

        return true;
    }
    $(document).ready(function () {
        const verificationCode = generateVerificationCode();
        $("#verificationCode").text(verificationCode);
    });
</script>
</body>
</html>
