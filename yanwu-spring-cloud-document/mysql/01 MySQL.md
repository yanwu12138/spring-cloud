## MySQL基本架构图

![clipboard](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/20200628130918.png)

### 连接器
负责根客户端建立连接，获取权限、维持和管理连接
- 用户名密码验证
- 查询权限信息，分配对应的权限
- 可以使用 `show processlist` 查看现在的连接
- 如果长时间没有活动，会自动断开连接（通过 `wait_timeout` 控制，默认`8` 小时）

### 查询缓存（不建议使用且8.0后不提供）
当执行查询语句的时候，会先查询缓存中又没有结果，如果有则返回，执行后续操作，并将结果设入查询缓存
##### 问题：
- 查询缓存的缓存失效比较频繁，只要表更新，缓存就会清空
- 缓存对应的更新的数据命中率比较低

### 分析器
- 词法分析：把输入的字符串进行识别每个部分代表什么意思，例如：把 字符串`ID` 识别成 列`ID`
- 语法分析：判断`SQL`语句是否满足`MySQL`的语法，如果不符合会报错：`You have error in your SQL synta`

### 优化器
在具体执行`SQL`语句之前，要先经过优化器的处理，不同的执行方式对`SQL`语句的执行效率影响很大
- 当表中有多个索引的时候，决定用那个索引
- 当`SQL`语句需要做多表关联的时候，决定表的连接顺序

##### 优化器类型：
###### RBO（Rule Based Optimizer）：基于规则优化
> 简单说基于规则的优化就是当数据库执行一条`query`语句的时候必须遵循预先定义好的一系列规则（比如[oracle的15条规则](https://docs.oracle.com/cd/B10501_01/server.920/a96533/rbo.htm#38960)，排名越靠前的执行引擎认为效率越高）来确定执行过程，它不关心访问表的数据分布情况，仅仅凭借规则经验来确定，所以说是一种比较粗放的优化策略。

###### CBO（Cost Based Optimizer）：基于成本优化
> 基于代价的优化的产生就是为了解决上面RBO的弊端，让执行引擎依据预先存储到数据库中表的一些实时更新的统计信息来选择出最优代价最小的执行计划来执行`query`语句，`CBO`会根据统计信息来生成一组可能被使用到的执行计划，进而估算出每个计划的代价，从而选择出代价最小的交给执行器去执行，其中表的统计信息一般会有表大小，行数，单行长度，单列数据分布情况，索引情况等等。

###### 总结：
> 基于规则的优化器更像是一个经验丰富熟知各条路段的老司机，大部分情况可以根据自己的经验来判断走哪条路可以更快的到达目的地；而基于代价的优化更像手机里面的地图，它可以选择出许多不同的路径根据实时的路况信息综合考虑路程长度，交通状况来挑出最优的路径。

### 执行器
执行`SQL`语句返回结果

### 超键、候选键、主键、外键
- 超键：在关系中能唯一标识元组的属性集称为关系模式的超键。一个属性可以为作为一个超键，多个属性组合在一起也可以作为一个超键。超键包含候选键和主键
- 候选键：是最小超键，即没有冗余元素的超键
- 主键：数据库表中对储存数据对象予以唯一和完整标识的数据列或属性的组合。一个数据列只能有一个主键，且主键的取值不能缺失，即不能为空值
- 外键：在一个表中存在的另一个表的主键称此表的外键



### 连接
#### 内连接
##### 内连接
获取两个表中字段匹配关系的记录：
`select * from table_a as a inner join table_b as b on a.id = b.id`

#### 外连接（LEFT JOIN/RIGHT JOIN）
##### 左连接
获取左表所有记录，即使右表没有对应匹配的记录：
`select * from table_a as a left join table_b as b on a.id = b.id`

##### 右连接
与左连接相反，用于获取右表所有记录，即使左表没有对应匹配的记录：
`select * from table_a as a right join table_b as b on a.id = b.id`

#### 联合查询（UNION与UNION ALL）
就是把多个结果集集中在一起，UNION前的结果为基准，需要注意的是联合查询的列数要相等，相同的记录行会合并：`select * from table_a as a union select * from table_b as b`

#### 全连接（FULL JOIN）
`MySQL`不支持全连接，但是可以`LEFT JOIN` 和`UNION`和`RIGHT JOIN`联合使用：`select * from table_a as a left join table_b as b on a.id = b.id union select * from table_a as a right join table_b as b on a.id = b.id;`

### SQL执行顺序
```sql
SELECT DISTINCT <select_list>
FROM <left_table>
<join_type> JOIN <right_table> ON <join_condition>
WHERE <where_condition>
GROUP BY <group_by_list>
WITH {CUBE|ROLLUP}
HAVING <having_condition>
ORDER BY <order_by_condition>
LIMIT <limit_number>

-- SQL关键字的执行顺序
-- FROM：对FROM子句中的左表<left_table>和右表<right_table>执行笛卡儿积，产生虚拟表VT1;
-- ON：对虚拟表VT1进行ON筛选，只有那些符合<join_condition>的行才被插入虚拟表VT2;
-- JOIN：如果指定了OUTER JOIN(如LEFT OUTER JOIN、RIGHT OUTER JOIN)，那么保留表中未匹配的行作为外部行添加到虚拟表VT2，产生虚拟表VT3。如果FROM子句包含两个以上的表，则对上一个连接生成的结果表VT3和下一个表重复执行步骤1~步骤3，直到处理完所有的表;
-- WHERE：对虚拟表VT3应用WHERE过滤条件，只有符合<where_condition>的记录才会被插入虚拟表VT4;
-- GROUP BY：根据GROUP BY子句中的列，对VT4中的记录进行分组操作，产生VT5;
-- CUBE|ROLLUP：对VT5进行CUBE或ROLLUP操作，产生表VT6;
-- HAVING：对虚拟表VT6应用HAVING过滤器，只有符合<having_condition>的记录才会被插入到VT7;
-- SELECT：第二次执行SELECT操作，选择指定的列，插入到虚拟表VT8中;
-- DISTINCT：去除重复，产生虚拟表VT9;
-- ORDER BY：将虚拟表VT9中的记录按照<order_by_list>进行排序操作，产生虚拟表VT10;
-- LIMIT：取出指定街行的记录，产生虚拟表VT11，并返回给查询用户
```
### in & exists
###### in：把外表和内表作 `hash` 连接
###### exists：对外表作 `loop` 循环，每次 `loop` 循环再对内表进行查询
##### 使用场景
- 如果查询的两个表大小相当，那么用`in`和`exists`差别不大
- 如果两个表中一个较小，一个是大表，则子查询表大的用`exists`，子查询表小的用`in`
- 如果查询语句使用了`not in`，那么内外表都进行全表扫描，没有用到索引
- 而`not extsts`的子查询依然能用到表上的索引。所以无论那个表大，用`not exists`都比`not in`要快

***


### 数据库三范式

1. 每个列都不可以再拆分
2. 在第一范式的基础上，非主键列完全依赖于主键，而不能只依赖主键的一部分
3. 在第二范式的基础上，非之间只能依赖主键，不能依赖其它非主键

