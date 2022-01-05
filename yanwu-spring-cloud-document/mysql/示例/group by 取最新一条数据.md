### My SQL 实现`group by`后取各分组的最新一条数据

>    `group by`函数后取到的是分组中的第一条数据，但是我们有时候需要取出各分组的最新一条，该怎么实现呢？

#### 准备数据

[edge_online_log.sql](./file/edge_online_log.sql)

#### 实现方式

##### 先`ORDER BY`之后再分组

```sql
SELECT
	tmp.sn,
	tmp.`online`,
	tmp.date_created 
FROM
	( SELECT * FROM edge_online_log WHERE `online` = 1 ORDER BY date_created DESC LIMIT 10000 ) AS tmp 
GROUP BY
	tmp.sn 
ORDER BY
	tmp.date_created DESC;
```

>   ![image-20220105113930452](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2022/01/2022010511393030.png)
>
>   注：不加`LIMIT`可能会无效，由于`mysql`的版本问题。但是总觉得这种写法不太正经，因为如果数据量大于`Limit`的值后，结果就不准确了。

##### 利用`MAX()`函数

```sql
SELECT
	eol.sn,
	eol.`online`,
	eol.date_created 
FROM
	edge_online_log AS eol
	JOIN ( SELECT MAX( id ) AS id FROM edge_online_log WHERE `online` = 1 GROUP BY sn ) AS tmp ON eol.id = tmp.id 
ORDER BY
	eol.date_created DESC;
```

>   ![image-20220105114048018](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2022/01/2022010511404848.png)

##### 利用`MAX()`函数和`WHERE IN`

```sql
SELECT
	eol.sn,
	eol.`online`,
	eol.date_created 
FROM
	edge_online_log AS eol 
WHERE
	eol.id IN ( SELECT MAX( id ) id FROM edge_online_log AS a WHERE a.`online` = 1 GROUP BY a.sn ) 
ORDER BY
	eol.date_created DESC;
```

>   ![image-20220105114156090](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2022/01/2022010511415656.png)