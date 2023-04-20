### 记一次`MySQL`容器无法启动的生产事故

生产环境一台服务器`MySQL`容器突然无法启动，通过`docker logs`命令查看容器日志，发现是`InnoDB page`页损坏问题。需要把对应表·的`InnoDB page`页修复然后重启`docker`容器恢复服务。

由于需要强制启动`MySQL`，所以需要在`MySQL`的配置文件`my.cnf`里找到`[mysqld]`字段下，添加`innodb_force_recovery`配置，但是因为现在容器挂掉了，所以需要在宿主机上找到`MySQL`容器的配置文件。

#### 错误描述

```tex
InnoDB: Database page corruption on disk or a failed file read of page [page id: space=0, page number=264]. You may have to recover from a backup.
```

![image-20230420170732192](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2023/04/image-20230420170732192.png)



### 恢复过程

#### 找`MySQL`的`my.cnf`

- 在宿主机上执行：

  ```shell
  [root@localhost ~]# find / -name my.cnf | grep '/etc/mysql/my.cnf'
  /var/lib/docker/overlay2/bbbc0161ead1d5074360fe315692e116655df0421c36f889416048764afa4f58/diff/etc/mysql/my.cnf
  /var/lib/docker/overlay2/0122885ad46aeda835d67a6ce59138b31790cbfe5d8a88366c39da0ee20e87db/merged/etc/mysql/my.cnf
  ```

  ![image-20230420173852138](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2023/04/image-20230420173852138.png)

- 进入`mysql`配置文件目录

  ```shell
  cd /var/lib/docker/overlay2/bbbc0161ead1d5074360fe315692e116655df0421c36f889416048764afa4f58/diff/etc/mysql/
  ```

  ![image-20230420173952374](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2023/04/image-20230420173952374.png)



#### 更改配置

找到配置文件后，更改`my.cnf`配置

##### `innodb_force_recovery`配置说明

参数`innodb_force_recovery`影响了整个`Innodb`存储引擎的恢复状况。该值默认为`0`，表示当需要恢复时执行所有的恢复操作。当不能进行有效恢复时，如数据页发生了`corruption`，`Mysql`数据库可能会宕机，并把错误写入错误日志中。

但在某些情况下，可能不需要执行完整的恢复操作。例如在进行`alter table`操作时，这时发生意外，数据库重启时会对`Innodb`表执行回滚操作。对于一个大表，这需要很长时间，甚至可能是几个小时。这时可以自行恢复，例如将表删除，从备份中重新将数据导入表中，这些操作可能要快于回滚操作。

| 等级 | 说明                                                         |
| ---- | ------------------------------------------------------------ |
| `0`  | 表示当需要恢复时执行所有的恢复操作                           |
| `1`  | `SRV_FORCE_IGNORE_CORRUPT`：忽略检查到的`corrupt`页          |
| `2`  | `SRV_FORCE_NO_BACKGROUND`：阻止主线程的运行，如主线程需要执行`full purge`操作，会导致`crash`。 |
| `3`  | `SRV_FORCE_NO_TRX_UNDO`：不执行事务回滚操作。                |
| `4`  | `SRV_FORCE_NO_IBUF_MERGE`：不执行插入缓冲的合并操作。        |
| `5`  | `SRV_FORCE_NO_UNDO_LOG_SCAN`：不查看重做日志，`InnoDB`存储引擎会将未提交的事务视为已提交。 |
| `6`  | `SRV_FORCE_NO_LOG_REDO`：不执行前滚的操作。                  |

__注：__ _当设置`innodb_force_recovery`大于`0`后，可以对表进行`select`、`create`、`drop`操作，但`insert`、`update`或者`delete`这类操作是不允许的。**大的参数包含小的参数，所以我们尝试恢复服务的时候，先尽量用最小参数尝试，小的不行再依次尝试用大的参数**_

##### 编辑文件

```tex
[mysqld]
disable-partition-engine-check
innodb_force_recovery=1
```



#### 重启`MySQL`容器，尝试恢复数据表

##### 重启`MySQL`容器

![image-20230420171417361](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2023/04/image-20230420171417361.png)

##### 使用`mysqlcheck`工具检查具体是那些表损坏

```shell
##### 进入MySQL容器
[root@localhost conf.d]# docker exec -ti 74883513e6af bash

##### 检查损坏的表
root@74883513e6af:/# mysqlcheck -A -u root -p
Enter password: XXXXXXXX
edge_center.m_user_business                        OK
edge_center.t_account                              OK
edge_center.t_up_message                           OK
mysql.columns_priv                                 OK
mysql.db
warning  : 2 clients are using or haven't closed the table properly
status   : OK
mysql.engine_cost                                  OK
mysql.event                                        OK
mysql.time_zone_transition_type                    OK
mysql.user
warning  : 2 clients are using or haven't closed the table properly
status   : OK
sys.sys_config                                     OK
```

检测得知`mysql.db && mysql.user`两张表都有损坏

![image-20230420171955014](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2023/04/image-20230420171955014.png)

##### 使用`repair`修复表

```sql
##### 进入MySQL
root@74883513e6af:/# mysql -uroot -pfisher#72a7,.

##### 修复损坏的表
mysql> repair table mysql.db;
+----------+--------+----------+----------+
| Table    | Op     | Msg_type | Msg_text |
+----------+--------+----------+----------+
| mysql.db | repair | status   | OK       |
+----------+--------+----------+----------+
1 row in set (0.00 sec)

mysql> repair table mysql.user;
+------------+--------+----------+----------+
| Table      | Op     | Msg_type | Msg_text |
+------------+--------+----------+----------+
| mysql.user | repair | status   | OK       |
+------------+--------+----------+----------+
1 row in set (0.00 sec)

mysql> ^DBye
```

![image-20230420172044199](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2023/04/image-20230420172044199.png)

__注：__ _损坏的表修复完之后重新使用 `mysqlcheck` 工具检测一遍，都没问题再还原`MySQL`配置_



#### 还原`MySQL`配置，并重启容器

##### 还原`innodb_force_recovery`配置

```tex
[mysqld]
# disable-partition-engine-check
# innodb_force_recovery=1
```

##### 重启`MySQL`容器，恢复服务

