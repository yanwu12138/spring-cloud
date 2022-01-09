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

__注：__当设置`innodb_force_recovery`大于`0`后，可以对表进行`select`、`create`、`drop`操作，但`insert`、`update`或者`delete`这类操作是不允许的。

