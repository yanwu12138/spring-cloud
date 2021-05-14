### go 语言中的范围

>   `Go` 语言中 `range` 关键字用于 `for` 循环中迭代数组(`array`)、切片(`slice`)、通道(`channel`)或集合(`map`)的元素。在数组和切片中它返回元素的索引和索引对应的值，在集合中返回 `key-value` 对

```go
func TestRange() {
	fmt.Println("====================================== 范围 ======================================")
	// ----- 迭代数组
	sliceA := []int{1, 2, 3, 4, 5}
	for index, item := range sliceA {
		fmt.Println("slice > index:", index, ", item", item)
	}

	// ----- 迭代map
	mapA := map[string]int{"yanWu": 29, "lotus": 29, "wenXin": 3, "wenFu": 2}
	for key, value := range mapA {
		fmt.Println("map > key:", key, ", value", value)
	}
}
```

![image-20210513161543869](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/05/2021051316154343.png)