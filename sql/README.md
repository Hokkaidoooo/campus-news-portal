# 数据库初始化与密码迁移

应用只读取三个配置名：`NEWS_JDBC_URL`、`NEWS_DB_USER`、`NEWS_DB_PASSWORD`。同名 JVM 系统属性优先于环境变量；缺少配置直接报错，不读取旧 properties 或默认业务库地址。应用中登录、用户维护和内容 CRUD 共用一个 HikariCP 4.0.3 DataSource（支持 Java 8），服务另有接收 `DataSource` 的构造函数供独立测试使用。应用停止时先同步关闭连接池，再等待本应用 MySQL 清理线程退出并注销自己的 JDBC 驱动；超时或失败会报错。

```powershell
$env:NEWS_JDBC_URL = 'jdbc:mysql://127.0.0.1:3306/news_demo?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC'
$env:NEWS_DB_USER = 'news_user'
$env:NEWS_DB_PASSWORD = '示例库密码'
```

全新库使用 [schema.sql](schema.sql)，兼容 MySQL 5.7 和 8.0。该文件只建表，不删除表、不创建数据库、不预置账户。[newsdemo.sql](newsdemo.sql) 作为历史示例原件保留，包含原始数据和重建表语句，不对已有库直接执行。

已使用旧明文账户的实例先备份用户表，再显式执行离线迁移：

```powershell
mvn -B compile org.codehaus.mojo:exec-maven-plugin:3.5.0:java '-Dexec.mainClass=com.citywy.util.PasswordMigration' '-Dexec.args=--migrate-plaintext'
```

这条命令先把 `password` 扩为 `VARCHAR(255)`，再锁定用户行，在一个事务中把明文转换为 jBCrypt 0.4 的 bcrypt 哈希（cost 12）。字段扩容属于 MySQL 独立提交的 DDL；密码数据更新失败时整批回滚。已符合当前格式的哈希不重复转换；可再次执行并返回转换数量 0。程序不输出密码和哈希。

迁移默认不执行，唯一执行开关为离线命令参数 `--migrate-plaintext`。应用启动与登录都不会自动迁移。登录遇到旧明文、损坏或不支持的哈希会报出需要迁移/重置的错误；不会退回明文比对。支持的存储格式为 `$2a$`、cost 4～16；新写入固定使用 cost 12。密码限制为 1～72 个 UTF-8 字节，避免 bcrypt 截断导致不同密码被视为相同。

用户查询只选择 `id/name/uname/role/valid`，密码字段不进入 JSP、用户列表或会话对象。新增用户会哈希密码；编辑用户时密码留空表示保持原密码。停用用户（`valid` 不等于“有效”）无法登录。

所有 Service 的 `condition` 参数现在表示原始搜索词，不接受 SQL 片段；内容按名称做字面包含查询，用户按登录名或显示名查询，`%`、`_`、`!` 都按普通字符处理。用户管理页的 `countFiltered` / `listFiltered` 保留角色、登录名、显示名三个独立条件，以 AND 组合，字段只允许 `role/name/uname`。`getLeftmenuByContent` 使用原始栏目内容做等值查询。排序列采用声明过的列名，排序方向只允许 ASC/DESC；非法值明确报错。未找到记录时返回 `null`，数据库故障向调用方抛出异常。
