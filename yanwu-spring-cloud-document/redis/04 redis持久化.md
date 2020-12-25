### [持久化](https://www.cnblogs.com/xiaoqiang-code/p/11748395.html)

#### RDB（redis database，全量模式）【默认】

>   RDB是redis内存到硬盘的快照，用于持久化，有save（同步）、bgsave（异步）和自动配置三种方式触发

##### RDB持久化有两种方式

- 当redis满足一定的条件时，会自动触发save指令同步快照。比如：60秒之内有1000个键被更改或定时1个小时执行，当条件满足时，会自动持久化
- 通过save、bgsave指令手动进行持久化。每次执行save、bgsave命令，都会生成一个新的快照文件覆盖旧的快照文件

| 命令   | 同步/异步 | 是否阻塞             | 复杂度 | 优点               | 缺点               |
| ------ | --------- | -------------------- | ------ | ------------------ | ------------------ |
| save   | 同步      | 是                   | O(N)   | 不会消耗额外的内存 | 阻塞客户端的命令   |
| bgsave | 异步      | 是（阻塞发生在fork） | O(N)   | 不阻塞客户端的命令 | 需要fork，消耗内存 |

#### AOF（append only file，增量模式）

>   AOF是以文件的形式进行持久化，类似于mysql的binlog，它会将每个客户端的每一次写操作都记录到AOF文件中，然后通过载入AOF文件进行数据恢复，AOF是以日志文件的形式持久化，针对客户端的每一条写命令，都会将对应的写命令写到appendonly.aof文件中。通过修改配置文件来打开AOF持久化功能：
>
>   ```bash
>   appendonly yes
>   ```

| 策略     | 说明                                                         | 优点          | 缺点           |
| -------- | ------------------------------------------------------------ | ------------- | -------------- |
| always   | redis将写命令刷新到缓冲区，然后把每条命令从缓冲区fsync到硬盘的AOF文件 | 不丢失数据    | IO开销大       |
| everysec | redis将写命令刷新到缓冲区，然后每秒从缓冲区fsync到硬盘的AOF文件 | 每秒一次fsync | 丢失一秒的数据 |
| no       | redis将写命令刷新到缓冲区，由OS决定时机执行fsync到硬盘的AOF文件 | 不用管        | 不可控         |

##### AOF的配置于触发方式

```bash
配置：
	auto-aof-rewrite-min-zise		>> AOF文件重写需要的尺寸
	auto-aof-rewrite-precentage		>> AOF文件增长率
统计：
	aof_current_size 				>> AOF当前尺寸（单位：字节）
	aof_base_size   				>> AOF上次启动和重写的储存（单位：字节）
```

当同时满足以下条件时，自动触发AOF策略，执行AOF流程

```bash
aof_current_size > auto-aof-rewrite-min-zise && (aof_current_size - aof_base_size) / aof_base_size > auto-aof-rewrite-precentage
```

![image-20201225094629206](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2020/12/2020122509462929.png)

 #### RDB & AOF 比较

| 命令 | 启动优先级 | 体积 | 恢复速度 | 数据安全性   | 轻重 |
| ---- | ---------- | ---- | -------- | ------------ | ---- |
| RDB  | 低         | 小   | 快       | 丢数据       | 重   |
| AOF  | 高         | 大   | 慢       | 根据策略决定 | 轻   |

#### RDB + AOF（redis4.0 - 混合模式）

重启redis恢复数据集时，很少会使用rdb来恢复内存状态，因为会丢失大量数据。通常会使用aof日志恢复数据，但是重放aof日志性能相对rdb来说要慢很多，这样在redis实例很大的情况下，启动需要花费很长时间。为了解决这个问题，redis4.0带来了新的持久化选项—— __混合持久化__。通过配置的方式开启。

```bash
aof-use-rdb-preamble yes
```

如果开启了混合持久化，aof在重写时，不再是单纯将内存数据转换为RESP命令写入aof文件，而是将重写这一刻之前的内存做rdb快照处理，并且将rdb快照内容和增量的aof修改内存数据的命令存在一起，都写入新的aof文件，新的aof文件一开始不叫appendonly.aof，等到重写完成后，新的aof文件才会进行改名，原子的覆盖原有的aof文件，完成新旧两个aof文件的替换。<br>
于是在redis重启的时候，可以先加载rdb文件，然后再重放增量的aof日志就可以完全替代之前的aof全量文件重放，因此重启效率大幅得到提高。