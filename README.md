# 校园新闻门户与后台管理系统

包含 Vue 2 门户页面和 Servlet/JSP 管理后台的校园新闻系统。前台展示导航、新闻与通知；后台维护内容、菜单和用户，通过 JDBC 访问 MySQL。

## 从哪里开始

| 内容 | 入口 | 作用 |
| --- | --- | --- |
| 门户首页 | [index.html](src/main/webapp/index.html) | 导航与首页内容 |
| 栏目及列表页 | [index2.html](src/main/webapp/index2.html) · [newslist.html](src/main/webapp/newslist.html) · [noticelist.html](src/main/webapp/noticelist.html) | 新闻、通知及栏目页面 |
| 管理后台 | [admin/](src/main/webapp/admin/) | JSP、Bootstrap 与富文本编辑器页面 |
| 前台数据 | [DataServlet.java](src/main/java/com/citywy/view/DataServlet.java) | 根据 `dataType` 返回门户所需 JSON |
| 后端实现 | [view/](src/main/java/com/citywy/view/) · [service/](src/main/java/com/citywy/service/) | Servlet 分发和业务查询 |
| 数据结构 | [newsdemo.sql](sql/newsdemo.sql) | 表结构与示例数据；脚本由 MySQL 8.0 导出 |

前台使用 Vue 2.7、axios 和 jQuery。后端按 view / service / util / filter / domain 分层，CRUD 主要使用 c3p0 连接池；登录仍使用另一套 JDBC 连接工具。

## 主要接口

以下路径相对于应用上下文；映射分别定义在 [web.xml](src/main/webapp/WEB-INF/web.xml) 和 Servlet 注解中，`/api/data` 由 [DataServlet.java](src/main/java/com/citywy/view/DataServlet.java) 的 `@WebServlet` 注册。

| 路径 | 当前作用 |
| --- | --- |
| `/api/data?dataType=topmenu` | 前台顶部导航 |
| `/api/data?dataType=news`、`dataType=notice` | 前台新闻、通知数据 |
| `/api/data?dataType=leftmenu&leftmenuContent=栏目内容` | 按栏目内容查询左侧菜单；实际请求需对参数值做 URL 编码 |
| `/servlets/LoginServlet.htm` | 后台登录 |
| `/servlets/news/*`、`/servlets/user/*`、`/servlets/notice/*` | 后台新闻、用户和通知管理 |
| `/servlets/topmenu/*`、`/servlets/leftmenu/*` | 后台菜单管理 |
| `/admin/ajax/*` | 编辑器上传等后台辅助接口；由 `CAjaxServlet` 处理 |

`CAjaxServlet` 不是前台新闻数据入口，其中菜单分支目前返回空数组。

## 运行

项目目标环境为 JDK 8、Maven、Tomcat 8/9 与 MySQL。以下是按当前配置复现的准备步骤，完整前后端流程仍需在目标环境验证。

1. 创建并选中独立的示例数据库，再导入 [sql/newsdemo.sql](sql/newsdemo.sql)。脚本不会自动建库，且包含 `DROP TABLE`，不要对已有业务库执行。
2. 复制 [jdbc-mysql.properties.example](src/main/resources/jdbc-mysql.properties.example) 为同目录下的 `jdbc-mysql.properties`，填入示例库连接信息。真实配置不应提交到仓库。
3. 在仓库根目录执行 `mvn package`。默认产物为 `target/com.citywy.usermanage-1.0-SNAPSHOT.war`。
4. 当前页面将 API 固定为 `http://localhost:8100/api/data`，登录跳转也依赖根上下文。因此复现现有代码时，需在独立 Tomcat 实例中使用 **8100 端口、ROOT 上下文**，或先统一修改前后端的地址与跳转规则。

以 8100 / ROOT 部署为例：

| 访问地址 | 预期用途 |
| --- | --- |
| `http://localhost:8100/index.html` | 门户首页 |
| `http://localhost:8100/servlets/LoginServlet.htm` | 后台登录 |
| `http://localhost:8100/api/data?dataType=news` | 查看新闻 JSON |

根路径 `/` 的欢迎页指向后台入口；访问门户请明确使用 `/index.html`。单独打开 HTML 只能查看静态页面，内容区仍依赖后端和数据库。

## 验证与当前边界

最小运行验证应包括：前台数据接口返回有效 JSON、门户显示示例库记录、后台登录后完成一次新闻的新增与查询。只看到页面框架不能证明这个流程已打通。

- 仓库尚无自动化测试或 CI，也没有新环境下的完整部署验证记录。
- API 地址、登录跳转、上传目录和图片 URL 仍含固定环境配置；上传成功不一定意味着浏览器能访问返回地址。
- 登录过滤器只覆盖部分后台路径，上传和管理入口的访问控制尚需统一。
- 左侧菜单查询的参数化、登录数据源的并发访问与密码存储仍需完善；当前适用于本地示例环境。

后续重点是统一上下文路径与数据库访问、补齐权限和上传规则，再建立前后端集成测试。
