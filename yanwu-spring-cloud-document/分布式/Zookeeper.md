#### 什么是Zookeeper

ZooKeeper 是 Apache 软件基金会的一个软件项目，它为大型分布式计算提供开源的分布式配置服务、同步服务和命名注册。

ZooKeeper 的架构通过冗余服务实现高可用性。

Zookeeper 的设计目标是将那些复杂且容易出错的分布式一致性服务封装起来，构成一个高效可靠的原语集，并以一系列简单易用的接口提供给用户使用。

一个典型的分布式数据一致性的解决方案，分布式应用程序可以基于它实现诸如数据发布/订阅、负载均衡、命名服务、分布式协调/通知、集群管理、Master 选举、分布式锁和分布式队列等功能。



#### Zookeeper的集群结构

![image-20210122153832148](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021012215383232.png)

在ZooKeeper集群当中，集群中的服务器角色有两种：1个Leader和多个Follower，具体功能如下：

1.  leader：领导者，负责进行投票的发起和决议，监控集群中的(follower)是否存活（心跳机制），进行分配资源

2.  follower：用于接受客户端请求并向客户端返回结果，在选主过程中参与投票

__特点：__

-   Zookeeper：一个leader，多个follower组成的集群

-   全局数据一致：每个server保存一份相同的数据副本，client无论连接到哪个server，数据都是一致的

-   数据更新原子性，一次数据更新要么成功，要么失败

-   实时性，在一定时间范围内，client能读到最新数据

-   半数机制：整个集群中只要有一半以上存活，就可以提供服务。因此通常Zookeeper由2n+1台servers组成，每个server都知道彼此的存在。每个server都维护的内存状态镜像以及持久化存储的事务日志和快照。为了保证Leader选举能过得到多数的支持，所以ZooKeeper集群的数量一般为奇数。对于2n+1台server，只要有n+1台（大多数）server可用，整个系统保持可用



#### ZooKeeper数据模型：Znode

>   在 zookeeper 中，可以说 zookeeper 中的所有存储的数据是由 znode 组成的，节点也称为 znode，并以 key/value 形式存储数据。
>
>   整体结构类似于 linux 文件系统的模式以树形结构存储。其中根路径以 **/** 开头。
>
>   进入 zookeeper 安装的 bin 目录，通过`sh zkCli.sh`打开命令行终端，执行 "ls /" 命令显示：
>
>   ```shell
>   $ ls /
>   $ ls /zookeeper
>   $ ls /zookeeper/quota
>   ```
>
>   ![image-20210122173314235](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021012217331414.png)
>
>   

我们直观的看到此时存储的数据在根目录下存在 runoob 和 zookeeper 两个节点，zookeeper 节点下存在 quota 这个节点。

![image-20210122173343748](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021012217334343.png)

##### Znode 的状态属性

| 属性           | 说明                                                         |
| -------------- | ------------------------------------------------------------ |
| cZxid          | 创建节点时的事务ID                                           |
| ctime          | 创建节点时的时间                                             |
| mZxid          | 最后修改节点时的事务ID                                       |
| mtime          | 最后修改节点时的时间                                         |
| pZxid          | 表示该节点的子节点列表最后一次修改的事务ID，添加子节点或删除子节点就会影响子节点列表，但是修改子节点的数据内容则不影响该ID**（注意，只有子节点列表变更了才会变更pzxid，子节点内容变更不会影响pzxid）** |
| cversion       | 子节点版本号，子节点每次修改版本号加1                        |
| dataversion    | 数据版本号，数据每次修改该版本号加1                          |
| aclversion     | 权限版本号，权限每次修改该版本号加1                          |
| ephemeralOwner | 创建该临时节点的会话的sessionID。（**如果该节点是持久节点，那么这个属性值为0）** |
| dataLength     | 该节点的数据长度                                             |
| numChildren    | 该节点拥有子节点的数量**（只统计直接子节点的数量）**         |



#### Zookeeper数据结构

>    zookkeeper 提供的名称空间非常类似于标准文件系统，key-value 的形式存储。名称 key 由斜线 `/` 分割的一系列路径元素，zookeeper 名称空间中的每个节点都是由一个路径标识。
>
>   ![image-20210122170526515](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021012217052626.png)

##### CAP

>   CAP 理论指出对于一个分布式计算系统来说，不可能同时满足以下三点：
>
>   -   **一致性**：在分布式环境中，一致性是指数据在多个副本之间是否能够保持一致的特性，等同于所有节点访问同一份最新的数据副本。在一致性的需求下，当一个系统在数据一致的状态下执行更新操作后，应该保证系统的数据仍然处于一致的状态。
>   -   **可用性：**每次请求都能获取到正确的响应，但是不保证获取的数据为最新数据。
>   -   **分区容错性：**分布式系统在遇到任何网络分区故障的时候，仍然需要能够保证对外提供满足一致性和可用性的服务，除非是整个网络环境都发生了故障。

一个分布式系统最多只能同时满足一致性（Consistency）、可用性（Availability）和分区容错性（Partition tolerance）这三项中的两项。

在这三个基本需求中，最多只能同时满足其中的两项，P 是必须的，因此只能在 CP 和 AP 中选择，zookeeper 保证的是 CP，对比 spring cloud 系统中的注册中心 eruka 实现的是 AP。

![image-20210122170702123](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/202101221707022.png)

##### BASE

>   BASE 是 Basically Available(基本可用)、Soft-state(软状态) 和 Eventually Consistent(最终一致性) 三个短语的缩写。
>
>   -   **基本可用：**在分布式系统出现故障，允许损失部分可用性（服务降级、页面降级）。
>   -   **软状态：**允许分布式系统出现中间状态。而且中间状态不影响系统的可用性。这里的中间状态是指不同的 data replication（数据备份节点）之间的数据更新可以出现延时的最终一致性。
>   -   **最终一致性：**data replications 经过一段时间达到一致性。

BASE 理论是对 CAP 中的一致性和可用性进行一个权衡的结果，理论的核心思想就是：我们无法做到强一致，但每个应用都可以根据自身的业务特点，采用适当的方式来使系统达到最终一致性。



#### Zookeeper数据同步

>   在 Zookeeper 中，主要依赖 ZAB 协议来实现分布式数据一致性。
>
>   ZAB 协议分为两部分：
>
>   -   消息广播
>   -   崩溃恢复

##### 消息广播

Zookeeper 使用单一的主进程 Leader 来接收和处理客户端所有事务请求，并采用 ZAB 协议的原子广播协议，将事务请求以 Proposal 提议广播到所有 Follower 节点，当集群中有过半的Follower 服务器进行正确的 ACK 反馈，那么Leader就会再次向所有的 Follower 服务器发送commit 消息，将此次提案进行提交。这个过程可以简称为 2pc 事务提交，整个流程可以参考下图，注意 Observer 节点只负责同步 Leader 数据，不参与 2PC 数据同步过程。

![image-20210122172439967](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021012217244040.png)

##### 崩溃恢复

在正常情况消息广播情况下能运行良好，但是一旦 Leader 服务器出现崩溃，或者由于网络原理导致 Leader 服务器失去了与过半 Follower 的通信，那么就会进入崩溃恢复模式，需要选举出一个新的 Leader 服务器。在这个过程中可能会出现两种数据不一致性的隐患，需要 ZAB 协议的特性进行避免。

1.  Leader 服务器将消息 commit 发出后，立即崩溃

2.  Leader 服务器刚提出 proposal 后，立即崩溃

ZAB 协议的恢复模式使用了以下策略：

1.  选举 zxid 最大的节点作为新的 leader

2.  新 leader 将事务日志中尚未提交的消息进行处理



#### Zookeeper集群选举原理

>   zookeeper 的 leader 选举存在两个阶段，一个是服务器启动时 leader 选举，另一个是运行过程中 leader 服务器宕机。在分析选举原理前，先介绍几个重要的参数。
>
>   -   服务器 ID(myid)：编号越大在选举算法中权重越大
>   -   事务 ID(zxid)：值越大说明数据越新，权重越大
>   -   逻辑时钟(epoch-logicalclock)：同一轮投票过程中的逻辑时钟值是相同的，每投完一次值会增加

__选举状态：__

-   **LOOKING**：竞选状态
-   **FOLLOWING**：随从状态，同步 leader 状态，参与投票
-   **OBSERVING**：观察状态，同步 leader 状态，不参与投票
-   **LEADING**：领导者状态

##### 服务器启动时的 leader 选举

每个节点启动的时候都 LOOKING 观望状态，接下来就开始进行选举主流程。这里选取三台机器组成的集群为例。第一台服务器 server1启动时，无法进行 leader 选举，当第二台服务器 server2 启动时，两台机器可以相互通信，进入 leader 选举过程。

1.  每台 server 发出一个投票，由于是初始情况，server1 和 server2 都将自己作为 leader 服务器进行投票，每次投票包含所推举的服务器myid、zxid、epoch，使用（myid，zxid）表示，此时 server1 投票为（1,0），server2 投票为（2,0），然后将各自投票发送给集群中其他机器。

2.  接收来自各个服务器的投票。集群中的每个服务器收到投票后，首先判断该投票的有效性，如检查是否是本轮投票（epoch）、是否来自 LOOKING 状态的服务器。

3.  分别处理投票。针对每一次投票，服务器都需要将其他服务器的投票和自己的投票进行对比，对比规则如下：

    a. 优先比较 epoch

    b. 检查 zxid，zxid 比较大的服务器优先作为 leader

    c. 如果 zxid 相同，那么就比较 myid，myid 较大的服务器作为 leader 服务器

4.  统计投票。每次投票后，服务器统计投票信息，判断是都有过半机器接收到相同的投票信息。server1、server2 都统计出集群中有两台机器接受了（2,0）的投票信息，此时已经选出了 server2 为 leader 节点。

5.  改变服务器状态。一旦确定了 leader，每个服务器响应更新自己的状态，如果是 follower，那么就变更为 FOLLOWING，如果是 Leader，变更为 LEADING。此时 server3继续启动，直接加入变更自己为 FOLLOWING。

![image-20210122171238383](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021012217123838.png)

##### 运行过程中的 leader 选举

当集群中 leader 服务器出现宕机或者不可用情况时，整个集群无法对外提供服务，进入新一轮的 leader 选举。

1.  变更状态。leader 挂后，其他非 Oberver服务器将自身服务器状态变更为 LOOKING。

2.  每个 server 发出一个投票。在运行期间，每个服务器上 zxid 可能不同。

3.  处理投票。规则同启动过程。

4.  统计投票。与启动过程相同。

5.  改变服务器状态。与启动过程相同。



#### Zookeeper的作用

Zookeeper包含一个简单的原语集，分布式应用程序可以基于它实现命名服务、配置维护、集群选主等：

-   命名服务：注册节点信息，形成有层次的目录结构（类似Java的包名）。

-   配置维护：配置信息的统一管理和动态切换

-   集群选主：确保整个集群中只有一个主，其它为从。并且当主挂了后，可以从新选主



#### Zookeeper集群安装

1.  下载安装包

    ```shell
    wget https://mirror.bit.edu.cn/apache/zookeeper/zookeeper-3.5.9/apache-zookeeper-3.5.9-bin.tar.gz
    ```

2.  解压缩安装包

    ```shell
    tar -zxvf apache-zookeeper-3.5.9-bin.tar.gz
    ```

3.  重命名目录

    ```shell
    ##### 由于某些机器无法识别标点，因此我们可以把目录的名称做修改
    mv apache-zookeeper-3.5.9-bin/ zookeeper
    ```

4.  修改配置文件

    ```shell
    ##### cd zookeeper
    cd zookeeper/conf/
    ##### 复制模板文件
    cp zoo_sample.cfg zoo.cfg
    ##### 修改配置文件信息，添加以下内容
    vim zoo.cfg
    
    ########## 要添加的内容 ##########
    ##### dataDir：数据目录
    ##### dataLogDir：日志目录
    ##### server.*：port1:port2 指定所有zookeeper的节点信息，数字是节点ID，port1是心跳端口，port2是数据端口
    dataDir=/usr/local/src/zookeeper/data
    dataLogDir=/usr/local/src/zookeeper/log
    server.1=192.168.56.11:2888:3888
    server.2=192.168.56.12:2888:3888
    server.3=192.168.56.13:2888:3888
    ```

5.  创建数据目录和日志目录

    ```shell
    mkdir –m 755 data
    mkdir –m 755 log
    ```

6.  添加节点ID信息

    ```shell
    ##### 进入data目录，创建文件myid，并且写上ID信息：1
    cd zookeeper/data/
    ##### 1，其它节点的ID必须与配置文件中的ID一致，分别是2和3
    vim myid
    ```

    ![image-20210122170035772](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021012217003535.png)

7.  配置zookeeper环境变量

    ```shell
    vi /etc/profile
    
    export ZOOKEEPER_HOME=/usr/local/src/zookeeper
    export PATH=$PATH:$ZOOKEEPER_HOME/bin
    
    source /etc/profile
    ```

8.  启动停止server

    1.  启动zookeeper

        ```shell
        ##### 启动
        zkServer.sh start
        ##### 观察启动日志启动
        zkServer.sh start-foreground
        ```

    2.  停止zookeeper

        ```shell
        zkServer.sh stop
        ```

    3.  查看zookeeper状态

        ```shell
        zkServer.sh status
        ```



#### zookeeper终端

##### 启动终端

```shell
zkCli.sh
```

##### ls

>   查看某个路径下目录列表。
>
>   ![image-20210122173904016](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/202101221739044.png)

```shell
##### path：路径
ls {path}
```

##### ls2

>   查看某个路径下目录列表，它比 ls 命令列出更多的详细信息
>
>   ![image-20210122174022661](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021012217402222.png)

```shell
##### path：路径
ls2 {path}
```

##### get

>   获取节点数据和状态信息
>
>   ![image-20210122174444725](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021012217444444.png)

```shell
##### path：路径
##### watch：可选参数，对节点进行事件监听，当进行事件监听时，其他终端或节点修改了数据，该节点会受到监听事件
get {path} [watch]
```

##### stat

>   查看节点状态信息
>
>   ![image-20210122174642624](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021012217464242.png)

```shell
##### path：路径
##### watch：可选参数，对节点进行事件监听，当进行事件监听时，其他终端或节点修改了数据，该节点会受到监听事件
stat {path} [watch]
```

##### create

>   创建节点并赋值
>
>   ![image-20210122174854327](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021012217485454.png)

```shell
##### -s：顺序节点，节点按照顺序进行排序
##### -e：临时节点，其中 -s 和 -e 可以同时使用的，并且临时节点不能再创建子节点。
##### path：指定要创建节点的路径，比如 /runoob。
##### data：要在此节点存储的数据。
##### acl：访问权限相关，默认是 world，相当于全世界都能访问。
create [-s] [-e] {path} {data} [acl]
```

##### set

>   修改节点存储的数据
>
>   ![image-20210122175213054](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021012217521313.png)

```shell
##### path：路径
##### data：需要存储的数据
##### version: 可选项，版本号(可用作乐观锁)
set path data [version]
```

##### delete

>   删除某节点
>
>   ![image-20210122175359365](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021012217535959.png)

```shell
##### path：路径
##### version：版本号（同 set 命令）
delete path [version]
```



#### Zookeeper for java

##### pom.xml依赖

```xml
<dependency>
	<groupId>org.apache.zookeeper</groupId>
	<artifactId>zookeeper</artifactId>
	<version>3.4.8</version>
</dependency>
<dependency>
	<groupId>org.apache.curator</groupId>
	<artifactId>curator-framework</artifactId>
	<version>4.0.0</version>
</dependency>
<dependency>
	<groupId>org.apache.curator</groupId>
 	<artifactId>curator-recipes</artifactId>
  	<version>4.0.0</version>
</dependency>
```

##### curator连接

Curator 是 Netflix 公司开源的一套 zookeeper 客户端框架，解决了很多 Zookeeper 客户端非常底层的细节开发工作，包括连接重连、反复注册 Watcher 和 NodeExistsException 异常等。

Curator 包含了几个包：

-   **curator-framework**：对 zookeeper 的底层 api 的一些封装。
-   **curator-client**：提供一些客户端的操作，例如重试策略等。
-   **curator-recipes**：封装了一些高级特性，如：Cache 事件监听、选举、分布式锁、分布式计数器、分布式 Barrier 等。