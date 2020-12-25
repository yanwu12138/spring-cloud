### 数据类型

### 数值

| 类型         |               大小（byte）               | 含义       |
| ------------ | :--------------------------------------: | ---------- |
| TINYINT      |                    1                     | 整数       |
| SMALLINT     |                    2                     | 整数       |
| MEDIUMINT    |                    3                     | 整数       |
| INT或INTEGER |                    4                     | 整数       |
| BIGINT       |                    8                     | 整数       |
| FLOAT        |                    4                     | 单精度浮点 |
| DOUBLE       |                    8                     | 双精度浮点 |
| DECIMAL      | 对DECIMAL(M,D)： 如果M>D，为M+2否则为D+2 | 小数       |



### 日期和时间

| 类型      | 大小（byte） | 含义     | 格式                |
| --------- | :----------: | -------- | ------------------- |
| DATE      |      3       | 日期     | YYYY-MM-DD          |
| TIME      |      3       | 时间     | HH:MM:SS            |
| YEAR      |      1       | 年分     | YYYY                |
| DATETIME  |      8       | 日期时间 | YYYY-MM-DD HH:MM:SS |
| TIMESTAMP |      4       | 时间戳   | YYYYMMDD HHMMSS     |

### 字符串

| 类型       | 最大值（byte） | 含义                            |
| ---------- | :------------: | ------------------------------- |
| CHAR       |      255       | 定长字符串                      |
| VARCHAR    |     65 535     | 变长字符串                      |
| TINYBLOB   |      255       | 不超过 255 个字符的二进制字符串 |
| TINYTEXT   |      255       | 短文本字符串                    |
| BLOB       |     65 535     | 二进制形式的长文本数据          |
| TEXT       |     65 535     | 长文本数据                      |
| MEDIUMBLOB |   16 777 215   | 二进制形式的中等长度文本数据    |
| MEDIUMTEXT |   16 777 215   | 中等长度文本数据                |
| LONGBLOB   | 4 294 967 295  | 二进制形式的极大文本数据        |
| LONGTEXT   | 4 294 967 295  | 极大文本数据                    |



### 约束

| 约束                   | 说明                                                         |
| ---------------------- | ------------------------------------------------------------ |
| 非空约束：NOT  NULL    | 控制字段的内容一定不能为空（NULL）                           |
| 唯一约束：UNIQUE  KEY  | 控制字段内容必须唯一，一个表允许有多个Unique约束             |
| 主键约束：PRIMARY  KEY | 主键约束，控制字段不能重复，但它在一个表只允许出现一个       |
| 外键约束：FOREIGN  KEY | 外键约束，预防破坏表之间连接的动作，也能防止非法数据插入外键列，因为它必须是它指向的那个表中的值之一 |
| 范围约束：CHECK        | 用于控制字段的值范围                                         |



### 命令行

#### 数据库

##### 创建

```SQL
-- DATABASE_NAME	数据库名称
CREATE DATABASE IF NOT EXISTS DATABASE_NAME;
```

##### 删除数据库

```SQL
-- DATABASE_NAME	数据库名称
DROP DATABASE DATABASE_NAME;
```

##### 切换数据库

```SQL
-- DATABASE_NAME	数据库名称
USE DATABASS_NAME;
```



#### 数据表

##### 创建数据表

```SQL
-- TABLE_NAME				表名
-- CLOUMN					字段名
-- TYPE						字段类型
-- [NOT | DEFAULT] NULL		是否允许为null
-- COMMENT					字段描述信息
CREATE TABLE IF NOT EXISTS TABLE_NAME (CLOUMN TYPE [NOT | DEFAULT] NULL COMMENT);

-- 示例
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否可用(true: 可用; false: 不可用)',
  `date_create` datetime NOT NULL COMMENT '创建时间',
  `date_update` datetime DEFAULT NULL COMMENT '修改时间',
  `description` varchar(255) DEFAULT NULL COMMENT '描述信息',
  `account` varchar(32) NOT NULL COMMENT '账号',
  `password` varchar(100) NOT NULL COMMENT '密码',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_account` (`account`) USING BTREE COMMENT '账号'
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='用户';
```

##### 删除数据表

```SQL
-- TABLE_NAME	表名
DROP TABLE TABLE_NAME;

-- 示例
DROP TABLE `user`;
```

##### 清空数据表

```SQL
-- TABLE_NAME	表名
TRUNCATE TABLE TABLE_NAME;

-- 示例
TRUNCATE TABLE `user`;
```

##### 修改表名

```SQL
-- TABLE_NAME_OLD	旧表名
-- TABLE_NAME_NEW	新表明
ALTER TABLE TABLE_NAME_OLD RENAME TO TABLE_NAME_NEW;

-- 示例
ALTER TABLE `user` RENAME TO `yanwu_user`;
```

##### 添加表字段

```SQL
-- TABLE_NAME				表名
-- CLOUNM_NAME				字段
-- TYPE						数据类型
-- LENGTH					字段长度
-- [NOT | DEFAULT] NULL		是否允许为null
-- COMMENT					字段描述信息
ALTER TABLE TABLE_NAME ADD CLOUNM_NAME TYPE(LENGTH) [NOT | DEFAULT] NULL COMMENT;

-- 示例
ALTER TABLE yanwu_user ADD role_id bigint(20) NOT NULL COMMENT '角色ID';
```

##### 删除表字段

```SQL
-- TABLE_NAME	表名
-- CLOUMN		字段
ALTER TABLE TABLE_NAME DROP CLOUMN;

-- 示例
ALTER TABLE yanwu_user DROP role_id;
```

##### 修改表字段

```SQL
-- TABLE_NAME				表名
-- CLOUNM_NAME				字段
-- TYPE						数据类型
-- LENGTH					字段长度
-- [NOT | DEFAULT] NULL		是否允许为null
-- COMMENT					字段描述信息
ALTER TABLE TABLE_NAME MODIFY CLOUMN TYPE(LENGTH) [NOT | DEFAULT] NULL COMMENT;

-- 示例
ALTER TABLE yanwu_user MODIFY role_id bigint(20) NOT NULL COMMENT '角色ID';
```



#### 索引

##### 创建索引

```SQL
-- INDEX_NAME	索引名称
-- TABLE_NAME	表名
-- CLOUMN		字段名
-- LENGTH		字段长度
-- 注意：如果是CHAR，VARCHAR类型，length可以小于字段实际长度；如果是BLOB和TEXT类型，必须指定length
CREATE INDEX INDEX_NAME ON TABLE_NAME(CLOUMN(LENGTH));
```

##### 建表时直接指定索引

```SQL
CREATE TABLE TABLE_NAME(CLOUMN... INDEX [TABLE_NAME] (CLOUMN(LENGTH)));
```

##### 修改索引

```SQL
ALTER TABLE TABLE_NAME ADD INDEX INDEX_NAME(CLOUMN);
```

##### 删除索引

```SQL
-- INDEX_NAME	索引名称
-- TABLE_NAME	表名
DROP INDEX INDEX_NAME ON TABLE_NAME;

-- 示例
DROP INDEX uk_account ON yanwu_user;
```

##### 查看索引

```SQL
-- TABLE_NAME	表名
SHOW INDEX FROM TABLE_NAME;

-- 示例
SHOW INDEX FROM yanwu_user;
```