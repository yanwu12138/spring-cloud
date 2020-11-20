### CPU缓存

>    CPU缓存是位于CPU与内存之间的临时存储器，它的容量比内存小的多，但是交换速度却比内存要快的多。CPU缓存的出现主要是为了解决CPU运算速度与内存读写速度不匹配的矛盾。因为CPU运算速度比内存读写速度要快的多的多的多的多......，这种情况会使CPU将很长时间花费在等待内存的读写上。
>
>   为了高效的访问数据，将内存中的一小部分CPU___常用的或可能即将访问的数据___存放在缓存中，当CPU需要调用大量数据时，优先从缓存中调用，从而加快读写速度。
>
>   当CPU需要读取数据进行计算时，首先需要在CPU缓存中查到所需数据并在最短时间交付给CPU进行计算；如果没有查到所需要的数据，CPU则会提出“要求”经过缓存从内存中读取，再原路返回CPU进行计算，同时把这个数据所在的数据块也调入缓存，可以使得以后对整块数据的读取都从缓存中进行，不必再调用内存。
>
>   缓存的大小是CPU的重要标志之一，而且缓存的结构和大小对CPU的计算速度影响非常大，CPU内缓存的运行频率极高，一般是和处理器同频运作，工作效率远远大于系统的内存和硬盘。实际工作中，CPU往往需要重复的读取同样的数据块，而缓存的通量的增大，可以大幅度提升CPU内部读取速度的命中率，而不用再到内存或者硬盘上寻找。但从CPU芯片的面积和成本的因素考虑，缓存都很小。

#### 如何判断哪些数据是常用的

缓存上存储的数据是那些计算机认为在接下来更有可能访问到的数据，计算机如何判断哪些数据接下来更有可能用到呢？系统对数据的访问频率有两种假设：

-   时间局部性：时间局部性假设目前访问的数据在接下来也更有可能再次访问到，所以计算机会把刚刚访问过的数据放入缓存中；

-   空间局部性：空间局部性假设与目前访问的数据相邻的那些数据接下来也更有可能访问到，所以会把当前数据周围的数据放入缓存中；

一个编写良好的代码往往符合时间局部性和空间局部性。

#### CPU的三级缓存

![image-20201026151859577](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2020/10/2020102615185959.png)

##### L1 cache

___一级缓存：___是CPU第一层级的高速缓存，主要承担的工作是缓存数据和缓存指令。L1缓存的容量和结构对CPU性能影响很大，但是由于它的结构很复杂，又考虑到成本等方面的因素，一般CPU的一级缓存也就能做到256KB左右的水平。

##### L2 cache

___二级缓存：___是CPU的第二层级高速缓存，二级缓存的容量会直接影响CPU性能，原则是越大越好。而且它是跟着核心走的，比如8代酷睿的i7-8700，6个核心每个都拥有256KB的二级缓存，属于各核心独享，这样总数就达到了1.5MB。

##### L3 cache

___三级缓存：___其实原本是服务器级别CPU才有的，后来逐步下放到家用级CPU上。三级缓存的作用是进一步降低内存延迟，同时提升海量数据量计算时的性能。和一、二级缓存不同的是，三级缓存是核心共享的，而且容量可以做的很大。

>   每一级缓存中所储存的全部数据都是下一级缓存的一部分，这三种缓存的技术难度和制造成本是相对递减的，所以其容量也是相对递增的。
>
>   当CPU要读取一个数据时，首先从一级缓存中查找，如果没有找到再从二级缓存中查找，如果还是没有就从三级缓存或内存中查找。
>
>   那么一般来说，若每级缓存的命中率大概都在80%左右，也就是说全部数据量的80%都可以在一级缓存中找到，只剩下20%的总数据量才需要从二级缓存、三级缓存或内存中读取，由此可见一级缓存是整个CPU缓存架构中最为重要的部分。

#### 数据在存储器层次之间传递方式

>   数据在存储器层次之间是以块的形式进行传递。存储器层次结构的本质是，每一层存储设备都是较低一层的缓存。为了利用空间局部性，存储器上的数据都是按块划分的，每个块包含多个字节的数据。第k层的缓存包含第k+1层块的一个子集的副本。数据总是以块为传送单位在第k层和第k+1层来回复制的。在层次结构中任何一对相邻的层次之间块大小是一样的。不过不同的层次之间块大小可以不同，一般而言，越靠近底层，越倾向于使用较大的块。

#### 缓存行

![image-20201027092252285](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2020/10/2020102709225252.png)

>   每个缓存里面都是由缓存行组成的，缓存系统中是以__缓存行（cache line）__为单位储存的。缓存行是2的整数次幂个连续的字节，一般为32~256个字节。__最常见的缓存行大小是64字节，并且它有效地引用主内存中的一块地址。当多线程修改相互独立的变量时，如果这些变量共同存储在同一个缓存行，就会无意中影响彼此的性能，这就是伪共享问题。__

#### CPU如何访问数据

当程序需要第k+1层的某个数据时，它首先在第k层的一个块中查找d，这里会出现两种情况：

-   缓存命中

    >   d刚好缓存在k层中，这里称之为缓存命中，该程序直接从k层读取d，根据存储器层次结构，这要比从k+1层取数据更快。

-   缓存不命中

    >   d没有缓存在k中，这种情况称之为缓存不命中，第k层的缓存从第k+1层中取出包含d的那个块，放在k层中，然后从k层读出d。这里涉及一个问题，即从k+1层中取出的块应如何放置在k层中，这里需要某种放置策略。可用的策略如下：

    -   随机放置，在k中随机选择一个位置进行放置，这种策略实现起来通常很昂贵，因为不好定位；

    -   分组放置，将第k+1层的某个块放置在第k层块的某个小组（子集）中；



### 缓存一致性协议

>   缓存一致性问题：在多核CPU场景下，以i++为例，i的初始值是0.那么在开始每个核都存储了i的值0，当第core1块做i++的时候，其缓存中的值变成了1，即使马上回写到主内存，那么在回写之后core2缓存中的i值依然是0，其执行i++，回写到内存就会覆盖第一块内核的操作，使得最终的结果是1，而不是预期中的2。

为了保证缓存内部数据的一致,不让系统数据混乱。这里就引出了一个一致性的协议MESI。

#### MESI

缓存一致性协议的一种实现，它将缓存行标记为四种状态：M、E、S、I

>   M(Modified)和E(Exclusive)状态的Cache line，数据是独有的，不同点在于M状态的数据是dirty的(和内存的不一致)，E状态的数据是clean的(和内存的一致)。
>
>   S(Shared)状态的Cache line，数据和其他Core的Cache共享。只有clean的数据才能被多个Cache共享。
>
>   I(Invalid)表示这个Cache line无效。

| 状态                 | 描述                                                         | 监听任务                                                     |
| -------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| M（Modified）修改的  | 该Cache line有效，数据被修改了，和内存中的数据不一致，数据只存在于本Cache中。 | 缓存行必须时刻监听所有试图读该缓存行相对就主存的操作，这种操作必须在缓存将该缓存行写回主存并将状态变成S（共享）状态之前被延迟执行。 |
| E（Exclusive）独享的 | 该Cache line有效，数据和内存中的数据一致，数据只存在于本Cache中。 | 缓存行也必须监听其它缓存读主存中该缓存行的操作，一旦有这种操作，该缓存行需要变成S（共享）状态。 |
| S（Shared）共享的    | 该Cache line有效，数据和内存中的数据一致，数据存在于很多Cache中。 | 缓存行也必须监听其它缓存使该缓存行无效或者独享该缓存行的请求，并将该缓存行变成无效（Invalid）。 |
| I（Invalid）无效的   | 该Cache line无效。                                           | 无                                                           |

__注意：__

>   对于M和E状态而言总是精确的，他们在和该缓存行的真正状态是一致的，而S状态可能是非一致的。如果一个缓存将处于S状态的缓存行作废了，而另一个缓存实际上可能已经独享了该缓存行，但是该缓存却不会将该缓存行升迁为E状态，这是因为其它缓存不会广播他们作废掉该缓存行的通知，同样由于缓存并没有保存该缓存行的copy的数量，因此（即使有这种通知）也没有办法确定自己是否已经独享了该缓存行。
>
>   从上面的意义看来E状态是一种投机性的优化：如果一个CPU想修改一个处于S状态的缓存行，总线事务需要将所有该缓存行的copy变成invalid状态，而修改E状态的缓存不需要使用总线事务。

##### E（exclusive）状态

>   只有Core 0访问变量x，它的Cache line状态为E(Exclusive)。

![image-20201027161734004](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2020/10/2020102716173434.png)

##### S（shared）状态

>   3个Core都访问变量x，它们对应的Cache line为S(Shared)状态。

![image-20201027161813020](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2020/10/2020102716181313.png)

##### M（modified）状态 & I（invalid）状态

>   Core 0修改了x的值之后，这个Cache line变成了M(Modified)状态，其他Core对应的Cache line变成了I(Invalid)状态。

![image-20201027161903317](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2020/10/202010271619033.png)

##### MESI状态转换

>   在MESI协议中，每个Cache的Cache控制器不仅知道自己的读写操作，而且也监听(snoop)其它Cache的读写操作。每个Cache line所处的状态根据本核和其它核的读写操作在4个状态间进行迁移。

![image-20201026173706242](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2020/10/202010261737066.png)

__图解前置说明：__

-   触发时间

    | 触发事件           | 描述                       |
    | ------------------ | -------------------------- |
    | LR（local read）   | 本地cache读取本地cache数据 |
    | LW（local write）  | 本地cache写入本地cache数据 |
    | RR（remote read）  | 其他cache读取本地cache数据 |
    | RW（remote write） | 其他cache写入本地cache数据 |

-   cache分类

    __前提__：所有的cache共同缓存了主存中的同一行数据

    -   本地cache：指当前cpu的cache。
    -   触发cache：触发读写事件的cache。
    -   其他cache：指既除了以上两种之外的cache。
    -   __注意__：本地的事件触发 本地cache和触发cache为相同。

__图解__

<table>
    <tr>
        <td>当前状态</td>
        <td>事件</td>
        <td>行为</td>
        <td>下一个状态</td>
    </tr>
    <tr>
        <td rowspan="4">M</td>
        <td>LR</td>
        <td>从Cache中取数据，状态不变</td>
        <td>M</td>
    </tr>
    <tr>
        <td>LW</td>
        <td>修改Cache中的数据，状态不变</td>
        <td>M</td>
    </tr>
    <tr>
        <td>RR</td>
        <td>这行数据被写到内存中，使其它核能使用到最新的数据，状态变成S</td>
        <td>S</td>
    </tr>
    <tr>
        <td>RW</td>
        <td>这行数据被写到内存中，使其它核能使用到最新的数据，由于其它核会修改这行数据，状态变成I</td>
        <td>I</td>
    </tr>
    <tr>
        <td rowspan="4">E</td>
        <td>LR</td>
        <td>从Cache中取数据，状态不变</td>
        <td>E</td>
    </tr>
    <tr>
        <td>LW</td>
        <td>修改Cache中的数据，状态变成M</td>
        <td>M</td>
    </tr>
    <tr>
        <td>RR</td>
        <td>数据和其它核共用，状态变成了S</td>
        <td>S</td>
    </tr>
    <tr>
        <td>RW</td>
        <td>数据被修改，本Cache line不能再使用，状态变成I</td>
        <td>I</td>
    </tr>
    <tr>
        <td rowspan="4">S</td>
        <td>LR</td>
        <td>从Cache中取数据，状态不变</td>
        <td>S</td>
    </tr>
    <tr>
        <td>LW</td>
        <td>修改Cache中的数据，状态变成M，其它核共享的Cache line状态变成I</td>
        <td>M</td>
    </tr>
    <tr>
        <td>RR</td>
        <td>状态不变</td>
        <td>S</td>
    </tr>
    <tr>
        <td>RW</td>
        <td>数据被修改，本Cache line不能再使用，状态变成I</td>
        <td>I</td>
    </tr>
    <tr>
        <td rowspan="4">I</td>
        <td>LR</td>
        <td>
            如果其它Cache没有这份数据，本Cache从内存中取数据，Cache line状态变成E；</br>
			如果其它Cache有这份数据，且状态为M，则将数据更新到内存，本Cache再从内存中取数据，2个Cache 的Cache line状态都变成S；</br>
			如果其它Cache有这份数据，且状态为S或者E，本Cache从内存中取数据，这些Cache 的Cache line状态都变成S；
		</td>
        <td>E/S</td>
    </tr>
    <tr>
        <td>LW</td>
        <td>
            从内存中取数据，在Cache中修改，状态变成M；</br>
			如果其它Cache有这份数据，且状态为M，则要先将数据更新到内存；</br>
			如果其它Cache有这份数据，则其它Cache的Cache line状态变成I；
        </td>
        <td>M</td>
    </tr>
    <tr>
        <td>RR</td>
        <td>既然是Invalid，别的核的操作与它无关</td>
        <td>I</td>
    </tr>
    <tr>
        <td>RW</td>
        <td>既然是Invalid，别的核的操作与它无关</td>
        <td>I</td>
    </tr>
</table>

>   下图示意了，当一个cache line的调整的状态的时候，另外一个cache line 需要调整的状态。

|      |  M   |  E   |  S   |  I   |
| :--: | :--: | :--: | :--: | :--: |
|  M   |  ×   |  ×   |  ×   |  √   |
|  E   |  ×   |  ×   |  ×   |  √   |
|  S   |  ×   |  ×   |  √   |  √   |
|  I   |  √   |  √   |  √   |  √   |

### 伪共享 & 缓存行对齐

#### 伪共享

>   当然如果两个独立的线程同时写两个不同的值会更糟。因为每次线程对缓存行进行写操作时，每个内核都要把另一个内核上的缓存块无效掉并重新读取里面的数据。你基本上是遇到两个线程之间的写冲突了，尽管它们写入的是不同的变量。这样情况会无意间影响彼此的性能，这就是___伪共享问题___。

![image-20201027091859085](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2020/10/2020102709185959.png)

图中说明了伪共享的问题。在核心1上运行的线程想更新变量X，同时核心2上的线程想要更新变量Y。不幸的是，这两个变量在同一个缓存行中。每个线程都要去竞争缓存行的所有权来更新变量。如果核心1获得了所有权，缓存子系统将会使核心2中对应的缓存行失效。当核心2获得了所有权然后执行更新操作，核心1就要使自己对应的缓存行失效。这会来来回回的经过L3缓存，大大影响了性能。如果互相竞争的核心位于不同的插槽，就要额外横跨插槽连接，问题可能更加严重。

#### 缓存行带来的锁竞争

处理器为了提高处理速度，不直接和内存进行通讯，而是先将系统内存的数据读到内部缓存（L1，L2或其他）后再进行操作，但操作完之后不知道何时会写到内存；如果对声明了Volatile变量进行写操作，JVM就会向处理器发送一条Lock前缀的指令，将这个变量所在缓存行的数据写回到系统内存。但是就算写回到内存，如果其他处理器缓存的值还是旧的，再执行计算操作就会有问题，所以**在多处理器下，为了保证各个处理器的缓存是一致的，就会实现缓存一致性协议**，每个处理器通过嗅探在总线上传播的数据来检查自己缓存的值是不是过期了，当处理器发现自己缓存行对应的内存地址被修改，就会将当前处理器的缓存行设置成无效状态，当处理器要对这个数据进行修改操作的时候，会强制重新从系统内存里把数据读到处理器缓存里。

__当多个线程对同一个缓存行访问时，其中一个线程会锁住缓存行，然后操作，这时候其他线程没办法操作缓存行。__

#### 避免伪共享（缓存行对齐）

##### JAVA语言

###### Java8之前

>   你会看到Disruptor消除这个问题，至少对于缓存行大小是64字节或更少的处理器架构来说是这样的（译注：有可能处理器的缓存行是128字节，那么使用64字节填充还是会存在伪共享问题）,通过增加补全来确保ring buffer的序列号不会和其他东西同时存在于一个缓存行中。
>
>   因此没有伪共享，就没有和其它任何变量的意外冲突，没有不必要的缓存未命中。

```java
@SuppressWarnings("all")
public class D02CacheLinePadding {
    private static class Super {
        volatile long l1, l2, l3, l4, l5, l6, l7;
    }
    private static class Test extends Super {
        public volatile long x = 0L;
    }
    public static Test[] arr = new Test[2];
    static {
        arr[0] = new Test();
        arr[1] = new Test();
    }
    public static void main(String[] args) throws Exception {
        Thread thread1 = new Thread(() -> {
            for (long l = 0; l < 1000_0000L; l++) {
                arr[0].x = l;
            }
        });
        Thread thread2 = new Thread(() -> {
            for (long l = 0; l < 1000_0000L; l++) {
                arr[1].x = l;
            }
        });
        final long begin = System.nanoTime();
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        System.out.println(System.nanoTime() - begin);
    }
}
```

######  Java8之后

>   实现字节填充避免伪共享 
>
>   -   JVM参数 -XX:-RestrictContended
>   -   @Contended 位于 sun.misc 用于注解java 属性字段，自动填充字节，防止伪共享

##### C语言

避免伪共享，编译器会自动将结构体，字节补全和对其，对其的大小最好是缓存行的长度。

总的来说，结构体实例会和它的最宽成员一样对齐。编译器这样做因为这是保证所有成员自对齐以获得快速存取的最容易方法。

从上面的情况可以看出，在设计[数据结构](https://links.jianshu.com/go?to=http%3A%2F%2Flib.csdn.net%2Fbase%2Fdatastructure)的时候，应该尽量将只读数据与读写数据分开，并具尽量将同一时间访问的数据组合在一起。这样 CPU 能一次将需要的数据读入。如：

![image-20201027093908929](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2020/10/202010270939088.png)

这样的数据结构就很不利。

在 X86 下，可以试着修改和调整它:

```
CACHE_LINE_SIZE – sizeof(int) + sizeof(name) * sizeof(name[0]) % CACHE_LINE_SIZE
```

看起来很不和谐，**CACHE_LINE_SIZE**表示高速缓存行为 64Bytes 大小。 __align 用于显式对齐。这种方式是使得结构体字节对齐的大小为缓存行的大小

![image-20201027094035922](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2020/10/2020102709403636.png)

### 合并写

>   当CPU执行写指令时，它首先会试图往L1上写，如果L1未命中，CPU就会使用另外一个缓存区，叫做合并写存储缓存区。合并写存储缓存只有 __四个位置__，极其昂贵。CPU会把待写入的数据写入到合并写存储缓存区，一起刷入到L2。

```java
public class D03WriteCombining {
    private static final int ITERATIONS = Integer.MAX_VALUE;
    private static final int ITEMS = 1 << 24;
    private static final int MASK = ITEMS - 1;

    private static final byte[] arrayA = new byte[ITEMS];
    private static final byte[] arrayB = new byte[ITEMS];
    private static final byte[] arrayC = new byte[ITEMS];
    private static final byte[] arrayD = new byte[ITEMS];
    private static final byte[] arrayE = new byte[ITEMS];
    private static final byte[] arrayF = new byte[ITEMS];

    public static void main(final String[] args) {
        for (int i = 1; i <= 3; i++) {
            System.out.println(i + " SingleLoop duration (ns) = " + runCaseOne());
            System.out.println(i + " SplitLoop  duration (ns) = " + runCaseTwo());
        }
    }
    public static long runCaseOne() {
        long start = System.nanoTime();
        int i = ITERATIONS;
        while (--i != 0) {
            int slot = i & MASK;
            byte b = (byte) i;
            arrayA[slot] = b;
            arrayB[slot] = b;
            arrayC[slot] = b;
            arrayD[slot] = b;
            arrayE[slot] = b;
            arrayF[slot] = b;
        }
        return System.nanoTime() - start;
    }
    public static long runCaseTwo() {
        long start = System.nanoTime();
        int i = ITERATIONS;
        while (--i != 0) {
            int slot = i & MASK;
            byte b = (byte) i;
            arrayA[slot] = b;
            arrayB[slot] = b;
            arrayC[slot] = b;
        }
        i = ITERATIONS;
        while (--i != 0) {
            int slot = i & MASK;
            byte b = (byte) i;
            arrayD[slot] = b;
            arrayE[slot] = b;
            arrayF[slot] = b;
        }
        return System.nanoTime() - start;
    }
}
```

### 参考链接

-   CPU缓存
    -   https://www.cnblogs.com/snsart/p/10700599.html
    -   https://blog.csdn.net/huayushuangfei/article/details/80717815
    -   https://baijiahao.baidu.com/s?id=1603241075982548149&wfr=spider&for=pc
-   MESI
    -   https://blog.csdn.net/m18870420619/article/details/82431319
    -   https://blog.csdn.net/muxiqingyang/article/details/6615199
    -   https://www.cnblogs.com/z00377750/p/9180644.html
    -   https://www.cnblogs.com/yanlong300/p/8986041.html
-   缓存行
    -   https://www.jianshu.com/p/e338b550850f
-   合并写
    -   https://www.cnblogs.com/liushaodong/p/4777308.html