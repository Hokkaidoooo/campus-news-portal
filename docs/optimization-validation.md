# 优化验收记录

日期：2026-10-15。NP-01～NP-04 已在实际 WAR、独立 MySQL 测试库和 Tomcat 中完成验证。

| 项目 | 实现与验收 |
| --- | --- |
| NP-01 | API、登录跳转、后台链接、上传读取与轮播图片均随部署上下文确定；同一 WAR 在 ROOT、`/news` 和不同端口运行 |
| NP-02 | 唯一 HikariCP 数据源；参数化查询、并发读取、连接归还和数据库异常传播通过实际数据库测试 |
| NP-03 | 管理 JSP、五组管理 Servlet 和两个上传入口共用访问控制；bcrypt 密码验证、显式旧密码迁移、会话更新与退出失效通过 |
| NP-04 | PNG 保存与读取共用 `ImageStorage`；随机标识、无覆盖发布、大小与内容检查、失败无残留，以及两类编辑器响应通过 |

## 本机验证

环境：Windows、JDK 8u504、Maven 3.9.9、Tomcat 9.0.111 和 Edge。数据库使用本轮隔离服务；测试自行创建随机库，结束时清除自己的库。

```powershell
mvn -B -Pintegration verify
node --check tools\browser-smoke.mjs
node --check src\main\webapp\js\lunbo.js
git diff --check
```

Maven 最终返回 `BUILD SUCCESS`，失败、错误和跳过均为 0：

| 测试 | 数量 | 检查范围 |
| --- | ---: | --- |
| `ImageStorageTest` | 3 | 同名独立存储、路径与伪 PNG 拒绝、超限及中断不遗留半文件 |
| `PasswordHashesTest` | 2 | bcrypt 哈希和密码校验 |
| `DatabaseServicesIT` | 4 | 带引号及通配符输入、CRUD、并发登录与读取、哈希写入、显式迁移、异常及连接归还 |
| `PortalIT` | 1 | 实际部署两种上下文、真实 HTTP/数据库/浏览器操作，以及正常停止 |

`PortalIT` 通过实际启动 `AccountAdmin --create-admin` 创建测试管理员。CLI 与后台用户操作调用相同的哈希服务，没有单独的测试密码写入实现。

两次部署的浏览器记录：

| 上下文 | 端口 | 记录 | 门户截图 |
| --- | ---: | --- | --- |
| ROOT | 7196 | [root-browser.json](validation/root-browser.json) | [ROOT 门户](validation/root-portal.png) |
| `/news` | 7245 | [news-browser.json](validation/news-browser.json) | [具名上下文门户](validation/news-portal.png) |

每次浏览器均完成 3 次上下文内 API 请求，显示测试库的新闻、通知和栏目，检查静态中文和 4 张轮播图片，再实际登录后台。另验证 390×844 视口的登录页无横向溢出。六张截图逐项查看：[ROOT 后台](validation/root-admin.png)、[ROOT 移动登录](validation/root-login-mobile.png)、[`/news` 后台](validation/news-admin.png)、[`/news` 移动登录](validation/news-login-mobile.png)。截图中的新闻和账号来自明确构造的隔离测试数据。

## 访问控制与上传

未登录时，后台 JSP、五组管理接口的修改/删除 GET 与 POST，以及 `pic`、`picedi` 两个上传入口均返回 401。登录后实际打开各组新建页面、写入一条新闻并从公共 API 读回；退出后旧会话访问后台仍返回 401。

两个上传入口使用同一个存储实现。测试实际上传、读取并比较 PNG 字节，重复原名生成不同标识；CKEditor 数字回调仍返回原协议。非法回调参数在存储前拒绝；路径穿越返回 400，伪 PNG 返回 415，超过 1 MiB 返回 413，拒绝请求没有新增文件。发布使用同卷硬链接，存储卷不支持时明确失败，不切换为可覆盖写入。

## 字符编码回归

实际截图曾发现静态中文乱码：源文件及 WAR 都是 UTF-8，但 Tomcat 读取静态文件时采用系统默认编码，再按响应编码转码。新增检查先真实复现失败，随后在 `web.xml` 为唯一生效的静态资源映射显式设置 `fileEncoding=UTF-8`。测试 JVM 固定使用 `GB18030` 默认编码，逐字节验证四份 HTML 和轮播脚本的 HTTP 响应与原文件一致。

轮播脚本原来的 `../images/` 会在 `/news` 部署下请求应用外目录，现已改为页面相对路径；没有真实目的地的 `None` 点击处理同时移除。浏览器逐张解码四张图片，并检查 URL 没有越过应用上下文。

测试停止流程保留最初的失败，关服错误作为附加异常报告。最终两份服务器日志均包含连接池完成关闭的记录，没有卸载后类访问、线程泄漏或类加载异常；两个容器进程均以 0 退出。

## 构建物与复跑

本次 `target/campus-news.war` 为 14765598 字节，SHA-256：

```text
9f16cf583503efaf9c2ba4fa90eef67099572bc61455740c662b360ac138ea7f
```

WAR 包含 38 个应用类及 HikariCP 4.0.3；没有 c3p0 或 mchange 依赖。独立容器 JVM 仅携带 Tomcat 依赖，应用类和运行库均从这个 WAR 加载。上面的摘要标识本次本机构建，不要求带不同构建时间的 WAR 字节一致。

配置方式见 [README](../README.md)。Linux CI 使用 MySQL 8、Chromium 和 `mvn -B clean -Pintegration verify`，保留 JUnit 报告、服务日志和浏览器截图。真实测试回执用于证明这些行为；没有将启动进程本身当作通过，也没有验证公开域名或反向代理部署。

