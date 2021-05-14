### go 语言中的切片

>   `Go` 语言切片是对数组的抽象。`Go` 数组的长度不可改变，在特定场景中这样的集合就不太适用，`Go` 中提供了一种灵活，功能强悍的内置类型切片("动态数组")，与数组相比切片的长度是不固定的，可以追加元素，在追加时可能使切片的容量增大。
>
>   切片的要素：
>
>   -   类型：切片中元素的类型
>   -   长度：切片的长度
>   -   容量：切片的容量

```go
func TestSlice() {
	fmt.Println("====================================== 切片 ======================================")
	var sliceA = make([]int, 2, 10)
	fmt.Println("sliceA:", sliceA, ", length:", len(sliceA), ", capacity:", cap(sliceA))
	sliceA[0] = 10
	fmt.Println("sliceA:", sliceA, ", length:", len(sliceA), ", capacity:", cap(sliceA))

	arr := [...]int{1, 2, 3, 4, 5}

	sliceB := arr[:]
	fmt.Println("sliceB:", sliceB, ", length:", len(sliceB), ", capacity:", cap(sliceB))

	sliceC := arr[0:]
	fmt.Println("sliceC:", sliceC, ", length:", len(sliceC), ", capacity:", cap(sliceC))

	sliceD := arr[:5]
	fmt.Println("sliceD:", sliceD, ", length:", len(sliceD), ", capacity:", cap(sliceD))

	sliceE := arr[0:5]
	fmt.Println("sliceE:", sliceE, ", length:", len(sliceE), ", capacity:", cap(sliceE))

	sliceF := arr[1:4]
	fmt.Println("sliceF:", sliceF, ", length:", len(sliceF), ", capacity:", cap(sliceF))

	sliceG := []int{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}
	fmt.Println("sliceG:", sliceG, ", length:", len(sliceG), ", capacity:", cap(sliceG))
	fmt.Println("sliceG[1:4]:", sliceG[1:4])
	fmt.Println("sliceG[:3]:", sliceG[:3])
	fmt.Println("sliceG[5:]:", sliceG[5:])
	// ----- append(): 追加元素
	sliceG = append(sliceG, 10)
	fmt.Println("sliceG:", sliceG, ", length:", len(sliceG), ", capacity:", cap(sliceG))
	sliceG = append(sliceG, 11, 12)
	fmt.Println("sliceG:", sliceG, ", length:", len(sliceG), ", capacity:", cap(sliceG))
	// ----- copy(): 拷贝切片
	sliceH := make([]int, len(sliceG), cap(sliceG))
	copy(sliceH, sliceG)
	fmt.Println("sliceH:", sliceH, ", length:", len(sliceH), ", capacity:", cap(sliceH))
}
```

![image-20210513160247158](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/05/2021051316024747.png)