### go 语言中的集合

>   `Map` 是一种无序的键值对的集合。`Map` 最重要的一点是通过 `key` 来快速检索数据，`key` 类似于索引，指向数据的值。`Map` 是一种集合，所以我们可以像迭代数组和切片那样迭代它。不过，`Map` 是无序的，我们无法决定它的返回顺序，这是因为 `Map` 是使用 `hash` 表来实现的。

```go
func TestMap() {
	fmt.Println("====================================== 集合 ======================================")
	var mapA map[string]int
	mapA = make(map[string]int)
	mapA["yanWu"] = 29
	mapA["lotus"] = 29
	fmt.Println("mapA:", mapA, ", length:", len(mapA))

	age, ok := mapA["yanWu"]
	if ok {
		fmt.Println("yanWu age:", age)
	} else {
		fmt.Println("yanWu 不存在")
	}

	fmt.Println("----------")
	mapB := map[string]string{"湖北": "武汉", "湖南": "长沙", "浙江": "杭州"}
	for key, value := range mapB {
		fmt.Println(key, "的省会:", value)
	}

	delete(mapB, "浙江")
	fmt.Println("----------")
	for key, value := range mapB {
		fmt.Println(key, "的省会:", value)
	}

	mapB["浙江"] = "杭州"
	fmt.Println("----------")
	for key, value := range mapB {
		fmt.Println(key, "的省会:", value)
	}
}
```

![image-20210513163050334](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/05/2021051316305050.png)