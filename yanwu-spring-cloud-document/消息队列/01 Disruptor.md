### Disruptor

单机最快消息队列，用于生产者消费者模式、观察者模式

#### 特点

- 无锁
- 高并发
- 速度快

#### 核心

通过数组实现环形队列（ringBuffer），没有首尾指针，只维护一个位置（sequence），直接覆盖（不用清除）旧的数据，降低GC频率

#### 生产者模式

- 单例：只有一个生产者的时候能够使用，多个生产者使用单例模式会产生消息被丢掉的现象
- 多例：默认模式

#### 等待策略

| 等待策略                    | 说明                                                         |
| --------------------------- | ------------------------------------------------------------ |
| BlockingWaitStrategy        | 通过线程阻塞的方式，等待生产者唤醒，被唤醒后，再循环检查依赖的sequence是都已经消费 |
| BusySpinWaitStrategy        | 线程一直自旋等待，可能会比较消耗CPU                          |
| LiteBlockingWaitStrategy    | 线程阻塞等待生产者唤醒，与BlockWaitStrategy的区别在于：signalNeeded.getAndSet > 如果两个线程同时访问一个waitfor，可以减少lock的加锁次数 |
| LiteTimeoutWaitStrategy     | 与LiteBlockingWaitStrategy相比设置了阻塞时间，超过时间后抛异常 |
| PhasedBackoffWaitStrategy   | 根据时间参数和传入的等待策略来决定使用哪种等待策略           |
| TimeoutBlockingWaitStrategy | 相对于BlockingWaitStrategy的区别是：设置了等待时间，超时后抛异常 |
| YieldWaitStrategy           | 尝试100次还没有得到执行，然后Thread.yield()让出CPU           |
| SleepingWaitStrategy        | sleep等待                                                    |

