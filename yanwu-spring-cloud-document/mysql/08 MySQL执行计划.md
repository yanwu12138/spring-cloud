### 执行计划：[EXPLAIN](https://dev.mysql.com/doc/refman/5.7/en/explain-output.html#explain)

#### EXPLAIN是什么

它是 `MySQL` 的一个命令，用来查看 `SQL` 的执行计划（`SQL`如何执行），根据其输出结果，我们能够知道以下信息：表的读取顺序，数据读取类型，哪些索引可以使用，哪些索引实际使用了，表之间的连接类型，每张表有多少行被优化器查询等信息，根据这些信息，我们可以找出 `SQL` 慢的原因，并做针对性的优化。

`MySQL 5.6` 之前的版本，`EXPLAIN` 只能用于查看 `SELECT` 的执行计划，而从 `MySQL 5.6` 开始，可以查看 `SELECT` 、 `DELETE` 、 `INSERT` 、 `REPLACE` 和 `UPDATE` 的执行计划。

`EXPLAIN` 使用方式非常简单，简单的你都不敢相信，就是在我们常写的 `SELECT` 、 `DELETE` 、 `INSERT` 、 `REPLACE` 和 `UPDATE` 语句之前加上 `EXPLAIN` 即可：

```sql
EXPLAIN SELECT * FROM mysql.`user`;
EXPLAIN DELETE FROM t_user WHERE user_name = '123';
```

虽然 EXPLAIN 使用起来非常简单，但它的输出结果中信息量非常大。



#### 环境和数据准备

`MySQL` 版本是 **5.7**.**2** ，存储引擎是 `InnoDB `。准备两张表：用户表 `tbl_user` 和用户登录记录表 `tbl_user_login_log` ，并初始化部分部分数据：

```sql
-- 表创建
DROP TABLE IF EXISTS tbl_user;
CREATE TABLE tbl_user (
  id INT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  user_name VARCHAR(50) NOT NULL COMMENT '用户名',
  sex TINYINT(1) NOT NULL COMMENT '性别, 1:男，0:女',
  create_time datetime NOT NULL COMMENT '创建时间',
  update_time datetime NOT NULL COMMENT '更新时间',
    remark VARCHAR(255) NOT NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (id)
) COMMENT='用户表';

DROP TABLE IF EXISTS tbl_user_login_log;
CREATE TABLE tbl_user_login_log (
  id INT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  user_name VARCHAR(50) NOT NULL COMMENT '用户名',
  ip VARCHAR(15) NOT NULL COMMENT '登录IP',
  client TINYINT(1) NOT NULL COMMENT '登录端, 1:android, 2:ios, 3:PC, 4:H5',
  create_time datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (id)
) COMMENT='登录日志';

-- 数据初始化
INSERT INTO tbl_user(user_name,sex,create_time,update_time,remark) VALUES
('何天香',1,NOW(), NOW(),'朗眉星目，一表人材'),
('薛沉香',0,NOW(), NOW(),'天星楼的总楼主薛摇红的女儿，也是天星楼的少总楼主，体态丰盈，乌发飘逸，指若春葱，袖臂如玉，风姿卓然，高贵典雅，人称“天星绝香”的武林第一大美女'),
('慕容兰娟',0,NOW(), NOW(),'武林东南西北四大世家之北世家慕容长明的独生女儿，生得玲珑剔透，粉雕玉琢，脾气却是刚烈无比，又喜着火红，所以人送绰号“火凤凰”，是除天星楼薛沉香之外的武林第二大美女'),
('苌婷',0,NOW(), NOW(),'当今皇上最宠爱的侄女，北王府的郡主，腰肢纤细，遍体罗绮，眉若墨画，唇点樱红；虽无沉香之雅重，兰娟之热烈，却别现出一种空灵'),
('柳含姻',0,NOW(), NOW(),'武林四绝之一的添愁仙子董婉婉的徒弟，体态窈窕，姿容秀丽，真个是秋水为神玉为骨，芙蓉如面柳如腰，眉若墨画，唇若点樱，不弱西子半分，更胜玉环一筹; 摇红楼、听雨轩，琵琶一曲值千金!'),
('李凝雪',0,NOW(), NOW(),'李相国的女儿，神采奕奕，英姿飒爽，爱憎分明'),
('周遗梦',0,NOW(), NOW(),'音神传人，湘妃竹琴的拥有者，云髻高盘，穿了一身黑色蝉翼纱衫，愈觉得冰肌玉骨，粉面樱唇，格外娇艳动人'),
('叶留痕',0,NOW(), NOW(),'圣域圣女，肤白如雪，白衣飘飘，宛如仙女一般，微笑中带着说不出的柔和之美'),
('郭疏影',0,NOW(), NOW(),'扬灰右使的徒弟，秀发细眉，玉肌丰滑，娇润脱俗'),
('钟钧天',0,NOW(), NOW(),'天界，玄天九部 - 钧天部的部主，超凡脱俗，仙气逼人'),
('王雁云',0,NOW(), NOW(),'尘缘山庄二小姐，刁蛮任性'),
('许侍霜',0,NOW(), NOW(),'药王谷谷主女儿，医术高明'),
('冯黯凝',0,NOW(), NOW(),'桃花门门主，娇艳如火，千娇百媚');

INSERT INTO tbl_user_login_log(user_name, ip, client, create_time) VALUES
('薛沉香', '10.53.56.78',2, '2019-10-12 12:23:45'),
('苌婷', '10.53.56.78',2, '2019-10-12 22:23:45'),
('慕容兰娟', '10.53.56.12',1, '2018-08-12 22:23:45'),
('何天香', '10.53.56.12',1, '2019-10-19 10:23:45'),
('柳含姻', '198.11.132.198',2, '2018-05-12 22:23:45'),
('冯黯凝', '198.11.132.198',2, '2018-11-11 22:23:45'),
('周遗梦', '198.11.132.198',2, '2019-06-18 22:23:45'),
('郭疏影', '220.181.38.148',3, '2019-10-21 09:45:56'),
('薛沉香', '220.181.38.148',3, '2019-10-26 22:23:45'),
('苌婷', '104.69.160.60',4, '2019-10-12 10:23:45'),
('王雁云', '104.69.160.61',4, '2019-10-16 20:23:45'),
('李凝雪', '104.69.160.62',4, '2019-10-17 20:23:45'),
('许侍霜', '104.69.160.63',4, '2019-10-18 20:23:45'),
('叶留痕', '104.69.160.64',4, '2019-10-19 20:23:45'),
('王雁云', '104.69.160.65',4, '2019-10-20 20:23:45'),
('叶留痕', '104.69.160.66',4, '2019-10-21 20:23:45');
```



#### EXPLAIN输出

![image-20211027103643091](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/10/2021102710364343.png)

##### id

>   输出的是整数，用来标识整个 `SQL` 的执行顺序。`id` 如果相同，从上往下依次执行；`id`不同，`id` 值越大，执行优先级越高，越先被执行；如果行引用其他行的并集结果，则该值可以为`NULL`
>
>   ![image-20211027104103232](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/10/202110271041033.png)

##### select_type

>   查询的类型，表明是普通查询、联合查询还是子查询；[官方](https://dev.mysql.com/doc/refman/5.7/en/explain-output.html#explain_select_type)说明如下：
>
>   ![image-20211027104550085](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/10/2021102710455050.png)

-   `SIMPLE`：简单的 `SELECT` 查询，没有 `UNION` 或者子查询，包括单表查询或者多表 `JOIN` 查询

    >   ![image-20211027152746494](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/10/2021102715274646.png)

-   `PRIMARY`：最外层的 `SELECT` 查询，常见于子查询或 `UNION` 查询 ，最外层的查询被标识为 `PRIMARY`

    >![image-20211027152940843](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/10/2021102715294040.png)

-   `UNION`：`UNION` 操作的第二个或之后的 `SELECT`，不依赖于外部查询的结果集（__外部查询指的就是 `PRIMARY` 对应的 `SELECT`__）

    >   ![image-20211027153105722](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/10/202110271531055.png)

-   `DEPENDENT UNION`：`UNION` 操作的第二个或之后的 `SELECT`，依赖于外部查询的结果集

    >![image-20211027153927884](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/10/2021102715392727.png)

-   `UNION RESULT`：`UNION` 的结果（如果是 `UNION ALL` 则无此结果）

    >![image-20211027154056806](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/10/2021102715405656.png)

-   `SUBQUERY`：子查询中的第一个 `SELECT` 查询，不依赖于外部查询的结果集

    >![image-20211027154144643](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/10/2021102715414444.png)

-   `DEPENDENT SUBQUERY`：子查询中的第一个 `SELECT` 查询，依赖于外部查询的结果集

    >   ![](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/10/2021102715294040.png)

-   `DERIVED`：派生表（临时表），常见于 `FROM` 子句中有子查询的情况

    >![image-20211027154518092](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/10/2021102715451818.png)
    >
    >
    >__注意：__`MySQL5.7` 中对 `Derived table` 做了一个新特性，该特性允许将符合条件的 `Derived table` 中的子表与父查询的表合并进行直接 `JOIN`，从而简化了执行计划，同时也提高了执行效率；默认情况下，`MySQL5.7` 中这个特性是开启的，所以在测试的时候需要先关闭改特性。

-   `MATERIALIZED`：被物化的子查询，`MySQL5.6` 引入的一种新的 `select_type`，主要是优化 `FROM` 或 `IN` 子句中的子查询，更多详情请查看：[Optimizing Subqueries with Materialization](https://dev.mysql.com/doc/refman/5.7/en/subquery-materialization.html)

    >![image-20211027154744227](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/10/2021102715474444.png)

-   `UNCACHEABLE SUBQUERY`：对于外层的主表，子查询不可被缓存，每次都需要计算

    >![image-20211027154922795](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/10/2021102715492222.png)

-   `UNCACHEABLE UNION`：类似于 `UNCACHEABLE SUBQUERY`，只是出现在 `UNION` 操作中

    >![image-20211027154953653](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/10/2021102715495353.png)

##### table

>   显示了对应行正在访问哪个表(__有别名就显示别名__)，还会有 `<union2,3>` 、 `<subquery2>` 、` <derived2>` （这里的 `2,3`、`2`、`2` 指的是 `id` 列的值）类似的值

##### partitions

>   查询进行匹配的分区，对于非分区表，该值为 `NULL`。大多数情况下用不到分区，所以这一列我们无需关注

##### type

>   关联类型或者访问类型，它指明了 MySQL 决定如何查找表中符合条件的行，这是我们判断查询是否高效的重要依据，完整介绍请看：[explain-join-types](https://dev.mysql.com/doc/refman/5.7/en/explain-output.html#explain-join-types)

`EXPLAIN` 输出的类型列描述了表的连接方式。在 `JSON` 格式的输出中，这些是作为 `access_type` 属性的值找到的。下面的列表描述了连接类型，从最好的类型到最差的类型：

-   `system`：该表只有一行（等于系统表），是 `const` 类型的特例

-   `const`：确定只有一行匹配的时候，`mysql` 优化器会在查询前读取它并且只读取一次，速度非常快。用于 `primary key` 或 `unique` 索引中有常亮值比较的情形

    >   ![image-20211027155504291](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/10/202110271555044.png)

-   `eq_ref`：对于每个来自于前面的表的行，从该表最多只返回一条符合条件的记录。当连接使用的索引是 `PRIMARY KEY` 或 `UNIQUE NOT NULL` 索引时使用，非常高效

    >![image-20211027155909332](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/10/202110271559099.png)

-   `ref`：索引访问，也称索引查找，它返回所有匹配某个单个值的行。此类型通常出现在多表的 `JOIN` 查询, 针对于非 `UNIQUE` 或非 `PRIMARY KEY`, 或者是使用了最左前缀规则索引的查询，换句话说，如果 `JOIN` 不能基于关键字选择单个行的话，则使用 `ref`

    >![image-20211027160024596](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/10/2021102716002424.png)

-   `fulltext`：当使用全文索引时会用到，这种索引一般用不到，会用专门的搜索服务(`solr`、`elasticsearch`等)来替代

-   `ref_or_null`：类似`ref`，但是添加了可以专门搜索 `NULL` 的行（__这个是有前提条件的，前提为 `weapon` 列有索引，且 `weapon` 列存在 `NULL` __）

    >![image-20211027164022087](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/10/2021102716402222.png)

-   `index_merge`：该访问类型使用了索引合并优化方法，这个同样也是有条件的， `id` 列和 `weapon` 列都有单列索引。如果出现 `index_merge`，并且这类 `SQL` 后期使用较频繁，可以考虑把单列索引换为组合索引，这样效率更高

    >![image-20211027164120285](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/10/2021102716412020.png)

-   `unique_subquery`：类似于两表连接中被驱动表的 `eq_ref` 访问方式，`unique_subquery` 是针对在一些包含 IN 子查询的查询语句中，如果查询优化器决定将 `IN` 子查询转换为 `EXISTS` 子查询，而且子查询可以使用到主键或者唯一索引进行等值匹配时，则会使用 `unique_subquery`

    >![image-20211027164206020](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/10/202110271642066.png)

-   `index_subquery`：`index_subquery` 与 `unique_subquery` 类似，只不过访问子查询中的表时使用的是普通的索引

    >![image-20211027164250509](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/10/2021102716425050.png)

-   `range`：使用索引来检索给定范围的行，当使用 `=`、`<>`、`>`、`>=`、`<`、`<=`、`IS NULL`、`<=>`、`BETWEEN` 或者 `IN` 操作符，用常量比较关键字列时，则会使用 `rang`（前提是必须基于索引，也就是 `id` 上必须有索引）

    >![image-20211027164519531](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/10/2021102716451919.png)

-   `index`：当我们可以使用索引覆盖，但需要扫描全部的索引记录时，则会使用 `index`；进行统计时非常常见

    >![image-20211027164546156](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/10/2021102716454646.png)

-   `ALL`：全表扫描

    >![image-20211027164609419](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/10/202110271646099.png)

##### possible_keys

>   展示在这个 `SQL` 中，可能用到的索引有哪些，但不一定在查询时使用。若为空则表示没有可以使用的索引，此时可以通过检查 `WHERE` 语句看是否可以引用某些列或者新建索引来提高性能

##### key

>   展示这个 `SQL` 实际使用的索引，如果没有选择索引，则此列为 `null`，要想强制 `MySQL` 使用或忽视 `possible_keys` 列中的索引，在查询中使用 `FORCE INDEX`、`USE INDEX` 或者 `I GNORE INDEX`

##### key_len

>   展示 `MySQL` 决定使用的索引长度（字节数）。如果 `key` 是 `NULL`，则长度为 `NULL`

##### ref

>   展示的是与索引列作等值匹配的东东是个啥，比如只是一个常数或者是某个列。它显示的列的名字（或 `const`），此列多数时候为 `Null`

##### rows

>   展示的是 `mysql` 解析器认为执行此 `SQL` 时预此数值为一个预估值，不是具体值，通常比实际值小

##### filtered

>   展示的是被条件过滤的行数所占需要读到的行（`rows` 的值）的比例（省略了百分号，显示的是百分号前的值）；我们平时关注的不多，有兴趣的可以去仔细琢磨下，你会有更多发现

#####  extra

>   表示不在其他列但也很重要的额外信息。取值有很多，我们挑一些比较常见的过一下

-   `using index`：表示 `SQL` 使用了使用覆盖索引，而不用回表去查询数据，性能非常不错

    >![image-20211027164701891](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/10/202110271647011.png)

-   `using where`：表示存储引擎搜到记录后进行了后过滤(`POST-FILTER`)，如果查询未能使用索引，`using where` 的作用只是提醒我们 `mysql` 要用 `where` 条件过滤结果集

    >![image-20211027164729100](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/10/2021102716472929.png)

-   `using temporary`：表示 `mysql` 需要使用临时表来存储结果集，常见于排序和分组查询

    >![image-20211027164754435](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/10/2021102716475454.png)

-   `using filesort`：表示 `mysql` 无法利用索引直接完成排序（排序的字段不是索引字段），此时会用到缓冲空间（内存或者磁盘）来进行排序；一般出现该值，则表示 `SQL` 要进行优化了，它对 `CPU` 的消耗是比较大的

    >![image-20211027164819036](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/10/2021102716481919.png)

-   `impossible where`：查询语句的 `WHERE` 子句永远为 `FALSE` 时将会提示该额外信息

    >![image-20211027164841883](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/10/2021102716484141.png)

