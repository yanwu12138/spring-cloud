### go 语言中的数组

>   数组是具有相同唯一类型的一组已编号且长度固定的数据项序列，这种类型可以是任意的原始类型例如整型、字符串或者自定义类型。数组元素可以通过索引（位置）来读取（或者修改），索引从 0 开始，第一个元素索引为 0，第二个索引为 1，以此类推。
>
>   ![image-20210513114121109](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/05/2021051311412121.png)

#### 声明数组

-   一般声明：`var arrayName [SIZE] valueType`
-   赋值操作：`var arrayName = [SIZE] valueType {value1、value2、value3、...}`
-   简单声明：`arrayName := [SIZE] valueType {value1、value2、value3、...}`

__注意：__使用赋值操作和简单声明的方式声明数组时，如果数组的长度不确定，可以使用`...`代替数组的长度，编译器会根据元素的个数自行推断数组的长度

```go
func TestArray() {
	fmt.Println("====================================== 数组 ======================================")
	var arrayA [10]int
	fmt.Println("arrayA:", arrayA, ", length:", len(arrayA))
	arrayA[0] = 10
	arrayA[7] = 10
	fmt.Println("arrayA:", arrayA, ", length:", len(arrayA))

	var arrayB = [...]string{"YanWu", "lotus", "WenXin", "WenFu"}
	fmt.Println("arrayB:", arrayB, ", length:", len(arrayB))

	arrayC := [...]string{"YanWu", "lotus", "WenXin", "WenFu"}
	fmt.Println("arrayC:", arrayC, ", length:", len(arrayC))

	for i := 0; i < len(arrayC);i++ {
		fmt.Println("arrayC > index:", i, ", value:", arrayC[i])
	}
}
```

![image-20210513120056489](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/05/2021051312005656.png)