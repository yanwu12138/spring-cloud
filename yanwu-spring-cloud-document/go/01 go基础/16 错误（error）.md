### go的错误处理

>   error类型是一个接口类型，这是它的定义：
>
>   ```go
>   type error interface {
>   	Error() string
>   }
>   ```
>
>   我们可以在编码中通过实现 error 接口类型来生成错误信息。函数通常在最后的返回值中返回错误信息。使用errors.New 可返回一个错误信息：

```go
func TestError() {
	fmt.Println("====================================== 错误 ======================================")
	result, err := div(10, 0)
	if err != "" {
		fmt.Println("err:", err)
	} else {
		fmt.Println("10/0 result:", result)
	}
}

func div(intA, intB int) (result int, err string) {
	if intB == 0 {
		runtimeErr := RuntimeError{100, "除数不能为0"}
		err = runtimeErr.Error()
		return
	} else {
		return intA / intB, ""
	}
}

type RuntimeError struct {
	code    uint   // 错误码
	message string // 错误信息
}

func (e RuntimeError) Error() string {
	return fmt.Sprintf("error >> code: %v, message: %v", e.code, e.message)
}
```

![image-20210513172504050](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/05/202105131725044.png)

### 错误的抛出与捕获

>   `panic` 与 `recover` 是 `Go` 的两个内置函数，这两个内置函数用于处理 `Go` 运行时的错误，`panic` 用于主动抛出错误，`recover` 用来捕获 `panic` 抛出的错误。
>
>   -   引发`panic`有两种情况，一是程序主动调用，二是程序产生运行时错误，由运行时检测并退出。
>   -   发生`panic`后，程序会从调用`panic`的函数位置或发生`panic`的地方立即返回，逐层向上执行函数的`defer`语句，然后逐层打印函数调用堆栈，直到被`recover`捕获或运行到最外层函数。
>   -   `panic`不但可以在函数正常流程中抛出，在`defer`逻辑里也可以再次调用`panic`或抛出`panic`。`defer`里面的`panic`能够被后续执行的`defer`捕获。
>   -   `recover`用来捕获`panic`，阻止`panic`继续向上传递。`recover()`和`defer`一起使用，但是`defer`只有在后面的函数体内直接被掉用才能捕获`panic`来终止异常，否则返回`nil`，异常继续向外传递。

