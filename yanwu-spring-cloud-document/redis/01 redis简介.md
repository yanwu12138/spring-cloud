#### 什么是redis

redis是基于内存的一个key-value的数据库，通常用来作为缓存使用。

redis是一个使用ANSI C编写的开源、支持网络、基于内存、可选持久性的键值对存储数据库

其具有以下特点：开源、多种数据结构、基于键值的存储服务器、高性能、功能服务

-   优点：数据类型丰富、效率高、支持集群、支持持久化

-   缺点：单进程单线程，长命令可能会导致redis阻塞、集群下多key同时操作无法使用

#### [跳表](https://www.jianshu.com/p/54d37710b2a6)

![clipboard](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/20200628135827.png)

#### 为什么快

- 完全基于内存，绝大部分请求是纯粹的内存操作，非常快速
- 数据结构简单，对数据操作也简单
- 采用单线程，避免了不必要的上下文切换和竞争条件
- 使用多路I/O复用模型，非阻塞IO

#### 多路复用

redis 内部使用文件事件处理器，这个文件事件处理器是单线程的，所以redis才叫做单线程的模型。它采用IO多路复用机制同时监听多个socket，根据socket上的事件来选择对应的事件处理器进行处理。文件事件处理器的结构包含 4 个部分：
- 多个socket
- IO多路复用程序
- 文件事件分派器
- 事件处理器（连接应答处理器、命令请求处理器、命令回复处理器）

多个socket可能会并发产生不同的操作，每个操作对应不同的文件事件，但是IO多路复用程序会监听多个socket，会将socket产生的事件放入队列中排队，事件分派器每次从队列中取出一个事件，把该事件交给对应的事件处理器进行处理

#### redis的优势

- 由于是基于内存的，所以redis读写速度快
- 支持丰富的数据类型（5种）
- 支持事务的原子性

#### redis与memcached

- memcache只支持string，redis可以支持多种数据类型
- memcache无法进行持久化，redis可以支持持久化
- memcache是多核的，redis是核的
- 都支持主从，但redis的主从是做备份的，memcache的主从都可以进行读写

#### 源码

线程模型-单线程，对于命令处理是单线程的，在IO层面同时面向多个客户端并发的提供服务，IO多路复用。

##### redis单线程快的原因

-   纯内存，不需要操作磁盘
-   非阻塞IO
-   避免线程切换和竞态消耗

##### 拒绝慢命令

　　KEYS、FLUSHALL、FLUSHDB、SHOW LUA SCREIPT、MUTIL/EXEC、OPERATE BIG VALUE

##### RedisObject

```c
typedef struct redisObject {
    /* 4位type表示具体的数据类型，redis中一共有5中数据类型，24足以表示所有的类型 */
    unsingned type:4;
    /* 4位encoding表示该类型的物理编码方式，同一种数据类型可能有不同的编码方式，目前redis主要有8种编码方式 */
    unsingned encoding:2;
    /* 表示当内存超限时采用LRU算法清除内存中的对象 */
    unsingned lru:REDIS_LRU_BITS;
    /* refcount表示对象的引用计数 */
    int refcount;
    /* ptr指针指向真正的存储结构 */
    void *ptr;
} robj;
```

##### 编码方式

| 编码 | 编码方式                         | 说明                           | 数据类型               |
| ---- | -------------------------------- | ------------------------------ | ---------------------- |
| 0    | define REDIS_ENCODING_RAW        | Raw representation             | 【STRING】             |
| 1    | define REDIS_ENCODING_INT        | Encoded as integer             | 【STRING】             |
| 2    | define REDIS_ENCODING_HT         | Encoded as hash table          | 【HASH & SET】         |
| 3    | define REDIS_ENCODING_ZIPMAP     | Encoded as zipmap              | 【】                   |
| 4    | define REDIS_ENCODING_LINKEDLIST | Encoded as regular linked list | 【LIST】               |
| 5    | define REDIS_ENCODING_ZIPLIST    | Encoded as ziplist             | 【HASH & LIST & ZSET】 |
| 6    | define REDIS_ENCODING_INTSET     | Encoded as intset              | 【SET】                |
| 7    | define REDIS_ENCODING_SKIPLIST   | Encoded as skiplist            | 【ZSET】               |

​         