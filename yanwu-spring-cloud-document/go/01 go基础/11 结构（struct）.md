### go 语言中的结构

>   `Go` 语言中数组可以存储同一类型的数据，但在结构体中我们可以为不同项定义不同的数据类型。结构体是由一系列具有相同类型或不同类型的数据构成的数据集合。结构体定义需要使用 `type` 和 `struct` 关键字。`struct` 关键字定义一个新的数据类型，结构体中有一个或多个成员。`type` 关键字设定了结构体的名称。结构体的格式如下：
>
>   ```go
>   type User struct {
>   	username string  // 用户名
>   	password string  // 密码
>   	sex      bool    // 性别 [true: 男; false: 女]
>   	age      uint8   // 年龄
>   	height   float32 // 身高
>   	weight   float32 // 体重
>   }
>   ```

```go
type User struct {
	username string  // 用户名
	password string  // 密码
	sex      bool    // 性别 [true: 男; false: 女]
	age      uint8   // 年龄
	height   float32 // 身高
	weight   float32 // 体重
}

func TestStruct() {
	fmt.Println("====================================== 结构 ======================================")
	var yanWu User
	yanWu.username = "YanWu"
	yanWu.password = "12138"
	yanWu.sex = constant.MAN
	yanWu.age = 29
	yanWu.height = 1.72
	yanWu.weight = 71.2
	fmt.Println("yanWu:", yanWu)
	printUser(yanWu)

	lotus := User{"lotus", "12138", constant.WOMAN, 29, 1.68, 51}
	printUser(lotus)

	wenXin := User{username: "WenXin", sex: constant.MAN, age: 3}
	printUser(wenXin)

	wenFu := User{username: "WenFu", sex: constant.WOMAN, age: 2}
	printUser(wenFu)
}

func printUser(user User) {
	fmt.Println("username:", user.username, ", password:", user.password, ", sex:", user.sex, ", age:", user.age, ", height:", user.height, ", weight:", user.weight)
}
```

![image-20210513150652719](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/05/2021051315065252.png)