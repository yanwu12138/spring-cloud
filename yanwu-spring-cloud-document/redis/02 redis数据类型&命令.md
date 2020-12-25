#### string：K-V

>   值可以是字符串、数字（整数和浮点）、二进制【其中，整数的取值范围与系统的长整型取值一致[32位系统是32位、64位系统是64位]；浮点数的精度为double】

| 命令                       | 说明                                       | 时间复杂度 |
| -------------------------- | ------------------------------------------ | ---------- |
| GET K                      | 获取K对应的V                               | O(1)       |
| MGET K1 K2 K3 ...          | 批量获取K对应的V，原子性操作               | O(N)       |
| GETSET K V                 | 设置K-V并返回旧的V                         | O(1)       |
| GETRANGE K start end       | 获取K对应V字符串的指定下标所有的值         | O(1)       |
| SET K V                    | 设置K-V，K存在修改K对应的V，K不存在新增K-V | O(1)       |
| SETNX K V                  | K不存在时才设置                            | O(1)       |
| SETRANGE K index V         | 设置K对应V字符串的执行下标所对应的值       | O(1)       |
| APPEND K V                 | 将V追加到K对应的旧V后                      | O(1)       |
| MSET K1 V1 K2 V2 K3 V3 ... | 批量设置K-V                                | O(1)       |
| DEL K                      | 删除K-V                                    | O(1)       |
| INCR K                     | K自增1，如果K不存在，自增后GET K = 1       | O(1)       |
| DECR K                     | K自减1，如果K不存在，自减后GET K = -1      | O(1)       |
| INCRBY K I                 | K自增I，如果K不存在，自增后GET K = I       | O(1)       |
| DECRBY K I                 | K自减I，如果K不存在，自减后GET K = -I      | O(1)       |
| INCRBYFLOAT K F            | K自增F，如果K不存在，自增后GET K = F       | O(1)       |
| STRLEN                     | 返回字符串长度（UTF8下中文占3个字符）      | O(1)       |



#### hash：K-[F-V]

>   在使用hash的HGETALL指令时应当谨慎小心，因为redis是单线程的，此命令可能需要比较久的处理时间，可能会导致redis阻塞

| 命令                          | 说明                             | 时间复杂度 |
| ----------------------------- | -------------------------------- | ---------- |
| HGET K F                      | 获取K下的F对应的V                | O(1)       |
| HMGET K F1 F2 F3 ...          | 批量获取K下F1、F2、F3的V         | O(N)       |
| HGETALL K                     | 批量获取K下所有的F-V             | O(N)       |
| HKEYS K                       | 返回K对应的所有的F               | O(N)       |
| HVALS K                       | 返回K对应的所有F的V              | O(N)       |
| HSET K F V                    | 设置K下对应的F-V                 | O(1)       |
| HMSET K F1 V1 F2 V2 F3 V3 ... | 批量设置K下的F1-V1、F2-V2、F3-V3 | O(1)       |
| HSETNX K F V                  | 设置K-F-V，当F已存在时则设置失败 | O(1)       |
| HEXISTE K F                   | 判断K下是否有F                   | O(1)       |
| HLEN K                        | 获取K下F的数量                   | O(1)       |
| HINCRBY K F I                 | K下的F对应的V自增I               | O(1)       |
| HINCRBYFLOAT K F F            | K下的F对应的V自增F               | O(1)       |



#### list：K-[V]

>   有序，不唯一，链表实现，左右两边都可以执行插入和弹出操作，容量是2的32次方减1个元素

```
1、LPUSH - LPOP >> STACK
2、LPUSH - RPOP >> QUEUE
3、LPUSH - LTRIM >> CAPPED COLLECTION
4、LPUSH - BRPOP >> MESSAGE QUEUE
```

| 命令                           | 说明                                                         | 时间复杂度 |
| ------------------------------ | ------------------------------------------------------------ | ---------- |
| RPUSH K V1 V2 V3 ...           | 从列表右端依次插入V1、V2、V3                                 | O(N)       |
| LPUSH K V1 V2 V3 ...           | 从列表左端依次插入V1、V2、V3                                 | O(N)       |
| LINSERT K [before\|after] V NV | 在K指定的V[前\|后]插入NV                                     | O(N)       |
| LPOP K                         | 从列表的左端弹出一个V                                        | O(1)       |
| BLOPO K timeoout               | LPOP阻塞版本，timeout为阻塞时间，timeout为0时永远阻塞        | O(1)       |
| RPOP K                         | 从列表的右端弹出一个V                                        | O(1)       |
| BRPOP K                        | RPOP阻塞版本，timeout为阻塞时间，timeout为0时永远阻塞        | O(1)       |
| LREM K count V                 | 1）count>0，从左到右删除最多count个value相等的项2）count<0，从右到左删除最多count个value相等的项3）count=0，删除所有value相等的项 | O(N)       |
| LTRIM K start end              | 按照索引范围修建列表                                         | O(N)       |
| LRANGE K start end             | 获取列表指定索引范围内所有的V                                | O(N)       |
| LINDEX K index                 | 获取列表指定索引的V                                          | O(1)       |
| LLEN K                         | 获取列表的长度                                               | O(1)       |
| LSET K index V                 | 设置列表指定索引的值为V                                      | 0(N)       |



#### set：K-[V]

>   无序，唯一，哈希表实现

| 命令                                      | 说明                                                | 时间复杂度 |
| ----------------------------------------- | --------------------------------------------------- | ---------- |
| SADD K V                                  | 向集合K中添加V，如果V已经存在，则添加失败           | O(1)       |
| SREM K V                                  | 将集合K中的V移除                                    | O(1)       |
| SCARD K                                   | 计算集合的大小                                      | O(1)       |
| SISMEMBER K V                             | 判断集合K中是否存在V                                | O(1)       |
| SRANDMEMBER K count                       | 从集合K中随机挑选count个V，不会改变集合             | O(1)       |
| SPOP K                                    | 从集合K中随机弹出一个V，会改变集合                  | O(1)       |
| SMEMBERS K                                | 获取集合K中所有的V，大数据量场景可能会导致redis阻塞 | O(1)       |
| SDIFF K1 K2                               | 求K1和K2两个集合的差集                              | O(1)       |
| SINTER K1 K2                              | 求K1和K2两个集合的交集                              | O(1)       |
| SUNION K1 K2                              | 求K1和K2两个集合的并集                              | O(1)       |
| [SDIFF\|SINTER\|SUNION] K1 K2 store destK | 将K1和K2的[差集、交集、并集]的结果保存到destK中     | O(1)       |



#### zset：K-[V]

>   有序，唯一，每个元素都会关联一个double类型的分数，通过分数来为集合中的元素进行从小到大的排序，**分数可以重复**

| 命令                                   | 说明                                                    | 时间复杂度 |
| -------------------------------------- | ------------------------------------------------------- | ---------- |
| ZADD K S V                             | 向集合K中添加分数为S的V                                 | O(logN)    |
| ZREM K V1 V2 V3 ...                    | 删除集合K中的V1、V2、V3                                 | O(1)       |
| ZSCORE K V                             | 返回集合K中V的分数                                      | O(1)       |
| ZINCRBY K increScore V                 | 将集合K中的V分数自增或自减increScore                    | O(1)       |
| ZCARD K                                | 返回集合K中的V总个数                                    | O(1)       |
| ZRANK K menber                         | 返回集合K中元素的排名                                   | O(1)       |
| ZRANGE K start end                     | 返回集合K中指定索引范围内的升序元素                     | O(logN+M)  |
| ZRANGEBYSCORE K minS maxS              | 返回集合K中指定分数范围内的升序元素                     | O(logN+M)  |
| ZCOUNT K minS maxS                     | 返回集合K中指定分数范围内的元素的个数                   | O(logN+M)  |
| ZREMRANGEBYRANK K start end            | 删除集合K中指定排名内的元素                             | O(logN+M)  |
| ZREMRANGEBYSCORE K minS maxS           | 删除集合K中指定分数内的元素                             | O(logN+M)  |
| ZINTERSTORE destZ numkeys K1 K2 K3 ... | 计算集合K1 K2 K3的交集，将结果存储在新的destZ有序集合中 |            |
| ZUNIONSTORE dtstZ numneys K1 K2 K3 ... | 计算集合K1 K2 K3的并集，将结果存储在新的destZ有序集合中 |            |

  

#### geo：K-[M LON LAT]

>   地理信息定位，存储经纬度，计算两地距离，范围计算等

| 命令                                                         | 说明                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| GEOADD K LON LAT member [LON1 LON2 member ...]               | 向K中添加地理位置，命名为memberLON（经度）：取值为[-180 ~ 180]LAT（维度）：取值为[-85.05112878 ~ 85.05112878] |
| GEOPOS K menber [member1 ...]                                | 获取K中的menber地理位置信息                                  |
| GEODIST K member1 member2 [unit]                             | 获取两个地理坐标之间的距离，默认为munit：[m（米）、km（千米）、mi（英里）、ft（英尺）] |
| GEORADIUS K LON LAT radius unit [withcoord] [withdist] [withhash] [asc\|desc] [count n] | 以给定的经纬度为中心，返回K包含的位置元素当中与中心的距离不超错给定最大距离的所有位置元素unit：[m（米）、km（千米）、mi（英里）、ft（英尺）]withcoord：将位置元素的经纬度一起返回withdist：在返回元素的同时，将位置与中心位置之间的距离也一起返回，距离的单位和给定的范围单位一致asc\|desc：根据中心位置，按照[从近到远\|从远到近]的方式返回位置元素，默认不排序count n：获取n个匹配的元素 |
| GEORADIUSBYMEMBER K menber radius unit [withcoord] [withdist] [withhash] [asc\|desc] [count n] | 同上，区别是GEORADIUS以给定的经纬度为中心，GEORADIUSBYMEMBER以给定的member为中心 |
| GEOHASH K member [member1 ...]                               | 将二维经纬度转化为一维字符串，字符串越长表明位置越精确，两个字符串越相似表示距离越近 |
| ZREM K member                                                | GEO没有提供删除成员的命令，但是因为GEO的底层实现是zset，所以可以借用zrem命令实现对地理位置信息的删除 |