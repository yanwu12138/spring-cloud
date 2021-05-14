### go 语言中的指针

>   变量是一种使用方便的占位符，用于引用计算机内存地址。`Go` 语言的取地址符是 `&`，放到一个变量前使用就会返回相应变量的内存地址。一个指针变量指向了一个值的内存地址。类似于变量和常量，在使用指针前你需要声明指针。指针声明格式如下：
>
>   ```go
>   var var_name *var-type
>   ```

```go
func TestPointer() {
	fmt.Println("====================================== 指针 ======================================")
	// ----- 声明变量
	var intA = 20
	// ----- 声明指针变量

	ptrA := &intA
	fmt.Println("intA:", intA, ", 变量的地址:", &intA, ", 指针的地址:", ptrA, ", 指针的地址值:", *ptrA)

	var ptrB *int
	fmt.Println("空指针:", ptrB)

	// ----- 指针数组，该数组中存储的每个元素都是地址
	arr := [...]int{1, 2, 3}
	var ptrArr [len(arr)]*int
	fmt.Println("数组:", arr, ", 指针数组:", ptrArr)
	ptrArr[0] = &arr[0]
	ptrArr[1] = &arr[1]
	ptrArr[2] = &arr[2]
	fmt.Println("数组:", arr, ", 指针数组:", ptrArr)

	// ----- 指向指针的指针
	intB := 10
	ptrB1 := &intB
	ptrB2 := &ptrB1
	fmt.Println("intB:", intB, ", ptrB1:", ptrB1, ", *ptrB1:", *ptrB1, ", ptrB2", ptrB2, ", *ptrB2", *ptrB2)

	// ----- 将指针作为参数传递（引用传递）
	intC, intD := 10, 20
	fmt.Println("原始数据 > intC:", intC, ", intD:", intD)
	swap(&intC, &intD)
	fmt.Println("调用之后 > intC:", intC, ", intB:", intD)
}

func swap(x *int, y *int) {
	*x, *y = *y, *x
}
```

![image-20210513144101736](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/05/202105131441011.png)



#### 空指针

>   当一个指针被定义后没有分配到任何变量时，它的值为 nil。nil 指针也称为空指针。nil在概念上和其它语言的null、None、nil、NULL一样，都指代零值或空值。