### 主从复制

所有节点间的数据是同步的，一个master，多个slave，其中slave不能写只能读，只有master能读，在部署好slave后，客户端可以向任意的slave发送读请求，而不必总是把读请求发送给master，达到负载均衡的效果，master节点挂掉后，redis就不能对外提供写服务了，因为剩下的slave不能成为master这个缺点影响很大，所以一般生产环境不会单单只用主从复制模式，而是使用sentinel哨兵模式。

如果有多个slave节点并发的向master发送sync命令，企图建立主从关系，只要第二个slave的sync命令发生在master完成bgsave操作之前，第二个slave将受到和第一个slave相同地快照和后续backlog；否则，第二个slave的sync命令会导致master的第二次bgsave。

##### 主从特点：

-   master可以拥有多个slave

-   多个slave可以连接同一个master，还可以连接到其它的slave
-   主从复制不会阻塞master
-   可以提高系统的伸缩性

##### 复制过程：

1.  slave和master建立连接，发送sync同步命令
2.  master接收到指令后，会开启一个后台进程，将数据库快照保存到文件中（RDB方式，bgsave指令），同时master主进程会开始收集新的写命令并缓存到backlog队列
3.  后台进程完成保存后，将文件发送到slave
4.  slave会丢弃所有的旧数据，开始载入master发过来的快照文件
5.  master向slave发送存储在backlog队列中的写命令，发送完毕后，每执行一个写命令，就向slave发送相同地写命令（异步复制）
6.  slave执行master发来的所有存储在缓冲区的写命令，并从现在开始，接受并执行master传来的每个写命令

 

### 哨兵（sentinel）

主从模式中，所有节点间的数据是同步的，当master节点挂掉后，slave节点不能自动选举出一个master出来，此时会导致redis服务的写操作不可用，我们可以通过一个或多个sentinel来处理这种情况，当sentinel发现master节点挂掉后，sentinel就会从slave中重新选举一个master出来。

sentinel是建立在主从模式的基础上的，所以如果只有一个redis节点，那么sentinel则没有任何意义

主从模式配置密码时，sentinel也会同步的将配置信息修改到配置问文件中

当master挂了以后，sentinel会在slave中选择一个作为新的master，并修改它和其它所有slave节点的配置文件，当旧的master重启后，它将不再是master而是以slave的身份接受新的master节点的同步数据

sentinel因为也是一个进程有挂掉的可能，所以一般也会启动多个sentinel形成sentinel集群，一个sentinel或sentinel集群可以管理多个主从redis

当使用sentinel模式的时候，客户端就不需要直接连接redis，而是应该通过sentinel来操作redis服务，由sentinel来提供具体的可提供服务的redis实现

##### 哨兵原理

-   sentinel集群通过给定的配置文件发现master，启动时会监控master，通过向master发送info信息获得该服务器下的所有从服务器

-   sentinel集群通过命令连接向被监视的主从服务器发送hello信息（每秒一次），该信息包括sentinel自身的IP、PORT、ID等内容，以此来向其它sentinel宣告自身的存在

-   sentinel集群通过订阅连接接受其它sentinel发送的hello信息，以此来发现监视同一个主服务器的其它的sentinel；集群之间会相互创建命令连接用于通信，因为已经有主从服务操作为发送和接受hello信息的中介，sentinel之间不会创建订阅连接

-   sentinel集群使用ping命令来检测示例的状态，如果在指定的时间内没有回复或返回错误的会粗，那么该实例会被判定为下线

-   当failover主备切换被触发后，failover并不会马上进行，还需要sentinel中的大多数sentinel授权后才可以进行failover，即进行failover的sentinel会去获得指定quorum个的sentinel授权，成功后进入ODOWN状态（如5个sentinel中配置了两个quorum，等到两个sentinel认为master死了才会执行failover）

-   sentinel向选为master的slave发送SLAVEOF NO ONE命令，**选择slave的条件是sentinel会首先根据slages的优先级来进行排序，优先级越小排名越靠前；如果优先级相同，则查看复制的下标，那个从master接收的复制最多，那个就靠前；如果优先级和小标都相同，就选择进程ID比较小的那个**

-   sentinel被授权后，它将会获得宕掉的master的一份最新配置版本号，当failover执行结束后，这个版本号将会被用于朱新的配置，通过广播的形式通知其它sentinel，其它sentinel则更新对应master的配置

    ___以上：___

    ```
    1、2、3：是自动发现机制：以10秒一次的频率，向被监视的master发送info命令，根据回复获取master当前信息；以每秒一次的频率向所有redis服务器和sentinel发送PING命令，通过回复判断服务是否在线；以2秒一次的频率向所有被监视的master、slave发送当前sentinel、master的信息。
    4：是检测机制
    5、6：是failover机制
    7：是更新配置机制
    ```

##### 哨兵职责：

-   监控：sentinel节点会定期检测redis数据节点和其它sentinel节点是否可达
-   通知：sentinel节点会将故障转移通知给应用方
-   主节点故障转移：实现slave晋升为master并维护后续正确的主从关系
-   配置提供：在redis sentinel结构中，客户端在初始化的时候连接的是sentinel节点集合，从中获取主节点信息



### 集群（cluster）

redis集群中数据是分片存储的，集群的出现是为了解决单机redis容量有限的问题，将redis的数据根据一定的规则分配到多台机器。

cluster可以说是sentinel和主从复制的结合体，通过cluster可以实现主从和master重选操作，cluster配置至少需要3个主节点（每个主节点对应一个从节点，所以需要6个实例）。

因为redis的数据是根据一定的规则分配到cluster不同机器的，当数据量过大时，可以新增机器进行横向扩容，这种模式适合数据量巨大的缓存要求，通常情况下使用sentinel模式就可以了。

redis-cluster通过分区（partition）来提供一定程度的可用性（availability）：即使集群中有一部分节点失效或者无法进行通知，cluster也可以继续处理命令的请求。

##### 集群好处：

-   数据自动切分到多个节点，当cluster中一部分节点不可用时，仍然可以继续处理命令请求的能力
-   不同的master存放不同的数据，所有的master数据并集是所有的数据（master和slave数据是一致的）

##### 数据分片：

###### HASH映射

__不是一致性HASH，而是HASH槽：[ HASH_SLOT = CRC16(K) mod 16384 ] （16384=214)__

在redis官方给出的集群方案中，数据分配是根据槽位来进行分配的，每一个数据的键被哈希函数映射到一个槽位，redis-3.0规定一共有214个槽位，槽位数可以进行配置，当用户GET或者PUT一个数据时，首先会查找数据对应的槽位，然后查找对应的节点，最后把数据放到节点内。这样就做到了把数据均匀的分配到cluster中的每个节点上，从而做到了每一个节点的负载均衡。

计算K字符串对应的映射值，redis 采用了crc16 函数然后与0x3FFF取低16位的方法。crc16以及md5都是比较常用的根据K均匀的分配的函数，就这样，用户传入的一个K我们就映射到一个槽上，然后经过gossip 协议，周期性的和集群中的其他节点交换信息，最终整个集群都会知道K在哪一个槽上。

Redis 集群有16384 个哈希槽,每个K通过CRC16校验后对16384取模来决定放置哪个槽.集群的每个节点负责一部分HASH槽,举个例子,比如当前集群有3个节点,那么:
　　节点A 包含0 到5500 号哈希槽.
　　节点B 包含5501 到11000 号哈希槽.
　　节点C 包含11001 到16384 号哈希槽.
这种结构很容易添加或者删除节点. 比如如果我想新添加个节点D, 我需要从节点A, B, C中得部分槽到D上. 如果我想移除节点A,需要将A中的槽移到B和C节点上,然后将没有任何槽的A节点从集群中移除即可. 由于从一个节点将哈希槽移动到另一个节点并不会停止服务,所以无论添加删除或者改变某个节点的哈希槽的数量都不会造成集群不可用的状态.

位序列结构：Master节点维护着一个16384/8字节的位序列，Master节点用bit来标识对于某个槽自己是否拥有。比如对于编号为1的槽，Master只要判断序列的第二位（索引从0 开始）是不是为1即可。

###### 范围映射

范围映射通常选择K本身而非K的函数计算值来作为数据分布的条件，且每个节点存放的K值域是一段连续的范围。

K的值域时通过业务决定的，业务层需要清楚每个区间的范围和redis实例数量，才能完整的描述数据分布，这使业务层的K值域与系统层的实例数耦合，数据分片无法在纯系统层实现，需要和业务层结合。

###### HASH和范围映射结合

首先对K进行hash计算，得到值域有限的hash值，再对hash值做范围映射，确定该K对应的业务数据存放的具体实例，这种方式的优势是节点新增或者退出时，涉及的数据迁移量小（变更节点上涉及的数据只需要和相邻的节点发生迁移关系）

###### 哈希标签

是一种可以让用户指定将一批K都能够被存放到同一个槽位中的实现方法，用户唯一要做的就是按照既定的规则生成K即可。redis在计算槽位的时候只会获取{}之间的字符串进行槽位编号计算，我们只需要保证多个K的{}内的字符串是相同的，就可以将他们分配到同一个槽位中：【abc{yanwu12138}def 和 ghi{yanwu12138}jkl 两个K会被分配到同一个槽位，因为{}中的字符串都是yanwu12138】

###### 节点通信（gossip）

redis-cluster节点间通过gossip协议来进行通信。gossip协议简单来说就是集群中每个节点会由于网络分化、节点抖动等原因而具有不同的cluster全局视图，节点间通过gossip协议进行节点信息共享，这是业界比较流行的去中心化方案。gossip协议由MEET、PING、PONG三种消息实现，这三种消息实现的正文都是由两个clusterMsgDataGossip结构组成。

节点间共享的关键信息有以下几点：

-   数据分片和节点的对应关系
-   cluster中每个节点的可用状态
-   cluster结构发生变化时，通过一定的协议对配置信息达成一致
-   phb/sub功能在cluster的内部实现所需要交互的信息

##### 集群的主从选举：

redis-cluster重用了sentinel的选举代码，cluster中每个节点都会定期的向其它节点发送PING消息，以此来交换各个节点的状态信息，节点分为三种状态：【在线、疑似下线、下线】

![image-20201225100504520](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2020/12/202012251005044.png)

#####  故障转移

1.  从下线master的所有slave中选择一个slave
2.  被选中的slave执行SLAVE NO NOE命令，成为新的master
3.  新master会撤销所有对已下线master的槽指派，并将这些槽都指派给自己
4.  新maser对cluster广播PONG消息，告知其他节点自己已经成为新的master
5.  新master开始接受和处理槽相关的请求

##### 功能限制

redis-cluster相对于单机在功能上有一定的局限性

1.  K批量操作的支持有限
2.  K事务操作支持有限，支持多个K在同一个节点上的事务，不支持分布在多个节点的事务功能
3.  K作为数据区分的最小粒度，所以不能将一个大的键值对象映射到不同的节点（HASH、LIST）
4.  不支持多数据库空间，单机的redis支持16个数据库空间，集群下只能使用db0一个数据库空间
5.  复制结构只能复制一层，不支持嵌套树状复制结构

