### 记一次MySQL容器无法启动的生产事故

生产环境一台服务器`MySQL`容器突然无法启动，通过`docker logs`命令查看容器日志，发现是因为表损坏问题。现在需要先回复服务再定位问题。所以需要先把MySQL强制运行起来。

由于需要强制启动`MySQL`，所以需要在`MySQL`的配置文件`my.cnf`里找到`[mysqld]`字段下，添加`innodb_force_recovery=1`配置，但是因为现在容器挂掉了，所以需要在宿主机上找到MySQL容器的配置文件。

#### [找MySQL的my.cnf](./00 在宿主机中找容器的配置文件.md)

-   在宿主机上执行：

    ```shell
    find / -name my.cnf | grep '/etc/mysql/my.cnf'
    ```

    ![image-20220108174521908](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2022/01/2022010817452121.png)

-   进入mysql配置文件目录

    ```shell
    cd /var/lib/docker/overlay2/11b4b25b35523ec146e4ae718120d64b1fb65bbf7f79dacd3dee26d034fbe23b/diff/etc/mysql/
    ```

    ![image-20220108174618725](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2022/01/2022010817461818.png)



#### 更改配置

找到配置文件后，更改`my.cnf`配置

##### innodb_force_recovery配置

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



#### 重启MySQL容器

更改`my.cnf`配置后，可以重启容器，容器重启后，根据实际情况进行数据恢复。

__注：__ _数据恢复后，要将`innodb_force_recovery`_设置回`0`，然后再重启容器。

