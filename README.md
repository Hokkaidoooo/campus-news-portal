# 校园新闻门户与后台管理系统

Vue 2 门户展示新闻、通知与栏目；Servlet/JSP 后台维护内容和账号。应用通过一个 HikariCP 数据源访问 MySQL，登录使用 bcrypt 密码哈希，图片由独立存储模块校验并发布。

同一 WAR 已在独立 Tomcat 中以 ROOT 和 `/news`、不同端口运行，浏览器显示真实测试库内容并完成登录。实现与验证范围见 [验收记录](docs/optimization-validation.md)。

## 运行

需要 JDK 8、Maven、Tomcat 9 和独立 MySQL 5.7/8.0 数据库。

1. 在自己创建的空库执行 [sql/schema.sql](sql/schema.sql)。它只建表，没有预置账号和删除语句。
2. 配置 `NEWS_JDBC_URL`、`NEWS_DB_USER`、`NEWS_DB_PASSWORD`，同名 JVM 属性优先于环境变量。缺少配置时明确失败，不读取旧 properties 文件。
3. 显式创建管理员，密码不会以明文写入数据库：

```powershell
$env:NEWS_JDBC_URL = 'jdbc:mysql://127.0.0.1:3306/news_demo?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC'
$env:NEWS_DB_USER = 'news_user'
$env:NEWS_DB_PASSWORD = '示例库密码'
$env:NEWS_ADMIN_NAME = 'admin'
$env:NEWS_ADMIN_PASSWORD = '自行设置的密码'
mvn -B compile org.codehaus.mojo:exec-maven-plugin:3.5.0:java '-Dexec.mainClass=com.citywy.util.AccountAdmin' '-Dexec.args=--create-admin'
```

4. 为 Tomcat 设置 `NEWS_UPLOAD_DIR`，指向应用部署目录以外、运行账号可写的独立目录。
5. 执行 `mvn -B package`，将 `target/campus-news.war` 部署为 `ROOT.war` 或 `news.war`。前台 API、后台跳转和图片 URL 随上下文确定，不固定端口。

门户为 `/index.html`，登录为 `/login.jsp`。例如 `/news/index.html`、`/news/login.jsp`；应用根路径默认进入门户。

已有明文账号需要先备份，再显式执行离线迁移，见 [数据库与密码说明](sql/README.md)。原 [newsdemo.sql](sql/newsdemo.sql) 是历史原件，不能直接对已有业务库执行。

## 接口与模块

以下路径相对于部署上下文。

| 接口 | 行为 |
| --- | --- |
| `/api/data?dataType=topmenu/news/notice` | 门户只读 JSON；每次选一种 dataType |
| `/api/data?dataType=leftmenu&leftmenuContent=值` | 参数化等值查询，含引号的栏目按普通文本处理 |
| `/servlets/LoginServlet.htm` | GET 登录页，POST 登录；`type=logout` 使会话失效 |
| `/servlets/news/*` 等五组管理接口 | 登录后访问新闻、通知、顶部/侧边栏目及用户管理 |
| `/admin/ajax/pic.htm` | POST multipart PNG，返回上下文内可读取的 URL |
| `/admin/ajax/picedi.htm` | 同一上传实现；无回调参数时返回 JSON；显式 `CKEditorFuncNum` 保留旧编辑器回调协议 |
| `/uploads/{id}.png` | 按服务器生成的标识读取已发布 PNG |

`AllFilter` 统一保护管理 JSP、Servlet 和辅助上传接口。用户查询不携带密码哈希；登录重新生成会话 ID，退出后原会话不能访问后台。应用仅提供同源访问，不再使用通配来源与凭据同时开启的 CORS 配置。

上传只接受可解码的 PNG，最多 1 MiB、4194304 像素。文件名必须是无路径的 PNG 名称；保存使用随机标识，同名上传生成不同文件。完整写入临时文件后以同卷硬链接发布，存在目标时拒绝覆盖；不支持硬链接的存储系统会明确失败。文件位于 WAR 外部，通过统一读取接口返回 `image/png`，不作为可执行页面。

## 验证

```sh
mvn -B test
mvn -B package
npm ci
npx playwright install --with-deps chromium
```

完整集成测试另需设置专用测试服务的 `NEWS_TEST_MYSQL_URL`（例如 `jdbc:mysql://127.0.0.1:33317/`，不含库名）、`NEWS_TEST_MYSQL_USER`、`NEWS_TEST_MYSQL_PASSWORD`，再运行：

```sh
mvn -B clean -Pintegration verify
```

测试自行创建随机 `news_test_` 库，部署实际 WAR，在两个上下文和端口上发送真实请求、运行浏览器并检查关服日志。它不复用测试类路径中的应用依赖补齐 WAR。Windows 可设置 `PLAYWRIGHT_CHANNEL=msedge` 使用已安装的 Edge。

数据库测试覆盖并发登录、连接归还、参数化筛选、异常传播和密码迁移；浏览器测试覆盖真实数据展示、登录和移动登录布局。测试报告、截图与实测限制见 [验收记录](docs/optimization-validation.md)。
