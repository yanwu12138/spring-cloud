### Elasticsearch

>   Elasticsearch是一个基于Apache Lucene(TM)的开源搜索引擎，无论在开源还是专有领域，Lucene可以被认为是迄今为止最先进、性能最好的、功能最全的搜索引擎库。 
>   但是，Lucene只是一个库。想要发挥其强大的作用，你需使用Java并要将其集成到你的应用中。Lucene非常复杂，你需要深入的了解检索相关知识来理解它是如何工作的。 
>   Elasticsearch也是使用Java编写并使用Lucene来建立索引并实现搜索功能，但是它的目的是通过简单连贯的RESTful API让全文搜索变得简单并隐藏Lucene的复杂性。 
>   不过，Elasticsearch不仅仅是Lucene和全文搜索引擎，它还提供：
>
>   -   分布式的实时文件存储，每个字段都被索引并可被搜索
>   -   实时分析的分布式搜索引擎
>   -   可以扩展到上百台服务器，处理PB级结构化或非结构化数据
>
>   而且，所有的这些功能被集成到一台服务器，你的应用可以通过简单的RESTful API、各种语言的客户端甚至命令行与之交互。上手Elasticsearch非常简单，它提供了许多合理的缺省值，并对初学者隐藏了复杂的搜索引擎理论。它开箱即用（安装即可使用），只需很少的学习既可在生产环境中使用。Elasticsearch在Apache 2 license下许可使用，可以免费下载、使用和修改。 
>   随着知识的积累，你可以根据不同的问题领域定制Elasticsearch的高级特性，这一切都是可配置的，并且配置非常灵活。



#### 基本概念

##### 接近实时（NRT）

Elasticsearch是一个接近实时的搜索平台。这意味着，从索引一个文档直到这个文档能够被搜索到有一个轻微的延迟（通常是1秒）。

##### 集群（cluster）

一个集群就是由一个或多个节点组织在一起，它们共同持有你整个的数据，并一起提供索引和搜索功能。一个集群由一个唯一的名字标识，这个名字默认就是“elasticsearch”。这个名字是重要的，因为一个节点只能通过指定某个集群的名字，来加入这个集群。在产品环境中显式地设定这个名字是一个好习惯，但是使用默认值来进行测试/开发也是不错的。

##### 节点（node）

一个节点是你集群中的一个服务器，作为集群的一部分，它存储你的数据，参与集群的索引和搜索功能。和集群类似，一个节点也是由一个名字来标识的，默认情况下，这个名字是一个随机的漫威漫画角色的名字，这个名字会在启动的时候赋予节点。这个名字对于管理工作来说挺重要的，因为在这个管理过程中，你会去确定网络中的哪些服务器对应于Elasticsearch集群中的哪些节点。

一个节点可以通过配置集群名称的方式来加入一个指定的集群。默认情况下，每个节点都会被安排加入到一个叫做“elasticsearch”的集群中，这意味着，如果你在你的网络中启动了若干个节点，并假定它们能够相互发现彼此，它们将会自动地形成并加入到一个叫做“elasticsearch”的集群中。

在一个集群里，只要你想，可以拥有任意多个节点。而且，如果当前你的网络中没有运行任何Elasticsearch节点，这时启动一个节点，会默认创建并加入一个叫做“elasticsearch”的集群。

##### 索引（index）

一个索引就是一个拥有几分相似特征的文档的集合。比如说，你可以有一个客户数据的索引，另一个产品目录的索引，还有一个订单数据的索引。一个索引由一个名字来标识（必须全部是小写字母的），并且当我们要对对应于这个索引中的文档进行索引、搜索、更新和删除的时候，都要使用到这个名字。索引类似于关系型数据库中Database的概念。在一个集群中，如果你想，可以定义任意多的索引。

##### 类型（type）

在一个索引中，你可以定义一种或多种类型。一个类型是你的索引的一个逻辑上的分类/分区，其语义完全由你来定。通常，会为具有一组共同字段的文档定义一个类型。比如说，我们假设你运营一个博客平台并且将你所有的数据存储到一个索引中。在这个索引中，你可以为用户数据定义一个类型，为博客数据定义另一个类型，当然，也可以为评论数据定义另一个类型。类型类似于关系型数据库中Table的概念。

##### 文档（document）

一个文档是一个可被索引的基础信息单元。比如，你可以拥有某一个客户的文档，某一个产品的一个文档，当然，也可以拥有某个订单的一个文档。文档以JSON格式来表示，而JSON是一个到处存在的互联网数据交互格式。 
在一个index/type里面，只要你想，你可以存储任意多的文档。注意，尽管一个文档，物理上存在于一个索引之中，文档必须被索引/赋予一个索引的type。文档类似于关系型数据库中Record的概念。实际上一个文档除了用户定义的数据外，还包括`_index`、`_type`和`_id`字段。

##### 字段（mapping）

Mapping是ES中的一个很重要的内容，它类似于传统关系型数据中table的schema，用于定义一个索引（index）的某个类型（type）的数据的结构。在ES中，我们无需手动创建type（相当于table）和mapping(相关与schema)。在默认配置下，ES可以根据插入的数据自动地创建type及其mapping。

mapping中主要包括字段名、字段数据类型和字段索引类型这3个方面的定义：

-   字段名：这就不用说了，与传统数据库字段名作用一样，就是给字段起个唯一的名字，好让系统和用户能识别。

-   字段数据类型：定义该字段保存的数据的类型，不符合数据类型定义的数据不能保存到ES中。下表列出的是ES中所支持的数据类型。（大类是对所有类型的一种归类，小类是实际使用的类型。）

    | 大类           | 包含的小类                 |
    | -------------- | -------------------------- |
    | String         | string                     |
    | number         | byte, short, integer, long |
    | Floating point | float, double              |
    | Boolean        | boolean                    |
    | Date           | date                       |

-   字段索引类型：索引是ES中的核心，ES之所以能够实现实时搜索，完全归功于Lucene这个优秀的Java开源索引。在传统数据库中，如果字段上建立索引，我们仍然能够以它作为查询条件进行查询，只不过查询速度慢点。而在ES中，字段如果不建立索引，则就不能以这个字段作为查询条件来搜索。也就是说，不建立索引的字段仅仅能起到数据载体的作用。string类型的数据肯定是日常使用得最多的数据类型，下面介绍mapping中string类型字段可以配置的索引类型。

    | 索索引类型   | 说明                                                         |
    | ------------ | ------------------------------------------------------------ |
    | analyzed     | 首先分析这个字符串，然后再建立索引。换言之，以全文形式索引此字段。 |
    | not_analyzed | 索引这个字段，使之可以被搜索，但是索引内容和指定值一样。不分析此字段。 |
    | no           | 不索引这个字段。这个字段不能被搜索到。                       |

    如果索引类型设置为analyzed，在表示ES会先对这个字段进行分析（一般来说，就是自然语言中的分词），ES内置了不少分析器（analyser），如果觉得它们对中文的支持不好，也可以使用第三方分析器。由于笔者在实际项目中仅仅将ES用作普通的数据查询引擎，所以并没有研究过这些分析器。如果将ES当做真正的搜索引擎，那么挑选正确的分析器是至关重要的。

##### 分片和复制（shards & replicas）

一个索引可以存储超出单个结点硬件限制的大量数据。比如，一个具有10亿文档的索引占据1TB的磁盘空间，而任一节点都没有这样大的磁盘空间；或者单个节点处理搜索请求，响应太慢。

为了解决这个问题，Elasticsearch提供了将索引划分成多份的能力，这些份就叫做分片。当你创建一个索引的时候，你可以指定你想要的分片的数量。每个分片本身也是一个功能完善并且独立的“索引”，这个“索引”可以被放置到集群中的任何节点上。 
分片之所以重要，主要有两方面的原因：

1.  允许你水平分割/扩展你的内容容量
2.  允许你在分片（潜在地，位于多个节点上）之上进行分布式的、并行的操作，进而提高性能/吞吐量

至于一个分片怎样分布，它的文档怎样聚合回搜索请求，是完全由Elasticsearch管理的，对于作为用户的你来说，这些都是透明的。

在一个网络/云的环境里，失败随时都可能发生，在某个分片/节点不知怎么的就处于离线状态，或者由于任何原因消失了。这种情况下，有一个故障转移机制是非常有用并且是强烈推荐的。为此目的，Elasticsearch允许你创建分片的一份或多份拷贝，这些拷贝叫做复制分片，或者直接叫复制。复制之所以重要，主要有两方面的原因：

1.  在分片/节点失败的情况下，提供了高可用性。因为这个原因，注意到复制分片从不与原/主要（original/primary）分片置于同一节点上是非常重要的。
2.  扩展你的搜索量/吞吐量，因为搜索可以在所有的复制上并行运行

总之，每个索引可以被分成多个分片。一个索引也可以被复制0次（意思是没有复制）或多次。一旦复制了，每个索引就有了主分片（作为复制源的原来的分片）和复制分片（主分片的拷贝）之别。分片和复制的数量可以在索引创建的时候指定。在索引创建之后，你可以在任何时候动态地改变复制数量，但是不能改变分片的数量。

默认情况下，Elasticsearch中的每个索引被分片5个主分片和1个复制，这意味着，如果你的集群中至少有两个节点，你的索引将会有5个主分片和另外5个复制分片（1个完全拷贝），这样的话每个索引总共就有10个分片。一个索引的多个分片可以存放在集群中的一台主机上，也可以存放在多台主机上，这取决于你的集群机器数量。主分片和复制分片的具体位置是由ES内在的策略所决定的。



#### centOS安装

ElasticSearch需要**Java 8 环境**。安装Java环境后就可以跟着官方文档安装ElasticSearch。

##### 下载与解压

```shell
##### 下载
wget https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-5.5.1.zip
##### 解压缩
unzip elasticsearch-5.5.1.zip
##### 更改目录名
mv elasticsearch-5.5.1 elasticsearch
```

##### 创建用户

```shell
##### 创建用户
adduser elasticsearch
##### 设置密码
passwd elasticsearch
##### 将对应的文件夹权限赋给该用户
chown -R elasticsearch elasticsearch
```

##### 修改配置

```shell
vim elasticsearch/config/elasticsearch.yml

##### 将其中的network.host改为0.0.0.0
network.host: 0.0.0.0
```

##### 启动

```shell
cd elasticsearch/bin

##### 使用elasticsearch用户启动
sudo -su elasticsearch ./elasticsearch -d -Xmx2g -Xms2g
##### 使用root用户启动
./elasticsearch -Des.insecure.allow.root=true -d
```

##### 检验是否安装成功

![image-20210129152141298](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021012915214141.png)



#### [docker安装](../docker/容器/docker安装ElasticSearch.md)



#### ElasticSearch配置

>   配置文件所在的目录路径如下：elasticsearch/config/elasticsearch.yml，下面介绍一些重要的配置项及其含义。

1.  cluster.name: elasticsearch

    >   配置elasticsearch的集群名称，默认是elasticsearch。elasticsearch会自动发现在同一网段下的集群名为elasticsearch的主机，如果在同一网段下有多个集群，就可以用这个属性来区分不同的集群。生成环境时建议更改。

2.  node.name: “Franz Kafka”

    >   节点名，默认随机指定一个name列表中名字，该列表在elasticsearch的jar包中config文件夹里name.txt文件中，其中有很多作者添加的有趣名字，大部分是漫威动漫里面的人物名字。生成环境中建议更改以能方便的指定集群中的节点对应的机器

3.  node.master: true

    >   指定该节点是否有资格被选举成为master，默认是true，elasticsearch默认集群中的第一台启动的机器为master，如果这台机挂了就会重新选举master。

4.  node.data: true

    >   指定该节点是否存储索引数据，默认为true。如果节点配置node.master:false并且node.data: false，则该节点将起到负载均衡的作用

5.  index.number_of_shards: 5

    >   设置默认索引分片个数，默认为5片。经本人测试，索引分片对ES的查询性能有很大的影响，在应用环境，应该选择适合的分片大小。

6.  index.number_of_replicas: 1

    >   设置默认索引副本个数，默认为1个副本。此处的1个副本是指index.number_of_shards的一个完全拷贝；默认5个分片1个拷贝；即总分片数为10。

7.  path.conf: /path/to/conf

    >   设置配置文件的存储路径，默认是es根目录下的config文件夹。

8.  path.data:/path/to/data1,/path/to/data2

    >   设置索引数据的存储路径，默认是es根目录下的data文件夹，可以设置多个存储路径，用逗号隔开。

9.  path.work:/path/to/work

    >   设置临时文件的存储路径，默认是es根目录下的work文件夹。

10.  path.logs: /path/to/logs

     >   设置日志文件的存储路径，默认是es根目录下的logs文件夹

11.  path.plugins: /path/to/plugins

     >   设置插件的存放路径，默认是es根目录下的plugins文件夹

12.  bootstrap.mlockall: true

     >   设置为true来锁住内存。因为当jvm开始swapping时es的效率会降低，所以要保证它不swap，可以把ES_MIN_MEM和ES_MAX_MEM两个环境变量设置成同一个值，并且保证机器有足够的内存分配给es。同时也要允许elasticsearch的进程可以锁住内存，linux下可以通过`ulimit -l unlimited`命令。

13.  network.bind_host: 192.168.0.1

     >   设置绑定的ip地址，可以是ipv4或ipv6的，默认为0.0.0.0。

14.  network.publish_host: 192.168.0.1

     >   设置其它节点和该节点交互的ip地址，如果不设置它会自动判断，值必须是个真实的ip地址。

15.  network.host: 192.168.0.1

     >   这个参数是用来同时设置bind_host和publish_host上面两个参数。

16.  transport.tcp.port: 9300

     >   设置节点间交互的tcp端口，默认是9300。

17.  transport.tcp.compress: true

     >   设置是否压缩tcp传输时的数据，默认为false，不压缩。

18.  http.port: 9200

     >   设置对外服务的http端口，默认为9200。

19.  http.max_content_length: 100mb

     >   设置内容的最大容量，默认100mb

20.  http.enabled: false

     >   是否使用http协议对外提供服务，默认为true，开启。

21.  gateway.type: local

     >   gateway的类型，默认为local即为本地文件系统，可以设置为本地文件系统，分布式文件系统，hadoop的HDFS，和amazon的s3服务器，其它文件系统的设置。

22.  gateway.recover_after_nodes: 1

     >   设置集群中N个节点启动时进行数据恢复，默认为1。

23.  gateway.recover_after_time: 5m

     >   设置初始化数据恢复进程的超时时间，默认是5分钟。

24.  gateway.expected_nodes: 2

     >   设置这个集群中节点的数量，默认为2，一旦这N个节点启动，就会立即进行数据恢复。

25.  cluster.routing.allocation.node_initial_primaries_recoveries: 4

     >   初始化数据恢复时，并发恢复线程的个数，默认为4。

26.  cluster.routing.allocation.node_concurrent_recoveries: 2

     >   添加删除节点或负载均衡时并发恢复线程的个数，默认为4。

27.  indices.recovery.max_size_per_sec: 0

     >   设置数据恢复时限制的带宽，如入100mb，默认为0，即无限制。

28.  indices.recovery.concurrent_streams: 5

     >   设置这个参数来限制从其它分片恢复数据时最大同时打开并发流的个数，默认为5。

29.  discovery.zen.minimum_master_nodes: 1

     >   设置这个参数来保证集群中的节点可以知道其它N个有master资格的节点。默认为1，对于大的集群来说，可以设置大一点的值（2-4）

30.  discovery.zen.ping.timeout: 3s

     >   设置集群中自动发现其它节点时ping连接超时时间，默认为3秒，对于比较差的网络环境可以高点的值来防止自动发现时出错。

31.  discovery.zen.ping.multicast.enabled: false

     >   设置是否打开多播发现节点，默认是true。

32.  discovery.zen.ping.unicast.hosts: [“host1”, “host2:port”, “host3 [portX-portY] “]

     >   设置集群中master节点的初始列表，可以通过这些节点来自动发现新加入集群的节点。













