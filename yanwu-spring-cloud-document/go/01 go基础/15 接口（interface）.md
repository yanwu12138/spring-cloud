### go 语言中的接口

>   `Go` 语言提供了另外一种数据类型即接口，它把所有的具有共性的方法定义在一起，任何其他类型只要实现了这些方法就是实现了这个接口。接口通过 `interface` 关键字来定义。

```go
type Device interface {
	// ----- 上线
	online() bool
	// ----- 离线
	offline()
	// ----- 开关
	onOff(onOff bool)
}

type Light struct {
}

func (light Light) online() bool {
	fmt.Println("light online")
	return true
}

func (light Light) offline() {
	fmt.Println("light offline")
}

func (light Light) onOff(onOff bool) {
	if onOff {
		fmt.Println("light on")
	} else {
		fmt.Println("light off")
	}
}

type Edge struct {
}

func (edge Edge) online() bool {
	fmt.Println("edge online")
	return true
}

func (edge Edge) offline() {
	fmt.Println("edge offline")
}

func (edge Edge) onOff(onOff bool) {
	if onOff {
		fmt.Println("edge on")
	} else {
		fmt.Println("edge off")
	}
}

func TestInterface() {
	fmt.Println("====================================== 接口 ======================================")
	var device Device
	device = new(Light)
	device.online()
	device.onOff(true)
	device.onOff(false)
	device.offline()
	fmt.Println("----------")
	device = new(Edge)
	device.online()
	device.onOff(true)
	device.onOff(false)
	device.offline()
}
```

![image-20210513170414406](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/05/2021051317041414.png)