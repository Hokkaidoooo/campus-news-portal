# 校园新闻门户与后台管理系统

前后端一体的校园新闻门户：Vue2 前台页面 + Servlet/JSP 管理后台 + MySQL 数据库。前台展示首页、新闻列表与通知列表；后台管理用户、新闻、通知与导航菜单。

## 技术栈与结构

| 部分 | 技术 | 位置 |
| --- | --- | --- |
| 前台页面 | HTML + Vue 2.7 + axios + jQuery | `src/main/webapp/` 下 `*.html`、`js/`、`css/` |
| 管理后台 | JSP + Bootstrap 模板 + 富文本编辑器（KindEditor / CKEditor 5） | `src/main/webapp/admin/` |
| 后端 | Servlet + Service + JDBC（c3p0 连接池）+ fastjson | `src/main/java/com/citywy/` |
| 数据库 | MySQL 建库建表与数据脚本 | `sql/newsdemo.sql` |

后端按 view / service / util / filter / domain 分层：`view` 是各模块 Servlet（用户、新闻、通知、导航、前台数据接口），`service` 封装业务，`util` 提供连接池、分页与公共方法，`filter` 处理登录校验与跨域。

## 主要接口

| 路径 | 说明 |
| --- | --- |
| `/servlets/LoginServlet.htm` | 后台登录；`web.xml` 配置 SessionFilter 对 `/servlets/*` 与 `/jsp/*` 做登录拦截 |
| `/servlets/UserServlet.htm`、`NewsServlet.htm`、`NoticeServlet.htm` 等 | 各模块增删改查 |
| `CAjaxServlet` | 前台页面数据接口（菜单、新闻、通知） |

## 运行

需要 JDK 8 + Tomcat 8/9 + MySQL：

1. 导入 `sql/newsdemo.sql` 建库建表；
2. 复制 `src/main/resources/jdbc-mysql.properties.example` 为 `jdbc-mysql.properties`，填入本机数据库地址与账号；
3. `mvn package` 生成 WAR 部署到 Tomcat，或直接在 IDE 中配置 Tomcat 运行。

## 说明

- 页面以校园门户风格设计，包含富文本编辑器的完整后台管理流程。
- 数据库连接信息不写死在代码里，由 `jdbc-mysql.properties` 提供。
