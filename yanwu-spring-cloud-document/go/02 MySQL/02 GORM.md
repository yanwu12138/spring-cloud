### GORM

>   官网：[https://gorm.io/zh_CN/](https://gorm.io/zh_CN/)
>
>   参考文档：[https://www.cnblogs.com/jiujuan/p/12676195.html](https://www.cnblogs.com/jiujuan/p/12676195.html)

#### 安装

```bash
go get gorm.io/gorm
```

![image-20210514145122054](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/05/2021051414512222.png)



### 模型

#### 声明模型

```go
import (
	"database/sql"
	"gorm.io/gorm"
	"time"
)

/**
 * @Author Baofeng Xu
 * @Date 2021/5/14 14:52
 *
 * Description: 用户
 **/

type User struct {
	gorm.Model                  // 声明该struct是gorm模型
	ID           uint           `gorm:"primaryKey"` // 通过primaryKey指定主键
	Name         string         `gorm:"size:32;index:idx_name"` // 用户名，通过size指定长度，通过index指定一个名为idx_name的索引
	Email        *string        `gorm:"uniqueIndex:un_email"` // 用户邮箱，通过index指定一个名为un_email的唯一索引
	Age          uint8          `gorm:"check:age > 100"` // 年龄
	Birthday     *time.Time     // 出生日期
	MemberNumber sql.NullString // 成员数量
	ActivatedAt  sql.NullTime   `gorm:"->;<-:create"` // 激活时间，允许读和创建，但不允许更改
	CreatedAt    time.Time      `gorm:"->"`           // 创建时间，只读
	UpdatedAt    time.Time      `gorm:"->;<-:update"` // 更新时间，允许读和更新
}
```

#### 字段级权限控制

**注意：** 使用 `GORM Migrator` 创建表时，不会创建被忽略的字段。

```go
type User struct {
  Name string `gorm:"<-:create"` 			// 允许读和创建
  Name string `gorm:"<-:update"` 			// 允许读和更新
  Name string `gorm:"<-"`        			// 允许读和写（创建和更新）
  Name string `gorm:"<-:false"`  			// 允许读，禁止写
  Name string `gorm:"->"`        			// 只读（除非有自定义配置，否则禁止写）
  Name string `gorm:"->;<-:create"` 		// 允许读和写
  Name string `gorm:"->:false;<-:create"` 	// 仅创建（禁止从 db 读）
  Name string `gorm:"-"`  					// 通过 struct 读写会忽略该字段
}
```

#### 创建 & 更新时间

GORM 约定使用 `CreatedAt`、`UpdatedAt` 追踪创建/更新时间。如果定义了这种字段，GORM 在创建、更新时会自动填充 [当前时间](https://gorm.io/zh_CN/docs/gorm_config.html#now_func)

要使用不同名称的字段，可以配置 `autoCreateTime`、`autoUpdateTime` 标签

如果想要保存 UNIX（毫/纳）秒时间戳，而不是 time，只需简单地将 `time.Time` 修改为 `int` 即可

```go
type User struct {
  CreatedAt time.Time 								// 在创建时，如果该字段值为零值，则使用当前时间填充
  UpdatedAt int       								// 在创建时该字段值为零值或者在更新时，使用当前时间戳秒数填充
  Updated   int64 `gorm:"autoUpdateTime:nano"` 		// 使用时间戳填纳秒数充更新时间
  Updated   int64 `gorm:"autoUpdateTime:milli"` 	// 使用时间戳毫秒数填充更新时间
  Created   int64 `gorm:"autoCreateTime"`      		// 使用时间戳秒数填充创建时间
}
```

#### 结构体嵌入

对于正常的结构体字段，也可以通过标签 `embedded` 将其嵌入，并且可以使用标签 `embeddedPrefix` 来为 db 中的字段名添加前缀，例如：

```go
// ----- 定义通用的结构体
type Author struct {
    Name  string
    Email string
}

// ----- 将通用结构体嵌入到另一个结构体
type Blog struct {
  	ID      int64
  	// ----- 使用embedded标签将Author嵌入Blog
  	Author  Author `gorm:"embedded"`
  	Upvotes int32
}
// 等效于
type Blog struct {
  	ID    		int64
 	Name  		string
  	Email 		string
  	Upvotes  	int32
}

// ----- 将通用结构体嵌入到另一个结构体
type Blog struct {
  	ID      int64
  	// ----- 使用embedded标签将Author嵌入Blog
  	Author  Author `gorm:"embedded;embeddedPrefix:author_"`
  	Upvotes int32
}
// 等效于
type Blog struct {
  	ID    				int64
 	AuthorName  		string
  	AuthorEmail 		string
  	Upvotes  			int32
}
```

#### 字段的标签

| 标签                   | 说明                                                         |
| ---------------------- | ------------------------------------------------------------ |
| column                 | 指定 db 列名                                                 |
| type                   | 列数据类型，推荐使用兼容性好的通用类型，例如：所有数据库都支持 bool、int、uint、float、string、time、bytes 并且可以和其他标签一起使用，例如：`not null`、`size`, `autoIncrement`… 像 `varbinary(8)` 这样指定数据库数据类型也是支持的。在使用指定数据库数据类型时，它需要是完整的数据库数据类型，如：`MEDIUMINT UNSIGNED not NULL AUTO_INCREMENT` |
| size                   | 指定列大小，例如：`size:256`                                 |
| primaryKey             | 指定列为主键                                                 |
| unique                 | 指定列为唯一                                                 |
| default                | 指定列的默认值                                               |
| precision              | 指定列的精度                                                 |
| scale                  | 指定列大小                                                   |
| not null               | 指定列为 `NOT NULL`                                          |
| autoIncrement          | 指定列为自动增长                                             |
| autoIncrementIncrement | 自动步长，控制连续记录之间的间隔                             |
| embedded               | 嵌套字段                                                     |
| embeddedPrefix         | 嵌入字段的列名前缀                                           |
| autoCreateTime         | 创建时追踪当前时间，对于 `int` 字段，它会追踪秒级时间戳，您可以使用 `nano`/`milli` 来追踪纳秒、毫秒时间戳，例如：`autoCreateTime:nano` |
| autoUpdateTime         | 创建/更新时追踪当前时间，对于 `int` 字段，它会追踪秒级时间戳，您可以使用 `nano`/`milli` 来追踪纳秒、毫秒时间戳，例如：`autoUpdateTime:milli` |
| index                  | 根据参数创建索引，多个字段使用相同的名称则创建复合索引，查看 [索引](https://gorm.io/zh_CN/docs/indexes.html) 获取详情 |
| uniqueIndex            | 与 `index` 相同，但创建的是唯一索引                          |
| check                  | 创建检查约束，例如 `check:age > 100`，查看 [约束](https://gorm.io/zh_CN/docs/constraints.html) 获取详情 |
| <-                     | 设置字段写入的权限， `<-:create` 只创建、`<-:update` 只更新、`<-:false` 无写入权限、`<-` 创建和更新权限 |
| ->                     | 设置字段读的权限，`->:false` 无读权限                        |
| -                      | 忽略该字段，`-` 无读写权限                                   |
| comment                | 迁移时为字段添加注释                                         |



### 连接到数据库

#### MySQL

##### 下载驱动

```bash
go get -t gorm.io/driver/mysql
```

![image-20210514154733438](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/05/2021051415473333.png)

#### 连接

```go
import (
	"fmt"
	"gorm.io/driver/mysql"
	"gorm.io/gorm"
)

/**
 * @Author Baofeng Xu
 * @Date 2021/5/14 15:48
 *
 * Description:
 **/

var myDb *gorm.DB

func init() {
	// ----- 初始化数据库连接
	db, err := gorm.Open(mysql.New(mysql.Config{
        // DSN data source name
		DSN: "root:yanwu12138@tcp(192.168.56.150:3306)/go_db_test?charset=utf8mb4&parseTime=True&loc=Local", 
        // string 类型字段的默认长度
		DefaultStringSize: 256, 
        // 禁用 datetime 精度，MySQL 5.6 之前的数据库不支持
		DisableDatetimePrecision: true, 
        // 重命名索引时采用删除并新建的方式，MySQL 5.7 之前的数据库和 MariaDB 不支持重命名索引
		DontSupportRenameIndex: true, 
        // 用 `change` 重命名列，MySQL 8 之前的数据库和 MariaDB 不支持重命名列
		DontSupportRenameColumn: true, 
        // 根据当前 MySQL 版本自动配置
		SkipInitializeWithVersion: false, 
	}), &gorm.Config{})
    
	if err != nil {
		fmt.Println("failed to open database:", err.Error())
		return
	}
	
    myDb = db
	fmt.Println("database init success:")
}
```

**注意：**想要正确的处理 `time.Time` ，您需要带上 `parseTime` 参数， ([更多参数](https://github.com/go-sql-driver/mysql#parameters)) 要支持完整的 UTF-8 编码，您需要将 `charset=utf8` 更改为 `charset=utf8mb4` 查看 [此文章](https://mathiasbynens.be/notes/mysql-utf8mb4) 获取详情



#### 插入数据

```go
user := model.GormUser{Name: "yanwu", Email: "yanwu0527@163.com", Age: 29}
result := myDb.Create(&user)
fmt.Println("RowsAffected:", result.RowsAffected, ", userId: ", user.ID)
```



#### 批量插入

```go
users := []model.GormUser{{Name: "yanwu", Email: "yanwu0527@163.com", Age: 29}, {Name: "lotus", Email: "499496273@qq.com", Age: 29}, {Name: "wenxin", Age: 3}, {Name: "wenfu", Age: 2}}
results := myDb.CreateInBatches(users, len(users))
for _, user := range users {
	fmt.Println("RowsAffected:", results.RowsAffected, ", userId: ", user.ID)
}
```



#### 根据Map插入

>   **注意：** 根据 map 创建记录时，association 不会被调用，且主键也不会自动填充

```go
userMap := []map[string]interface{}{
    {"Name": "yanwu", "Email": "yanwu0527@163.com", "Age": 29},
    {"Name": "lotus", "Email": "499496273@qq.com", "Age": 29},
    {"Name": "wenxin", "Email": "wenxin@qq.com", "Age": 3},
    {"Name": "wenfu", "Email": "wenfu@qq.com", "Age": 2},
}
resultMap := myDb.Model(&model.GormUser{}).CreateInBatches(userMap, len(userMap))
fmt.Println("RowsAffected:", resultMap.RowsAffected)
```

#### 查询数据

```go
fmt.Println("------------- 获取第一条记录（主键升序） -------------")
var userFirst model.GormUser
myDb.First(&userFirst)
fmt.Printf("userFirst: %+v\n", userFirst)

fmt.Println("------------- 获取一条记录，没有指定排序字段 -------------")
var userTake model.GormUser
myDb.Take(&userTake)
fmt.Printf("userTake: %+v\n", userTake)

fmt.Println("------------- 获取最后一条记录（主键降序） -------------")
var userLast model.GormUser
myDb.Last(&userLast)
fmt.Printf("userLast: %+v\n", userLast)

fmt.Println("------------- 根据主键检索数据 -------------")
var userById model.GormUser
myDb.First(&userById, 10)
fmt.Printf("userById: id: %v > %+v\n", userById.ID, userById)

fmt.Println("------------- 根据主键集合检索数据 -------------")
var userByIds []model.GormUser
myDb.Find(&userByIds, []uint{12, 15, 17})
for _, user := range userByIds {
    fmt.Printf("userByIds: id: %v > %+v\n", user.ID, user)
}
```

