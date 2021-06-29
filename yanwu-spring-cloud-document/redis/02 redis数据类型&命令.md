#### string：`K-[V]`

>   值可以是字符串、数字（整数和浮点）、二进制【其中，整数的取值范围与系统的长整型取值一致[`32`位系统是`32`位、`64`位系统是`64`位]；浮点数的精度为`double`】

| 命令                         | 说明                                                 | 时间复杂度 |
| ---------------------------- | ---------------------------------------------------- | ---------- |
| `GET K`                      | 获取`K`对应的`V`                                     | $O(1)$     |
| `MGET K1 K2 K3 ...`          | 批量获取`K`对应的`V`，原子性操作                     | $O(N)$     |
| `GETSET K V`                 | 设置`K-V`并返回旧的`V`                               | $O(1)$     |
| `GETRANGE K start end`       | 获取`K`对应`V`字符串的指定下标所有的值               | $O(1)$     |
| `SET K V`                    | 设置`K-V`，`K`存在修改`K`对应的`V`，K不存在新增`K-V` | $O(1)$     |
| `SETNX K V`                  | `K`不存在时才设置                                    | $O(1)$     |
| `SETRANGE K index V`         | 设置`K`对应`V`字符串的执行下标所对应的值             | $O(1)$     |
| `APPEND K V`                 | 将`V`追加到`K`对应的旧`V`后                          | $O(1)$     |
| `MSET K1 V1 K2 V2 K3 V3 ...` | 批量设置`K-V`                                        | $O(1)$     |
| `DEL K`                      | 删除`K-V`                                            | $O(1)$     |
| `INCR K`                     | `K`自增`1`，如果`K`不存在，自增后`GET K = 1`         | $O(1)$     |
| `DECR K`                     | `K`自减`1`，如果`K`不存在，自减后`GET K = -1`        | $O(1)$     |
| `INCRBY K I`                 | `K`自增`I`，如果`K`不存在，自增后`GET K = I`         | $O(1)$     |
| `DECRBY K I`                 | `K`自减`I`，如果`K`不存在，自减后`GET K = -I`        | $O(1)$     |
| `INCRBYFLOAT K F`            | `K`自增`F`，如果`K`不存在，自增后`GET K = F`         | $O(1)$     |
| `STRLEN`                     | 返回字符串长度（`UTF8`下中文占`3`个字符）            | $O(1)$     |



#### hash：`K-[F-V]`

>   在使用`hash`的`HGETALL`指令时应当谨慎小心，因为`redis`是单线程的，此命令可能需要比较久的处理时间，可能会导致`redis`阻塞

| 命令                            | 说明                                 | 时间复杂度 |
| ------------------------------- | ------------------------------------ | ---------- |
| `HGET K F`                      | 获取`K`下的F对应的`V`                | $O(1)$     |
| `HMGET K F1 F2 F3 ...`          | 批量获取`K`下`F1、F2、F3`的`V`       | $O(N)$     |
| `HGETALL K`                     | 批量获取`K`下所有的`F-V`             | $O(N)$     |
| `HKEYS K`                       | 返回`K`对应的所有的F                 | $O(N)$     |
| `HVALS K`                       | 返回`K`对应的所有`F`的`V`            | $O(N)$     |
| `HSET K F V`                    | 设置`K`下对应的`F-V`                 | $O(1)$     |
| `HMSET K F1 V1 F2 V2 F3 V3 ...` | 批量设置`K`下的`F1-V1、F2-V2、F3-V3` | $O(1)$     |
| `HSETNX K F V`                  | 设置`K-F-V`，当`F`已存在时则设置失败 | $O(1)$     |
| `HEXISTE K F`                   | 判断`K`下是否有`F`                   | $O(1)$     |
| `HLEN K`                        | 获取`K`下`F`的数量                   | $O(1)$     |
| `HINCRBY K F I`                 | `K`下的`F`对应的V自增`I`             | $O(1)$     |
| `HINCRBYFLOAT K F F`            | `K`下的`F`对应的V自增`F`             | $O(1)$     |



#### list：`K-[V]`

>   有序，不唯一，链表实现，左右两边都可以执行插入和弹出操作，容量是$2^{32}-1$个元素

```
1、LPUSH - LPOP >> STACK
2、LPUSH - RPOP >> QUEUE
3、LPUSH - LTRIM >> CAPPED COLLECTION
4、LPUSH - BRPOP >> MESSAGE QUEUE
```

| 命令                            | 说明                                                         | 时间复杂度 |
| ------------------------------- | ------------------------------------------------------------ | ---------- |
| `RPUSH K V1 V2 V3 ...`          | 从列表**右端**依次插入`V1、V2、V3`                           | $O(N)$     |
| `LPUSH K V1 V2 V3 ...`          | 从列表**左端**依次插入`V1、V2、V3`                           | $O(N)$     |
| `LINSERT K [before|after] V NV` | 在`K`指定的`V [ 前 | 后 ] 插入 NV`                           | $O(N)$     |
| `LPOP K`                        | 从列表的**左端**弹出一个`V`                                  | $O(1)$     |
| `BLOPO K timeoout`              | `LPOP`阻塞版本，`timeout`为阻塞时间，`timeout`为`0`时永远阻塞 | $O(1)$     |
| `RPOP K`                        | 从列表的**右端**弹出一个`V`                                  | $O(1)$     |
| `BRPOP K`                       | `RPOP`阻塞版本，`timeout`为阻塞时间，`timeout`为`0`时永远阻塞 | $O(1)$     |
| `LREM K count V`                | 1）$count>0$，**从左到右**删除最多`count`个`value`相等的项<br />2）$count<0$，**从右到左**删除最多`count`个`value`相等的项<br />3）$count=0$，删除所有`value`相等的项 | $O(N)$     |
| `LTRIM K start end`             | 按照索引范围修建列表                                         | $O(N)$     |
| `LRANGE K start end`            | 获取列表指定索引范围内所有的`V`                              | $O(N)$     |
| `LINDEX K index`                | 获取列表指定索引的`V`                                        | $O(1)$     |
| `LLEN K`                        | 获取列表的长度                                               | $O(1)$     |
| `LSET K index V`                | 设置列表指定索引的值为`V`                                    | $0(N)$     |



#### set：`K-[V]`

>   无序，唯一，哈希表实现

| 命令                                      | 说明                                                      | 时间复杂度 |
| ----------------------------------------- | --------------------------------------------------------- | ---------- |
| `SADD K V`                                | 向集合`K`中添加`V`，如果`V`已经存在，则添加失败           | $O(1)$     |
| `SREM K V`                                | 将集合`K`中的`V`移除                                      | $O(1)$     |
| `SCARD K`                                 | 计算集合的大小                                            | $O(1)$     |
| `SISMEMBER K V`                           | 判断集合`K`中是否存在`V`                                  | $O(1)$     |
| `SRANDMEMBER K count`                     | 从集合`K`中随机挑选`count`个`V`，不会改变集合             | $O(1)$     |
| `SPOP K`                                  | 从集合`K`中随机弹出一个`V`，会改变集合                    | $O(1)$     |
| `SMEMBERS K`                              | 获取集合`K`中所有的`V`，大数据量场景可能会导致`redis`阻塞 | $O(1)$     |
| `SDIFF K1 K2`                             | 求`K1`和`K2`两个集合的差集                                | $O(1)$     |
| `SINTER K1 K2`                            | 求`K1`和`K2`两个集合的交集                                | $O(1)$     |
| `SUNION K1 K2`                            | 求`K1`和`K2`两个集合的并集                                | $O(1)$     |
| `[SDIFF|SINTER|SUNION] K1 K2 store destK` | 将`K1`和`K2`的`[差集、交集、并集]`的结果保存到`destK`中   | $O(1)$     |



#### zset：`K-[V (S)]`

>   有序，唯一，每个元素都会关联一个`double`类型的分数，通过分数来为集合中的元素进行从小到大的排序，**分数可以重复**

| 命令                                     | 说明                                                        | 时间复杂度  |
| ---------------------------------------- | ----------------------------------------------------------- | ----------- |
| `ZADD K S V`                             | 向集合`K`中添加分数为`S`的`V`                               | $O(logN)$   |
| `ZREM K V1 V2 V3 ...`                    | 删除集合`K`中的`V1、V2、V3`                                 | $O(1)$      |
| `ZSCORE K V`                             | 返回集合K中`V`的分数                                        | $O(1)$      |
| `ZINCRBY K increScore V`                 | 将集合`K`中的`V`分数自增或自减`increScore`                  | $O(1)$      |
| `ZCARD K`                                | 返回集合`K`中的`V`总个数                                    | $O(1)$      |
| `ZRANK K menber`                         | 返回集合`K`中元素的排名                                     | $O(1)$      |
| `ZRANGE K start end`                     | 返回集合`K`中指定索引范围内的升序元素                       | $O(logN+M)$ |
| `ZRANGEBYSCORE K minS maxS`              | 返回集合`K`中指定分数范围内的升序元素                       | $O(logN+M)$ |
| `ZCOUNT K minS maxS`                     | 返回集合`K`中指定分数范围内的元素的个数                     | $O(logN+M)$ |
| `ZREMRANGEBYRANK K start end`            | 删除集合`K`中指定排名内的元素                               | $O(logN+M)$ |
| `ZREMRANGEBYSCORE K minS maxS`           | 删除集合`K`中指定分数内的元素                               | $O(logN+M)$ |
| `ZINTERSTORE destZ numkeys K1 K2 K3 ...` | 计算集合`K1 K2 K3`的交集，将结果存储在新的`destZ`有序集合中 |             |
| `ZUNIONSTORE dtstZ numneys K1 K2 K3 ...` | 计算集合`K1 K2 K3`的并集，将结果存储在新的`destZ`有序集合中 |             |

  

#### geo：`K-[M (LON LAT)]`

>   地理信息定位，存储经纬度，计算两地距离，范围计算等

| 命令                                                         | 说明                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| `GEOADD K LON LAT member [LON1 LON2 member ...]`             | 向`K`中添加地理位置，命名为`member`<br />`LON`（经度）：取值为$[-180 ~ 180]$<br />`LAT`（维度）：取值为$[-85.05112878 ~ 85.05112878]$ |
| GEOPOS K menber [member1 ...]                                | 获取`K`中的`menber`地理位置信息                              |
| `GEODIST K member1 member2 [unit]`                           | 获取两个地理坐标之间的距离，默认为`munit：[m（米）、km（千米）、mi（英里）、ft（英尺）]` |
| `GEORADIUS K LON LAT radius unit [withcoord] [withdist] [withhash] [asc\|desc] [count n]` | 以给定的经纬度为中心，返回`K`包含的位置元素当中与中心的距离不超错给定最大距离的所有位置元素<br />`unit`：[`m`（米）、`km`（千米）、`mi`（英里）、`ft`（英尺）]<br />`withcoord`：将位置元素的经纬度一起返回<br />`withdist`：在返回元素的同时，将位置与中心位置之间的距离也一起返回，距离的单位和给定的范围单位一致<br />`asc\|desc`：根据中心位置，按照[从近到远\|从远到近]的方式返回位置元素，默认不排序<br />`count n`：获取n个匹配的元素 |
| `GEORADIUSBYMEMBER K menber radius unit [withcoord] [withdist] [withhash] [asc\|desc] [count n]` | 同上，区别是`GEORADIUS`以给定的经纬度为中心，`GEORADIUSBYMEMBER`以给定的`member`为中心 |
| `GEOHASH K member [member1 ...]`                             | 将二维经纬度转化为一维字符串，字符串越长表明位置越精确，两个字符串越相似表示距离越近 |
| `ZREM K member`                                              | `GEO`没有提供删除成员的命令，但是因为`GEO`的底层实现是`zset`，所以可以借用`zrem`命令实现对地理位置信息的删除 |