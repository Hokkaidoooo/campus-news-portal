# 数据库与密码服务验证

日期：2026-10-15。此记录覆盖数据库/服务层；门户上下文、页面鉴权和上传由应用集成验收另行记录。

本地环境为 Windows、JDK 8u504、Maven 3.9.9、MySQL 5.7.17。连接的是本轮新建数据目录和独立端口 `33317`，每次创建随机 `news_test_` 数据库。测试只在确认创建成功后清理自己的库；没有访问原业务库或 MRP 测试库。

| 验证入口 | 结果 | 关键行为 |
| --- | --- | --- |
| `PasswordHashesTest` | 2 个测试通过，退出 0 | 随机盐、正确/错误密码、72 个 UTF-8 字节边界、旧明文和损坏哈希显式失败 |
| `DatabaseServicesIT` | 4 个真实数据库测试通过，退出 0 | 24 组并发登录和菜单查询；引号与 LIKE 特殊字符作为普通值；新闻 CRUD、其余三类内容的写入/读取；用户公开字段及三条件 AND 筛选；显式密码迁移与重复执行；真实缺表故障传播；连接池归还后借出数为 0 |

真实测试发现新版 MySQL 驱动的 `getObject(DATETIME)` 返回 `LocalDateTime`，与域对象的 `java.util.Date` 契约不一致。已在唯一 JDBC 映射入口按 SQL 类型使用 `getTimestamp` / `getDate`；不在每个服务重复转换。

上述首轮测试使用 c3p0，应用级复验发现其 close 返回后仍有异步资源销毁线程，可能晚于 WebappClassLoader 卸载。当前实现已改为 Java 8 支持的 HikariCP 4.0.3，测试改为核对 HikariPoolMXBean 的 activeConnections 为 0，并使用其同步关闭生命周期。新池版本的整套测试与真实关服日志由应用集成复验统一记录，尚未据首轮结果宣称新版本通过。

复现命令（在仓库根目录，JDK/Maven 已在 PATH）：

```powershell
mvn -B test -Dtest=PasswordHashesTest
$env:NEWS_TEST_MYSQL_URL = 'jdbc:mysql://127.0.0.1:33317/'
$env:NEWS_TEST_MYSQL_USER = 'test_admin'
$env:NEWS_TEST_MYSQL_PASSWORD = '独立测试服务器密码'
mvn -B -Pintegration '-Dit.test=DatabaseServicesIT' test-compile failsafe:integration-test failsafe:verify
```

PowerShell 中带点的 `-Dit.test=...` 参数需要引号，避免被拆成生命周期名称。测试报告位于 `target/surefire-reports` 和 `target/failsafe-reports`。

配置只使用 `NEWS_JDBC_URL/NEWS_DB_USER/NEWS_DB_PASSWORD`；DBConnUtil 已删除，应用仅有一个 HikariCP 工厂，没有共享连接/结果集、SQL 参数打印或异常后返回成功的行为。服务支持注入 `DataSource`，实际 HTTP 集成可以复用同一服务实现。
