### 锁等待超时异常

锁等待超时时，会抛出 `Lock wait timeout exceeded; try restarting transaction` 异常，该异常表明某个事务在修改数据库数据时，由于长时间获取不到锁导致超时。当出现锁等待超时异常时，可以检查是那些业务或线程造成的长时间持有锁不释放导致其他事务无法获取锁。后提交的事务等待前面处理的事务释放锁，但是在等待的时候超过了mysql的锁等待时间，也会引发这个异常。



#### 当前出现的锁：`INNODB_LOCKS`

```sql
SELECT * FROM INFORMATION_SCHEMA.INNODB_LOCKS;
```

| lock_id                    | lock_trx_id     | lock_mode | lock_type | lock_table         | lock_index | lock_space | lock_page | lock_rec | lock_data |
| -------------------------- | --------------- | --------- | --------- | ------------------ | ---------- | ---------- | --------- | -------- | --------- |
| 52835755:2342:16:10        | 52835755        | X         | RECORD    | fisherdba.t_device | PRIMARY    | 2342       | 16        | 10       | 9681      |
| 422075193815024:2342:16:10 | 422075193815024 | S         | RECORD    | fisherdba.t_device | PRIMARY    | 2342       | 16        | 10       | 9681      |
| 52835754:2342:11:8         | 52835754        | X         | RECORD    | fisherdba.t_device | PRIMARY    | 2342       | 11        | 8        | 7309      |

| 字段          | 说明                                                         |
| ------------- | ------------------------------------------------------------ |
| `lock_id`     | 锁的`ID`                                                     |
| `lock_trx_id` | 拥有锁的事务 `ID`。可以结合 `INNODB_TRX` 表得到事务的详细信息。 |
| `lock_mode`   | [MySQL锁](./04 MySQL锁.md)<br />锁模式：【`S`：共享锁；`X`：排它锁；`IS`：意向共享锁；`IX`：意向排他锁】<br />表级锁：【`S_GAP`：共享间隙锁；`X_GAP`：排它间隙锁；`IS_GAP`：意向共享间隙锁；`IX_GAP`：意向排它间隙锁、`AUTO_INC`：自动递增锁】 |
| `lock_type`   | [MySQL锁](./04 MySQL锁.md)<br />锁类型:【`RECORD`：行锁；`GAP`：间隙锁；`NEXT-KEY`：页锁；`TABLE`：表锁】 |
| `lock_table`  | 被锁定的或者包含锁定记录的表的名称。                         |
| `lock_index`  | [MySQL索引](./05 MySQL索引.md)<br />当 `LOCK_TYPE='RECORD' `时，表示索引的名称；否则为 `NULL` |
| `lock_space`  | 当 `LOCK_TYPE='RECORD' `时，表示锁定行的表空间 `ID`；否则为 `NULL` |
| `lock_page`   | 当 `LOCK_TYPE='RECORD'`时，表示锁定行的页号；否则为 `NULL`   |
| `lock_rec`    | 当 `LOCK_TYPE='RECORD'`时，表示一堆页面中锁定行的数量，亦即被锁定的记录号；否则为 `NULL` |
| `lock_data`   | 当 `LOCK_TYPE='RECORD'`时，表示锁定行的主键；否则为 `NULL`   |



#### 锁等待的对应关系：`INNODB_LOCK_WAITS`

```sql
SELECT * FROM information_schema.INNODB_LOCK_WAITS;
```

| requesting_trx_id | requested_lock_id    | blocking_trx_id | blocking_lock_id            |
| ----------------- | -------------------- | --------------- | --------------------------- |
| 52835846          | 52835846:249:38359:1 | 422075193815024 | 422075193815024:249:38359:1 |
| 52835823          | 52835823:249:38359:1 | 422075193815024 | 422075193815024:249:38359:1 |
| 52835816          | 52835816:2342:12:11  | 422075193815024 | 422075193815024:2342:12:11  |

| 字段                | 说明                                                         |
| ------------------- | ------------------------------------------------------------ |
| `requesting_trx_id` | 请求事务的 `ID`                                              |
| `requested_lock_id` | 事务所等待的锁定的 `ID`。可以结合 `INNODB_LOCKS` 表得到事务的信息 |
| `blocking_trx_id`   | 阻塞事务的 `ID`                                              |
| `blocking_lock_id`  | 某一事务的锁的 `ID`，该事务阻塞了另一事务的运行。可以结合 `INNODB_LOCKS` 表得到事务的信息 |



#### 当前运行的所有事务：`INNODB_TRX`

```sql
SELECT * FROM information_schema.INNODB_TRX;
```

| trx_id          | trx_state | trx_started         | trx_requested_lock_id | trx_wait_started | trx_weight | trx_mysql_thread_id | trx_query   | trx_operation_state | trx_tables_in_use | trx_tables_locked | trx_lock_structs | trx_lock_memory_bytes | trx_rows_locked | trx_rows_modified | trx_concurrency_tickets | trx_isolation_level | trx_unique_checks | trx_foreign_key_checks | trx_last_foreign_key_error | trx_adaptive_hash_latched | trx_adaptive_hash_timeout | trx_is_read_only | trx_autocommit_non_locking |
| --------------- | --------- | ------------------- | --------------------- | ---------------- | ---------- | ------------------- | ----------- | ------------------- | ----------------- | ----------------- | ---------------- | --------------------- | --------------- | ----------------- | ----------------------- | ------------------- | ----------------- | ---------------------- | -------------------------- | ------------------------- | ------------------------- | ---------------- | -------------------------- |
| 422075193815024 | RUNNING   | 2021-03-03 14:20:00 | NULL                  | NULL             | 12908      | 759589              | SQL语句详情 | fetching rows       | 4                 | 3                 | 12908            | 1368272               | 1835529         | 0                 | 0                       | REPEATABLE READ     | 1                 | 1                      | NULL                       | 0                         | 0                         | 0                | 0                          |

| 字段                         | 说明                                                         |
| ---------------------------- | ------------------------------------------------------------ |
| `trx_id`                     | 事务`ID`                                                     |
| `trx_state`                  | 事务状态：【`RUNNING`、`LOCK WAIT`、`ROLLING BACK` 和 `COMMITTING`】 |
| `trx_started`                | 事务开始时间                                                 |
| `trx_requested_lock_id`      | 事务当前正在等待锁的标识，可以结合 `INNODB_LOCKS` 表得到更多详细信息 |
| `trx_wait_started`           | 事务开始等待的时间                                           |
| `trx_weight`                 | 事务的权重                                                   |
| `trx_mysql_thread_id`        | 事务线程 `ID`，可以和 `PROCESSLIST` 表`JOIN`                 |
| `trx_query`                  | 事务正在执行的 `SQL` 语句                                    |
| `trx_operation_state`        | 事务当前操作状态                                             |
| `trx_tables_in_use`          | 当前事务执行的 `SQL` 中使用的表的个数                        |
| `trx_tables_locked`          | 当前执行 `SQL` 的行锁数量                                    |
| `trx_lock_structs`           | 事务保留的锁数量                                             |
| `trx_lock_memory_bytes`      | 事务锁住的内存大小，单位为 `BYTES`                           |
| `trx_rows_locked`            | 事务锁住的记录数。包含标记为 `DELETED`，并且已经保存到磁盘但对事务不可见的行 |
| `trx_rows_modified`          | 事务更改的行数                                               |
| `trx_concurrency_tickets`    | 事务并发票数                                                 |
| `trx_isolation_level`        | 当前事务的隔离级别                                           |
| `trx_unique_checks`          | 是否打开唯一性检查的标识                                     |
| `trx_foreign_key_checks`     | 是否打开外键检查的标识                                       |
| `trx_last_foreign_key_error` | 最后一次的外键错误信息                                       |
| `trx_adaptive_hash_latched`  | 自适应散列索引是否被当前事务锁住的标识                       |
| `trx_adaptive_hash_timeout`  | 是否立刻放弃为自适应散列索引搜索 `LATCH` 的标识              |
| `trx_is_read_only`           |                                                              |
| `trx_autocommit_non_locking` |                                                              |



#### MySql线程的用户，地址以及操作的DB：`PROCESSLIST`

```sql
SELECT * FROM information_schema.PROCESSLIST;
-- 或者 --
SHOW PROCESSLIST
```

| ID    | USER          | HOST                 | DB               | COMMAND | TIME  | STATE     | INFO |
| ----- | ------------- | -------------------- | ---------------- | ------- | ----- | --------- | ---- |
| 13884 | fisher_debug2 | 172.19.49.115:44730  | fisherdba_debug2 | Sleep   | 15867 |           |      |
| 12486 | fisher_debug2 | 172.19.49.116:45280  | fisherdba_debug2 | Sleep   | 78    |           |      |
| 12917 | fisher_debug2 | 172.19.49.116:56766  | fisherdba_debug2 | Sleep   | 152   |           |      |
| 13968 | fisher_debug2 | 220.248.51.102:17154 |                  | Query   | 0     | executing |      |

| 字段      | 说明                                                         |
| --------- | ------------------------------------------------------------ |
| `ID`      | 标识`ID`。这与在`SHOW PROCESSLIST`语句的`Id`列、`Performance Schema threads`表的`PROCESSLIST_ID`列中显示的值类型相同，并由`CONNECTION_ID()`函数返回 |
| `USER`    | 发出该语句的`mysql`用户                                      |
| `HOST`    | 发出该语句的客户机的主机名(系统用户除外，没有主机)           |
| `DB`      | 默认数据库                                                   |
| `COMMAND` | 线程正在执行的命令的类型                                     |
| `TIME`    | 线程处于当前状态的时间(以秒为单位)                           |
| `STATE`   | 指示线程正在执行的操作、事件或状态                           |
| `INFO`    | 线程正在执行的语句，如果没有执行任何语句，则为`NULL`         |



#### 当前锁的状态数据

```sql
SHOW STATUS LIKE '%Innodb_row_lock%';
```

| Vaiable_name                    | Value  | 说明                                     |
| ------------------------------- | ------ | ---------------------------------------- |
| `Innodb_row_lock_current_waits` | 0      | 当前正在等待锁定的数量                   |
| `Innodb_row_lock_time`          | 599686 | 从系统启动到现在锁定总时间长度           |
| `Innodb_row_lock_time_avg`      | 637    | 每次等待所花平均时间                     |
| `Innodb_row_lock_time_max`      | 51602  | 从系统启动到现在等待最长的一次所花的时间 |
| `Innodb_row_lock_waits`         | 941    | 系统启动后到现在总共等待的次数           |



#### 锁等待超时时间相关配置

```sql
SHOW VARIABLES LIKE '%lock_wait_timeout';
```

| Vaiable_name               | Value    | 说明                              |
| -------------------------- | -------- | --------------------------------- |
| `innodb_lock_wait_timeout` | 50       | innodb的dml操作的行级锁的等待时间 |
| `lock_wait_timeout`        | 31536000 | 数据结构ddl操作的锁的等待时间     |



#### 定位锁超时的步骤：

##### 1、查看事务，找到状态为 `RUNNING` 的事务

```sql
SELECT * FROM information_schema.INNODB_TRX;
```

##### 2、找到执行事务的客户端请求的`SQL`线程

```sql
SELECT * FROM information_schema.PROCESSLIST WHERE ID = '{trx_id}';
```

##### 3、查看到端口和`host`以后，到对应的服务器查看相关的应用和日志

```shell
netstat -nlatp | grep {PORT}
ps -eaf | grep {PID}

##### 示例：
[releasefisher@bxt]$ netstat -nlatp | grep 5199
tcp        0      0 0.0.0.0:5199            0.0.0.0:*               LISTEN      7159/java
[releasefisher@bxt]$ ps -eaf | grep 7159
release+  7159     1  0 Jan07 ?        00:28:23 java -Xms256m -Xmx256m -Duser.timezone=Asia/Shanghai -jar bxt-dataService.jar
release+ 22174 22140  0 21:50 pts/0    00:00:00 grep --color=auto 7159
```

##### 4、如果无法定位，此时我们需要从`performance_schema`表寻找特定线程的信息

```sql
SELECT * FROM performance_schema.events_statements_current
```

##### 5、根据我们拿到的线程`id`去查，可以获取到具体的执行`sql`

```sql
SELECT * FROM performance_schema.events_statements_current
WHERE THREAD_ID IN (SELECT THREAD_ID FROM performance_schema.threads WHERE PROCESSLIST_ID=15844)
```



### 慢查询日志解读

>   MySQL的慢查询，全名是慢查询日志，是MySQL提供的一种日志记录，用来记录在MySQL中响应时间超过阀值的语句。具体环境中，运行时间超过`long_query_time`值的SQL语句，则会被记录到慢查询日志中。`long_query_time`的默认值为`10`，意思是记录运行`10`秒以上的语句。默认情况下，MySQL数据库并不启动慢查询日志，需要手动来设置这个参数。当然，如果不是调优需要的话，一般不建议启动该参数，因为开启慢查询日志会或多或少带来一定的性能影响。慢查询日志支持将日志记录写入文件和数据库表。



#### 慢查询参数

| 参数                          | 说明                                                         |
| ----------------------------- | ------------------------------------------------------------ |
| slow_query_log                | 是否开启慢查询日志，【1：开启；0：关闭】                     |
| log-slow-queries              | 旧版（5.6以下版本）MySQL数据库慢查询日志存储路径。可以不设置该参数，系统则会默认给一个缺省的文件`host_name-slow.log` |
| slow-query-log-file           | 新版（5.6及以上版本）MySQL数据库慢查询日志存储路径。可以不设置该参数，系统则会默认给一个缺省的文件`host_name-slow.log` |
| long_query_time               | 慢查询**阈值**，当查询时间多于设定的阈值时，记录日志         |
| log_queries_not_using_indexes | 未使用索引的查询也被记录到慢查询日志中（可选项）             |
| log_output                    | 日志存储方式。`log_output='FILE'`表示将日志存入文件，默认值是`'FILE'`。`log_output='TABLE'`表示将日志存入数据库 |



#### 慢查询配置

##### 慢查询开关

>   默认情况下`slow_query_log`的值为`OFF`，表示慢查询日志是禁用的，可以通过设置`slow_query_log`的值来开启

-   查看慢查询开关 & 慢查询日志文件路径

```sql
mysql> show variables like '%slow_query_log%';
 +---------------------+-----------------------------------------------+
 | Variable_name       | Value                                         |
 +---------------------+-----------------------------------------------+
 | slow_query_log      | OFF                                           |
 | slow_query_log_file | /home/WDPM/MysqlData/mysql/DB-Server-slow.log |
 +---------------------+-----------------------------------------------+
 2 rows in set (0.00 sec)
```

-   开启/关闭慢查询日志

```sql
-- 临时开启关闭慢查询日志【0：关闭；1：开启】
mysql> set global slow_query_log=1;
Query OK, 0 rows affected (0.09 sec)
```

**注：**使用`set global slow_query_log=1`开启了慢查询日志只对当前数据库生效，MySQL重启后则会失效。如果需要永久生效，就必须修改配置文件`my.cnf`，`my.cnf`要增加或修改参数`slow_query_log` 和`slow_query_log_file`，如下所示：

```cfg
slow_query_log = 1
slow_query_log_file = /tmp/mysql_slow.log
```

##### 慢查询阈值

>   开启了慢查询日志后，运行时间超过`long_query_time`值的SQL语句会被记录下来，默认情况下`long_query_time`的值为10秒，可以使用命令修改，也可以在my.cnf参数里面修改。关于运行时间正好等于long_query_time的情况，并不会被记录下来。即在mysql源码里是判断大于long_query_time，而非大于等于

-   查看慢查询阈值

```sql
-- 查看方式一：
mysql> show variables like 'long_query_time%';
 +-----------------+-----------+
 | Variable_name   | Value     |
 +-----------------+-----------+
 | long_query_time | 10.000000 |
 +-----------------+-----------+
 1 row in set (0.00 sec)
 
-- 查看方式二：
mysql> show global variables like 'long_query_time'
 +-----------------+-----------+
 | Variable_name   | Value     |
 +-----------------+-----------+
 | long_query_time | 10.000000 |
 +-----------------+-----------+
 1 row in set (0.00 sec)
```

-   设置慢查询阈值

```sql
mysql> set global long_query_time=4;
Query OK, 0 rows affected (0.00 sec)
```

**注：**使用命令 `set global long_query_time=4`修改后，需要重新连接或新开一个会话才能看到修改值，用`show variables like 'long_query_time'`查看是当前会话的变量值。也可以不用重新连接会话，而是用`show global variables like 'long_query_time';`

##### 慢查询日志存储

>   `log_output`参数指定日志的存储方式：
>
>   -   `log_output='FILE'`：表示将日志存入文件，默认值也是`'FILE'`。
>   -   `log_output='TABLE'`：表示将日志存入数据库，这样日志信息就会被写入到`mysql.slow_log`表中。
>
>   同时也支持两种日志存储方式，配置的时候以逗号隔开即可，如：`log_output='FILE,TABLE'`
>
>   日志记录到系统的专用日志表中，要比记录到文件耗费更多的系统资源。因此对于需要启用慢查询日志，又需要能够获得更高的系统性能，那么**建议优先记录到文件**

-   查看慢查询日志存储方式

```sql
mysql> show variables like '%log_output%';
 +---------------+-------+
 | Variable_name | Value |
 +---------------+-------+
 | log_output    | FILE  |
 +---------------+-------+
 1 row in set (0.00 sec)
```

-   设置慢查询日志存储方式

```
mysql> set global log_output='TABLE';
Query OK, 0 rows affected (0.00 sec)
```

-   测试慢查询日志

```sql
mysql> select sleep(11);
 
mysql> select * from mysql.slow_log;
```

##### 未使用索引的查询

>   该系统变量指定**未使用索引的查询**也被记录到慢查询日志中（可选项），开启了这个参数，其实使用`full index scan`的SQL也会被记录到慢查询日志。

-   查看是否开启未使用索引查询输出

```
mysql> show variables like 'log_queries_not_using_indexes';
 +-------------------------------+-------+
 | Variable_name                 | Value |
 +-------------------------------+-------+
 | log_queries_not_using_indexes | OFF   |
 +-------------------------------+-------+
 1 row in set (0.00 sec)
```

-   开启/关闭未使用索引查询输出

```sql
mysql> set global log_queries_not_using_indexes=1;
Query OK, 0 rows affected (0.00 sec)
```

##### 慢管理语句

>   这个系统变量表示，是否将慢管理语句例如`ANALYZE TABLE`和`ALTER TABLE`等记入慢查询日志。

-   查看慢管理语句输出是否开启

```sql
mysql> show variables like 'log_slow_admin_statements';
 +---------------------------+-------+
 | Variable_name             | Value |
 +---------------------------+-------+
 | log_slow_admin_statements | OFF   |
 +---------------------------+-------+
 1 row in set (0.00 sec)
```

-   开启/关闭慢管理语句输出

```sql
set global log_slow_admin_statements=1;
```

##### 慢查询记录条数

>   如果你想查询有多少条慢查询记录，可以使用`Slow_queries`系统变量。

```
mysql> show global status like '%Slow_queries%';
 +---------------+-------+
 | Variable_name | Value |
 +---------------+-------+
 | Slow_queries  | 11    |
 +---------------+-------+
 1 row in set (0.00 sec)
```



#### 慢查询日志文件解析

##### 查看慢查询日志文件

```tex
cat /FILE_PATH/xx-slow.log

# Time: 2021-03-04T06:49:05.745579Z
# User@Host: root[root] @  [192.168.56.1]  Id:     6
# Query_time: 11.000286  Lock_time: 0.000000 Rows_sent: 1  Rows_examined: 0
SET timestamp=1614840545;
select sleep(11);
# Time: 2021-03-04T06:49:16.746748Z
# User@Host: root[root] @  [192.168.56.1]  Id:     6
# Query_time: 11.000309  Lock_time: 0.000000 Rows_sent: 1  Rows_examined: 0
SET timestamp=1614840556;
select sleep(11);
```


##### 慢查询日志文件字段意义解析


| 选项                | 说明                                                         |
| ------------------- | ------------------------------------------------------------ |
| `Time`              | 执行SQL的时间                                                |
| `User`              | 执行SQL的连接信息、用户、IP                                  |
| `Id`                |                                                              |
| `Query_time`        | SQL执行的时长，时间越长表示SQL执行越慢                       |
| `Lock_time`         | 在MySQL服务器阶段等待锁时间（不是在存储引擎阶段）            |
| `Rows_sent`         | 查询返回的参数                                               |
| `Rows_examined`     | SQL扫描的行数，时间越长就越浪费时间                          |
| `SET timestamp`     | 设置时间戳，没有实际意义，和`Time`相等                       |
| `select sleep(11);` | 最后几行（第五行与下一个`# Time`之前），执行的SQL语句，可能会很长 |




#### 日志分析工具

>   在生产环境中，如果要手工分析日志，查找、分析SQL，显然是个体力活。MySQL提供了日志分析工具`mysqldumpslow`

##### mysqldumpslow帮助信息

```
root@e97e4fe4eef0:/# mysqldumpslow --help
```

```tex
Usage: mysqldumpslow [ OPTS... ] [ LOGS... ]

Parse and summarize the MySQL slow query log. Options are

  --verbose    verbose
  --debug      debug
  --help       write this text to standard output

  -v           verbose
  -d           debug
  -s ORDER     what to sort by (al, at, ar, c, l, r, t), 'at' is default
                al: average lock time
                ar: average rows sent
                at: average query time
                 c: count
                 l: lock time
                 r: rows sent
                 t: query time
  -r           reverse the sort order (largest last instead of first)
  -t NUM       just show the top n queries
  -a           don't abstract all numbers to N and strings to 'S'
  -n NUM       abstract numbers with at least n digits within names
  -g PATTERN   grep: only consider stmts that include this string
  -h HOSTNAME  hostname of db server for *-slow.log filename (can be wildcard),
               default is '*', i.e. match all
  -i NAME      name of server instance (if using mysql.server startup script)
  -l           don't subtract lock time from total time
```

| 规则 | 说明                                                         |
| ---- | ------------------------------------------------------------ |
| -s   | 排序规则：【c：记录次数；l：锁定时间；r：返回记录；t：执行时间；al：锁定时间倒序；ar：返回记录倒序；at：执行时间倒序】 |
| -t   | 是`top n`的意思，即为返回前面多少条的数据                    |
| -g   | 是`grep`的意思，后边可以写一个正则匹配模式，大小写不敏感的   |

##### 示例

```shell
root@e97e4fe4eef0:/# mysqldumpslow -t 1 -s c slow.log
```

```tex
Reading mysql slow query log from e97e4fe4eef0-slow.log
Count: 3  Time=11.00s (33s)  Lock=0.00s (0s)  Rows=1.0 (3), root[root]@[192.168.56.1]
  select sleep(N)
```

| 字段                        | 说明                               |
| --------------------------- | ---------------------------------- |
| `Count`                     | 该SQL执行的次数                    |
| `Time`                      | SQL平均执行时长（SQL总的执行时长） |
| `Lock`                      | SQL平均锁定时长（SQL总的锁定时长） |
| `Rows`                      | SQL平均返回行数（SQL总的返回行数） |
| `root[root]@[192.168.56.1]` | 执行SQL的用户、IP                  |
| `select sleep(N)`           | 执行的SQL语句                      |