### go语言的通道

>   通道（`channel`）是用来传递数据的一个数据结构。通道可用于两个 `goroutine` 之间通过传递一个指定类型的值来同步运行和通讯。操作符 `<-` 用于指定通道的方向，发送或接收，箭头的指向就是数据的流向。如果未指定方向，则为双向通道。
>
>   `Channel`可以作为一个先入先出（`FIFO`）的队列，接收的数据和发送的数据的顺序是一致的。
>
>   ```go
>   channel <- value      // 把 v 发送到通道 ch
>   value := <- channel   // 从 ch 接收数据，并把值赋给 v
>   ```
>
>   声明一个通道很简单，我们使用chan关键字即可，通道在使用前必须先创建：
>
>   ```go
>   ch := make(chan int)
>   ```
>
>   **注意**：默认情况下，通道是不带缓冲区的。发送端发送数据，同时必须有接收端相应的接收数据。

```go
func TestChannel() {
	fmt.Println("====================================== channel ===========================")
	arr := []int{7, 2, 8, -9, 4, 0}
	chanA := make(chan int)
	go sum(arr[:len(arr)/2], chanA)
	go sum(arr[len(arr)/2:], chanA)
    // ----- 从通道中读取数据并赋值给r&&l
	r, l := <-chanA, <-chanA
	fmt.Println("r:", r, ", l:", l, ", sum:", r+l)
}

func sum(arr []int, chanA chan int) {
	sum := 0
	for _, item := range arr {
		sum += item
	}
	// ----- 把 sum 发送到通道 c
	chanA <- sum
}
```

![image-20210514100341845](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/05/2021051410034141.png)



#### 通道缓冲区

>   通道可以设置缓冲区，通过 `make` 的第二个参数指定缓冲区大小。带缓冲区的通道允许发送端的数据发送和接收端的数据获取处于异步状态，就是说发送端发送的数据可以放在缓冲区里面，可以等待接收端去获取数据，而不是立刻需要接收端去获取数据。不过由于缓冲区的大小是有限的，所以还是必须有接收端来接收数据的，否则缓冲区一满，数据发送端就无法再发送数据了。
>
>   **注意**：如果通道不带缓冲，发送方会阻塞直到接收方从通道中接收了值。如果通道带缓冲，发送方则会阻塞直到发送的值被拷贝到缓冲区内；如果缓冲区已满，则意味着需要等待直到某个接收方获取到一个值。接收方在有值可以接收之前会一直阻塞。

```go
func TestChanBuffer() {
	fmt.Println("====================================== channel > buffer ===========================")
	// ----- 定义一个缓冲区大小为2的通道
	chanA := make(chan int, 2)
	// ----- 因为 ch 是带缓冲的通道，我们可以同时发送两个数据, 而不用立刻需要去同步读取数据
	chanA <- 100
	chanA <- 200
	// ----- 读取两个数据
	intA, intB := <-chanA, <-chanA
	fmt.Println("intA:", intA, ", intB:", intB)
}
```

![image-20210514100815877](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/05/2021051410081515.png)

##### 有无缓冲区的区别

无缓冲是同步的，例如 `make(chan int)`，就是一个送信人去你家门口送信，你不在家他不走，你一定要接下信，他才会走，无缓冲保证信能到你手上。

有缓冲是异步的，例如 `make(chan int, 2)`，就是一个送信人去你家仍到你家的信箱，转身就走，除非你的信箱满了，他必须等信箱空下来，有缓冲的保证信能进你家的邮箱。



#### 关闭通道

>   通道可以使用 `close()` 函数来关闭

```go
func TestChanClose() {
	fmt.Println("====================================== channel > close ===========================")
	chanA := make(chan int, 10)
	go testClose(cap(chanA), chanA)
	for i := range chanA {
		fmt.Println("i:", i)
	}
}

func testClose(intA int, chanA chan int) {
	x, y := 0, 1
	for i := 0; i < intA; i++ {
		chanA <- x
		x, y = y, x+y
	}
	// ----- 关闭通道
	close(chanA)
}
```

![image-20210514101747148](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/05/2021051410174747.png)



#### channel类型

>   `Channel`类型的定义格式如下：
>
>   ```
>   ChannelType = ( chan | chan<- | <-chan ) ElementType .
>   ```
>
>   它包括三种类型的定义。可选的`<-`代表channel的方向。如果没有指定方向，那么`Channel`就是双向的，既可以接收数据，也可以发送数据。
>
>   ```go
>   chan T          	// 可以接收和发送类型为 T 的数据
>   chan<- T		  	// 只可以用来发送 T 类型的数据
>   <-chan T	      	// 只可以用来接收 T 类型的数据
>   ```



#### select

>   `select`语句选择一组可能的`send`操作和`receive`操作去处理。它类似`switch`,但是只是用来处理通讯(`communication`)操作。它的`case`可以是`send`语句，也可以是`receive`语句，亦或者`default`。`receive`语句可以将值赋值给一个或者两个变量。它必须是一个`receive`操作。
>
>   最多允许有一个`default case`,它可以放在`case`列表的任何位置，尽管我们大部分会将它放在最后。
>
>   如果有同时多个`case`去处理,比如同时有多个`channel`可以接收数据，那么`Go`会伪随机的选择一个`case`处理(`pseudo-random`)。如果没有`case`需要处理，则会选择`default`去处理，如果`default case`存在的情况下。如果没有`default case`，则`select`语句会阻塞，直到某个`case`需要处理。
>
>   __注意：__`nil channel`上的操作会一直被阻塞，如果没有`default case`,只有`nil channel`的`select`会一直被阻塞。

```go
func TestSelect() {
	fmt.Println("====================================== channel > select ===========================")
	chanA := make(chan int)
	chanB := make(chan int)
	go func() {
		for i := 0; i < 10; i++ {
			fmt.Println("i:", <-chanA)
		}
		chanB <- 0
	}()
	fibonacci(chanA, chanB)
}

func fibonacci(chanA, chanB chan int) {
	x, y := 0, 1
	for {
		select {
		// ----- 将x发送到chanA
		case chanA <- x:
			x, y = y, x+y
		// ----- 当能从chanB中读取数据时，退出操作
		case <-chanB:
			fmt.Println("quit")
			return
		}
	}
}
```



#### timeout

>   `select`有很重要的一个应用就是超时处理。 因为没有`case`需要处理，`select`语句就会一直阻塞着。这时候我们可能就需要一个超时操作，用来处理超时的情况。

```go
func TestTimeout() {
	fmt.Println("====================================== channel > select > timeout ===========================")
	chanA := make(chan string, 1)
	go func() {
		time.Sleep(time.Second * 2)
		chanA <- "result 1"
	}()
	select {
	// ----- 从chanA中读取数据
	case res := <-chanA:
		fmt.Println(res)
	// ----- 超时处理
	case <-time.After(time.Second * 1):
		fmt.Println("timeout 1")
	}
}
```

![image-20210514104805547](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/05/202105141048055.png)



#### Timer & Ticker

>   `timer`是一个定时器，代表未来的一个单一事件，你可以告诉`timer`你要等待多长时间，它提供一个`Channel`，在将来的那个时间那个`Channel`提供了一个时间值。下面的例子中第二行会阻塞`2`秒钟左右的时间，直到时间到了才会继续执行。
>
>   ```go
>   timer1 := time.NewTimer(time.Second * 2)
>   <-timer1.C
>   fmt.Println("Timer 1 expired")
>   ```
>
>   `ticker`是一个定时触发的计时器，它会以一个间隔(`interval`)往`Channel`发送一个事件(当前时间)，而`Channel`的接收者可以以固定的时间间隔从`Channel`中读取事件。下面的例子中`ticker`每`500`毫秒触发一次，你可以观察输出的时间。
>
>   ```go
>   ticker := time.NewTicker(time.Millisecond * 500)
>   go func() {
>       for t := range ticker.C {
>           fmt.Println("Tick at", t)
>       }
>   }()
>   ```