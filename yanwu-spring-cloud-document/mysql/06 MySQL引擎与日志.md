#### 引擎

##### MyISAM

`InnoDB`引擎提供了数据库`ACID`事务的支持，并且提供了行锁和外键的约束

##### InnoDB：

不提供事务，支持表锁

-   特性：插入缓冲、二次写、自适应哈希、预读

##### memory：

所有的数据在内存中，处理速度快，但是安全性不高

##### InnoDB和MyISAM索引的区别

`InnoDB`和`MyISAM`都是使用 `B+`树，`InnoDB`是索引组织表，`MyISAM`是堆表

- 索引组织表：数据存储是把表按照索引的方式存储的，数据是有序的，数据的位置是预先定好的，与插入的顺序没有关系，__查询快，修改慢__
- 堆表：数据插入时时存储位置是随机的，主要是数据库内部块的空闲情况决定，获取数据是按照命中率计算，全表扫表时不见得先插入的数据先查到，__修改快，查询慢__

| 引擎     | 事务   | 锁     | MVCC   | 外键   | 全文索引 | 多种行格式 | 其它       |
| -------- | ------ | ------ | ------ | ------ | -------- | ---------- | ---------- |
| `InnoDB` | 支持   | 行级锁 | 支持   | 支持   | 不支持   | 支持       | 索引组织表 |
| `MyISAM` | 不支持 | 表级锁 | 不支持 | 不支持 | 支持     | 不支持     | 堆表       |

##### InnoDB的事务与日志

###### 日志类型

- `redo log`：在页修改的时候，先写到`redo log buffer`里面，然后写到`redo log`的文件系统缓存里面(`fwrite`)，然后再同步到磁盘文件（`fsync`）
- `undo log`：在操作任何数据之前，首先将数据备份到`redo log buffer`里面，然后进行数据的修改。如果出现了错误或者用户执行了 `ROLLBACK` 语句，系统可以利用 `Undo Log` 中的备份将数据恢复到事务开始之前的状态

###### 事务如何通过日志来实现

基本流程如下：因为事务在修改页时，要先记`undo`，在记`undo`之前要记`undo`的`redo`， 然后修改数据页，再记数据页修改的`redo`。`redo`（里面包括`undo`的修改） 一定要比数据页先持久化到磁盘。当事务需要回滚时，因为有`undo`，可以把数据页回滚到前镜像的状态，崩溃恢复时，如果`redo log`中事务没有对应的`commit`记录，那么需要用`undo`把该事务的修改回滚到事务开始之前。 如果有`commit`记录，就用`redo`前滚到该事务完成时并提交掉。



#### 日志

##### redo log（前滚日志）

`InnoDB`存储引擎的日志文件，当发生数据修改的时候，`InnoDB`引擎会先将记录写到`redo log`中，并更新内存，此时更新就算完成了，同时`InnoDB`引擎会在合适的时机将记录持久化到磁盘中（`WAL` 预写日志），通过`redo log`的保证 __事务的持久性__，`redo log`是固定大小的，它是一个循环写的过程，循环写通过`checkpoint`和`write pos`两个指针实现，有了`redo log`之后，`InnoDB`就可以保证即使数据库发生异常重启，之前的记录也不会丢失，叫做：`crash-safe`

###### WAL预写日志

![clipboard](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/20200628133129.png)

##### undo log（回滚日志）

`undo log`是为了实现 __事务的原子性__，在`MySQL`数据库中`InnoDB`引擎中通过`undo log`来实现多版本并发控制，在操作任何数据之前，首先将数据备份到`undo log`中，然后进行数据库的修改，当出现了错误或用户执行了`rollback`语句时，系统可以根据`undo log`中的备份将数据恢复到食物开始之前的状态

###### undo log是逻辑日志，可以理解为

- 当执行`delete`语句时，`undo log`中会记录一条对应的`insert`语句
- 当执行`insert`语句是，`undo log`中会记录一条对应的`delete`语句
- 当执行`update`语句时，`undo log`中会记录一条对应相反的`update`记录

##### binlog（归档日志）

`binlog`是`server`层的日志，主要做`MySQL`功能层面的事情，不管是什么存储引擎都可以使用`biglog`，它会记录所有语句的原始逻辑，并且采用追加写的方式。默认是关闭的，需要手动开启，并且可以指定记录方式（语句或行）。一般的数据备份系统可以通过备份`binlog`来实现数据备份。

###### binLog的三种录入形式

-   `statement`

    每条修改数据的`sql`都会记录带`binlog`中，不需要记录每一行的变化，减少了`binlog`的日志量，节约`IO`，由于`sql`的执行是有上下文的，因此在保存的时候需要保存相关的信息，同时还有一些使用了函数之类的语句无法被记录复制。

-   `row`

    不记录`sql`语句上下文相关信息，仅保存哪条记录被修改。记录单元为每一行的改动，基本是可以全部记下来但是由于很多操作，会导致大量行的改动(比如`alter table`)，因此这种模式的文件保存的信息太多，日志量太大

-   `mixed`

    一种折中的方案，普通操作使用`statement`记录，当无法使用`statement`的时候使用`row`

#### 恢复数据过程

1. 找到最近的一次全量备份数据
2. 从备份时间点开始，将`biglog`取出来，执行`SQL`语句恢复数据

