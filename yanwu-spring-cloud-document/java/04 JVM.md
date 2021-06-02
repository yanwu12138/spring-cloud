# Java从编译到运行
.java文件通过javac指令编辑成.class文件，然后通过JVM虚拟机编译成硬件可执行的二进制码

![clipboard](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/20200628133516.png)

## class文件格式
通过javap命令或idea插件jclasslib bytecode viewer查看

### [class文件组成](../xmind/CLASS文件结构.xmind)

## 类加载 & 初始化

### 类加载器
类加载器由高到低分为四种：Bootstrap >> Extension >> Application >> CustomClassLoader，通过类的全路径名称调用getClassLoader()函数获取类加载器
- Bootstrap：加载 lib/rt.jar charset.jar 等核心类，通过C++实现
- Extension：加载扩展包 jre/lib/ext/*.jar 或由-Djava.ext.dirs指定
- Application：加载classpath指定内容
- CustomClassLoader：自定义的ClassLoader
```java
public class D02ClassLoader {
    public static void main(String[] args) {
        // ----- null > Bootstrap
        System.out.println(String.class.getClassLoader());
        // ----- sun.misc.Launcher$ExtClassLoader > Extension
        System.out.println(sun.net.spi.nameservice.dns.DNSNameService.class.getClassLoader());
        // ----- sun.misc.Launcher$AppClassLoader > Application
        System.out.println(com.yanwu.spring.cloud.common.demo.d04jvm.j01class.D02ClassLoader.class.getClassLoader());
        // ----- null > Bootstrap
        System.out.println(com.yanwu.spring.cloud.common.demo.d04jvm.j01class.D02ClassLoader.class.getClassLoader().getClass().getClassLoader());
    }
}
```

#### JVM采用双亲委派制：
- parent方向：自底向上检查该类是否已经加载
- child方向：自顶向下进行实际查找和加载
![clipboard](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/20200628133614.png)
```java
protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
    // 同步上锁
    synchronized (getClassLoadingLock(name)) {
        // 先查看这个类是不是已经加载过
        Class<?> c = findLoadedClass(name);
        if (c == null) {
            long t0 = System.nanoTime();
            try {
                // 递归，双亲委派的实现，先获取父类加载器，不为空则交给父类加载器
                if (parent != null) {
                    c = parent.loadClass(name, false);
                    // 前面提到，bootstrap classloader的类加载器为null，通过find方法来获得
                } else {
                    c = findBootstrapClassOrNull(name);
                }
            } catch (ClassNotFoundException e) {
            }
            if (c == null) {
                // 如果还是没有获得该类，调用findClass找到类
                long t1 = System.nanoTime();
                c = findClass(name);
                // jvm统计
                sun.misc.PerfCounter.getParentDelegationTime().addTime(t1 - t0);
                sun.misc.PerfCounter.getFindClassTime().addElapsedTimeFrom(t1);
                sun.misc.PerfCounter.getFindClasses().increment();
            }
        }
        // 连接类
        if (resolve) {
            resolveClass(c);
        }
        return c;
    }
}
```

#### 为什么用双亲委派制
- 主要是为了安全
- 次要是为了避免资源浪费，已经加载的类不会重复加载

#### 破坏双亲委派
- 场景：热部署

### 加载过程
- class loading：通过双亲委派进行类的加载
- class linking
    - verification：验证文件是否符合JVM规定
    - preparation：__静态成员变量__ 赋默认值
    - resolution：将类、方法、属性等符号引用解析为直接引用
- initializing：调用类初始化代码（init函数），__静态成员变量__ 顺序赋初始值
- 申请对象内存
- __成员变量__ 赋默认值
- 调用构造方法
    - __成员变量__ 顺序赋初始值
    - 执行构造方法语句

```java
/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-06-03 20:25:33.
 * <p>
 * describe:
 * 类加载时静态你成员变量的赋值过程：
 * * 1. 将class对象加载到内存中
 * * 2. 给class对象的静态成员变量赋默认值
 * * 3. 给class对象的静态成员变量赋初始值
 */
public class D03ClassLoaderProcedure {
    public static void main(String[] args) {
        // ----- 结果为：3
        System.out.println("count01 -- " + Count_01.count);
        // ----- 结果为：2
        System.out.println("count02 -- " + Count_02.count);
    }
    /**
     * 1. 将 Count_01 对象加载到内存
     * 2. 给 count 属性赋默认值：0
     * 3. 给 count_01 属性赋默认值：null
     * 4. 给 count 属性赋初始值：2
     * 5. 给 count_01 属性对象赋初始值：new Count_01()
     * 注意：此时的 count 值为：2，在第 5 步给 count_01 属性赋初始值时会调用 Count_01 的构造方法
     * 所以：count 会 ++，最终 count 值为：3
     */
    private static class Count_01 {
        public static int count = 2;
        public static Count_01 count_01 = new Count_01();
        private Count_01() {
            count++;
        }
    }
    /**
     * 1. 将 Count_02 对象加载到内存
     * 2. 给 count_02 属性赋默认值：null
     * 3. 给 count 属性赋默认值：0
     * 4. 给 count_02 属性赋初始值：new Count_02()
     * 5. 给 count 属性赋默认值：2
     * 注意：在第 4 步给 count_02 属性赋初始值时会调用 Count_02 的构造方法，此时的 count 还没有赋初始值，只有默认值，所以此时 count为：0，count++为：1
     * 在第 5 步给count 属性赋默认值时会用 2 覆盖 count++：1 的值，所以 count 最终值为：2
     */
    private static class Count_02 {
        public static Count_02 count_02 = new Count_02();
        public static int count = 2;
        private Count_02() {
            count++;
        }
    }
}
```

#### 对象的半初始化
new 对象的时候其实是依次执行了：new >> dup >> invokspecial >> store这几个指令

# java内存模型（JMM）
当CPU需要读取硬盘上的数据进行计算时，会依次检查 L1 > L2 > L3 > 内存 这几块区域中是否有需要的数据，如果有，则直接返回来用，并且会在各级缓存中都缓存一份。而CPU缓存的基本单位是64个字节一行，这64个字节就被称为 __缓存行__ 。<br>
由于CPU的L1和L2两级缓存都是私有的，所以很可能会产生缓存数据不一致的问题。<br>

## （一致性）硬件层的缓存数据一致性
现代CPU的缓存数据一致性一般都是通过 __缓存锁__ 和 __总线锁__ 一起使用来保证<br>

- 总线锁：将整条总线锁住，只能让一个CPU操作3级缓存或主存，达到数据同步的目的，但是总线锁的效率相对来说比较低
- 缓存锁：有很多种，由于inter处理器使用的使[MESI](../操作系统/CPU缓存&缓存一致性.md)，所以一般的说到缓存锁都是只MESI，但其实是不同的处理器缓存所实现是不一样的，MESI指CPU中每个缓存行使用两位来标识缓存行的状态，一共四种状态
    - __Modified(被修改的)__: 该缓存行只被缓存在该CPU中，并且是被修改过的，既与主存中的数据不一致，该缓存行中的内存需要在未来的某个时间点写回主存。当背写回主存之后，该缓存行的状态会从modified变为exclusive状态。
    - __Exclusive(独享的)__: 该缓存行只被缓存在该CPU中，它是没有被修改过的，与主存中的数据一致，该状态可以在任何时刻当有其他CPU读取该内存时变为shared状态。当CPU修改该缓存行时，该缓存行状态会从exclusive变为modified状态。
    - __Shared(共享的)__: 该状态表示该缓存行可能被多个CPU缓存，并且各个缓存中的数据与主存一致。当有一个CPU修改缓存行时，其它CPU中该缓存行变为invalid状态
    - __Invalid(无效的)__: 该缓存是无效的，可能被其它CPU修改了缓存行

##### [伪共享问题](../操作系统/CPU缓存&缓存一致性.md)
位于同一缓存行的两个不同数据被两个不同的CPU锁定，产生互相影响的
> 可以通过 __缓存行对齐__ 解决伪共享问题，但是会稍微浪费一些空间

##### [合并写](../操作系统/CPU缓存&缓存一致性.md)

当CPU执行写指令时，它首先会试图往L1上写，如果L1未命中，CPU就会使用另外一个缓存区，叫做合并写存储缓存区。合并写存储缓存只有 __四个位置__，极其昂贵。CPU会把待写入的数据写入到合并写存储缓存区，一起刷入到L2。

## [（有序性）指令执行顺序](../操作系统/乱序执行&指令重排.md)
__乱序问题__：CPU为了提高效率，会在一条指令执行过程中（比如去内存读取数据），去同时执行其它的指令，前提是两条指令没有依赖关系

乱序执行的证明：
#### 如何保证特定情况下的不乱序
- 加锁（硬件层面上的lock指令）
- CPU级别内存屏障(X86)
    - sfence（写屏障[save]）：在sfence指令前的写操作当必须在sfence指令后的写操作前完成
    - lfence（读屏障[load]）：在lfence指令前的读操作当必须在lfence指令后的读操作前完成
    - mfence（全能屏障[mix]）：在mfence指令前的读写操作当必须在mfence指令后的读写操作前完成
- JVM级别内存屏障（只是规范，各种虚拟机有自己的实现）
    - LoadLoadp：l1; LoadLoad; l2; > 在l2及后续读取操作执行之前，保证l1要读取的数据被读取完毕
    - StoreStore：s1; StoreStore; s2; > 在s2及后续写入操作执行之前，保证s1的写入操作对其它处理器可见
    - LoadStore：l1; LoadStore; s2; > 在s2及后续写入操作执行之前，保证l1要读取的数据被读取完毕
    - StoreLoad：s1; StoreLoad; l2; > 在l2及后续读取操作执行之前，保证s1的写入操作对其它处理器可见

#### Java八大原子操作
| 指令   | 含义 | 作用域   | 描述                                                         |
| ------ | ---- | -------- | ------------------------------------------------------------ |
| lock   | 锁定 | 主存     | 它把一个变量标记为一条线程独占状态                           |
| read   | 读取 | 主存     | 它把变量从主存传送到线程的工作内存中                         |
| load   | 载入 | 工作内存 | 它把read操作的值放入工作内存中的变量副本中                   |
| use    | 使用 | 工作内存 | 它把工作内存中的值传递给执行引擎，每次虚拟机遇到一个需要使用变量的时候就会执行该操作 |
| assign | 赋值 | 工作内存 | 它把从执行引擎获取的值赋值给工作内存中的变量，每次虚拟机遇到一个需要赋值变量的时候就会执行该操作 |
| store  | 存储 | 工作内存 | 它把工作内存在中的一个变量传送到主存中                       |
| write  | 写入 | 主存     | 它把store传送值放到主存中的变量中                            |
| unlock | 解锁 | 主存     | 它将一个处于锁定状态的变量释放出来                           |

不管如何重排序，单线程执行结果不会改变

## Object的内存布局
> 通过 java -XX:+PrintCommandLineFlags -version 查看JVM虚拟机配置

#### 普通对象
1. 对象头：markword >> 8字节
2. ClassPointer指针：指向Class对象，配置-XX:+UseCompressedClassPointers为4个字节，不开启为8个字节
3. 实例数据：引用类型：配置-XX:+UseCompressedOops为4个字节，不开启为8个字节
4. Padding对齐，8的倍数

#### 数组对象
1. 对象头：markword >> 8字节
2. ClassPointer指针：指向Class对象
3. 数组长度：4个字节
4. 数组数据：实例数据
5. Padding对齐：8的倍数

### 对象头内容：根据对象的状态而变
#### markwork：
```bash
  64 bits:
  --------
  unused:25 hash:31 -->| unused:1   age:4    biased_lock:1 lock:2 (normal object)
  JavaThread*:54 epoch:2 unused:1   age:4    biased_lock:1 lock:2 (biased object)
  PromotedObject*:61 --------------------->| promo_bits:3 ----->| (CMS promoted object)
  size:64 ----------------------------------------------------->| (CMS free block)
 
  unused:25 hash:31 -->| cms_free:1 age:4    biased_lock:1 lock:2 (COOPs && normal object)
  JavaThread*:54 epoch:2 cms_free:1 age:4    biased_lock:1 lock:2 (COOPs && biased object)
  narrowOop:32 unused:24 cms_free:1 unused:4 promo_bits:3 ----->| (COOPs && CMS promoted object)
  unused:21 size:35 -->| cms_free:1 unused:7 ------------------>| (COOPs && CMS free block)
 [ptr             | 00]  locked             ptr points to real header on stack
 [header      | 0 | 01]  unlocked           regular object header
 [ptr             | 10]  monitor            inflated lock (header is wapped out)
 [ptr             | 11]  marked             used by markSweep to mark an object
```

##### markwork的五种状态：
|   状态   | 锁标识位 | 偏向锁标识位 | 存储内容                           |
| :------: | :------: | :----------: | :--------------------------------- |
| 无锁状态 |    0     |      01      | hashCode：31<br>年龄：4            |
|  偏向锁  |    1     |      01      | 线程ID：54<br>时间戳：2<br>年龄：4 |
| 轻量级锁 |    无    |      00      | 栈中锁记录的指针：64               |
| 重量级锁 |    无    |      10      | monitor的指针：64                  |
|  GC标记  |    无    |      11      | 空，不需要记录信息                 |

### 对象定位
1. 句柄池：分为两部分：第一部分指向对象实例，第二部分指向class对象
2. 直接指针：直接指向对象实例，由对象实例指向class对象

# JVM运行时内存区

## 程序计数器（PC）
> __线程私有__：存放指令位置，在CPU切换的时候，记录当前执行的位置，等CPU切回来的时候，取PC中记录的位置继续执行

## JVM栈
> __线程私有__：里面装的是栈帧，每个函数对应一个栈帧，当方法被执行时，会将对应的栈帧进行压栈，当函数执行完之后，该栈帧会弹栈

#### 栈帧：由四个部分组成：局部变量表、操作数栈、动态链接、返回值地址
- 局部变量表(local variable table)：函数的入参和函数内部使用的局部变量，记录在constant-pool中，类似于寄存器
- 操作数栈(operand stack)：
- 动态链接(dynamic linking)：指向class constant-pool常量池中的符号链接，看对应的链接是都已经被解析，如果没解析就进行动态解析，如果解析了就直接拿过来使用
- 返回值地址(return address)：a() -> b()：如果有返回值，标识着b方法的返回值存放在什么以及a()方法接下来应该执行那个位置

## 本地方法栈
> __线程私有__：

## 方法区（MethodArea）
> __线程共享__：有以下两个版本的不同实现
1. perm space（ < 1.8）：字符串常量位于permSpace；FGC不会清理；大小启动的时候指定不能变；
2. meta space（ > 1.8）：字符串常量位于堆；会出发FGC清理；不设定的话最大默认是物理内存；

## 直接内存（1.4增加）
> 直接归属操作系统管理，JVM可以直接访问的内核空间的内存（OS管理的内存），使用于NIO（零拷贝）

## 堆
> __线程共享__：
### 堆内存逻辑分区（不适用不分代垃圾收集器）
> 除Epsilon、ZGC、Shenandoah之外的GC都是使用逻辑分代模型
> G1是逻辑分代，物理不分代；除此之外的所有垃圾回收器不仅逻辑分代，而且物理分代

![clipboard](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/20200628133741.png)

- 新生代：存储刚刚new出来的对象，分为一个eden区和两个survivor区（8:1:1）
    - eden区：刚刚new出来的对象放置的区域，经过一次垃圾回收则被移动到survival区
    - survivor区：经过垃圾回收时在两个survivor区来回移动，当年龄达到一定的大小时移动到老年代
- 老年代：经过多次垃圾回收都没有被回收掉的对象，被放置到老年代
- 永久代：methodArea（方法区）：永久代存放的是Class元数据、JIT编译好的类信息等
    - JDK1.7：perm generation：必须限制大小，容易溢出
    - JDK1.8：Metaspace：可以设置也可以不设置，不设置的话受限于物理内存

#### 对象分配过程
> 一个对象产生后，会首先进行栈上分配，当栈内存不够时，会进入eden区，然后经过一次垃圾回收之后，进入survivor区，然后每经过一次垃圾回收，会在两个survivor区来回移动，当垃圾回收达到一定次数【通过-XX:MaxTenuringThreshold指定，最大15次】之后，进入old区，__老年代和新生代默认比例为 2:1__
> - 栈上分配：
>     - 线程私有的小对象
>     - 无逃逸：只会在某个区域或代码块使用，不会被其它地方引用
>     - 支持标量替换：用普通的类型来代替整个对象
>     - 无需调整
> - 线程本地分配TLAB（thread local allocation buffer）：
>     - 占用eden区，默认1%
>     - 多线程的时候不用竞争eden就可以申请空间，提高效率
>     - 小对象
>     - 无需调整
> - 老年代：
>     - 大对象

![clipboard](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/20200628133831.png)

#### 对象何时进入老年代
- 超过XX:MaxTenuringThreshold指定次数（YGC）：最大是15次，因为markword中的age标志是4位
- 动态年龄：s0 -> s1超过50%，把年龄最大的放入old

| 分代   | 算法               | 原因                               |
| ------ | ------------------ | ---------------------------------- |
| 新生代 | 拷贝算法           | 新生代大量死去，少量存活，回收较多 |
| 老年代 | 标记清除或标记压缩 | 老年代存活率较高，回收较少         |

### [常见垃圾回收器](../xmind/垃圾回收.xmind)
#### 垃圾回收器的发展历程
> - JDK诞生Serial追随，由于Serial是单线程的，所以效率比较低
> - 为了提高效率，诞生了PS，PS是多线程，但也会有STW问题
> - 因为无法忍受STW，诞生了CMS，为了配合CMS，诞生了PN。CMS是1.4后期版本引入，它是里程碑式的GC，它开启了并发回收的过程，但是它的问题较多，因此目前没有任何一个JDK版本默认是CMS，只能手动指定

##### 垃圾回收器和内存大小的关系：和服务器内存增长有直接密切的关系
- Serial：几十M
- PS：上百M～几个G
- CMS：20G左右
- G1：上百G
- ZGC：4T

### GC
### 如何判断对象已死
对象已死的意思是指该对象不可能再被任何途径使用，判断对象已死的方式有：
- 引用计数算法
    - 引用计数算法是指给对象添加一个引用计数器，每当有一个地方引用它，该计数器就加1；当饮用失效时，该计数器就减1。当计数器是0时，说明该对象没有被引用。但引用计数算法无法解决 __循环引用__ 的问题。
- 可达性分析算法
    - 可达性分析算法基本思路就是通过一系列称为GCRoots的对象作为起始点，从这些节点乡下搜索，搜索所走过的路径称之为引用链，当一个对象到GCRoots没有任何引用链可以到达时，说明该对象不可用，在Java中，可GCRoots对象的有以下几种：
        - 线程栈变量：虚拟机栈中引用的对象
        - 静态变量：方法区中类静态属性引用的对象
        - 常量池：方法区中常量引用的对象
        - JNI指针：本地方法栈中native函数引用的对象
- card table（卡表）：
    - 由于在做YGC时，如果新生代的对象被老年代中的对象引用了，那么使用可达性分析算法需要扫描整个old区，效率非常低，所以JVM设计了CardTable，它将新生代和老年代分为一个个的card，具体的对象存储在一个个的card中，如果有一个card从老年代指回了新生代，那么对应的card会被标记为dirty，下次扫描是只需要扫描dirty card，在结构上。它通过位图BitMap来实现
        - G1特有的：
            - CSet(CollectionSet)：G1将它需要被回收的那些card存储到一个区域中，该区域就是CSet，当需要进行垃圾回收时，可以直接到CSet中找
            - RSet(RememberedSet)：每一个region中都有一个区域记录了其它region中的对象到本region中的引用，这个区域就是RSet，其价值在于垃圾回收器不需要扫描整个堆栈找到谁引用了当前分区中的对象，只需要扫描RSet即可

### GC清除算法：标记清除、拷贝、标记压缩
- mark-sweep：标记清除
    - 从GC Roots开始，找到不可回收的对象，标记出来，然后将其它可回收的对象回收掉
    - 适用于存活对象比较多的情况下
    - 需要经过两次扫描（第一次找到不可回收对象进行标记，第二次回收可回收对象），效率偏低；容易产生碎片

![clipboard](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/20200628133853.png)

- copying：拷贝
    - 将内存一分为二，然后将不可回收对象都拷贝到同一块内存区域，然后将另一块内存区域统一回收；只需要扫描一次，效率较高
    - 适用于存活对象比较少的情况下
    - 需要移动复制对象，需要调整对象的引用；会有造成内存减半问题

![clipboard](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/20200628133922.png)

- mark-compact：标记压缩
    - 从GC Roots开始，找到不可回收的对象，然后将不可回收的对象移动到同一块内存区域，然后将另一块对象统一回收
    - 不会产生碎片，方便对象分配，不会产生内存减半
    - 需要经过两次扫描（第一次找到不可回收的对象进行标记，第二次将它们移动到同一块内存区域），需要移动对象，相对效率最低（一边标记一边整理）

![clipboard](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/20200628133953.png)

### 并发标记算法
##### 三色标记：在逻辑上将对象分成三种颜色[黑色、灰色、白色]
![clipboard](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/20200628134012.png)
- 黑色：自身已经标记完了，其成员变量也已经标记完了
- 灰色：自身已经标记完了，但是成员变量没有被标记
- 白色：未被标记的对象

> __漏标__：漏标是指本来是可用对象，但是由于没有遍历到被当成垃圾回收掉了，当发生一下两种情况时，会产生漏标：
> 1. 并发标记进行时增加了一个黑到白的引用，如果不重新对黑色进行处理，则会漏标
> 2. 并发标记进行时删除了灰到白对象的引用，那么这个白色对象有可能会被漏标

> 漏标的 __解决方案__ 就是是打破上述两个条件其中的任何一项即可：
> - __Incremental Update【CMS使用】__：关注引用的增加，把黑色重新标记为灰色，下次重新扫描属性，但incrementalUpdate有一个问题，就是JVM不知道那些引用是新增的，所以需要扫描整个堆中所有的灰色对象下所有的属性才行，效率比较低
> - __SATB【G1使用】__：关注引用的删除，当灰色对象到白色对象的引用消失时，要把这个 __引用__ 推到GC的堆栈，保证白色对象还能被GC扫描到

> __难点__：为什么G1使用SATB？
>
> - 因为灰色对象到白色对象的引用消失时，如果没有黑色指向白色引用则会被push到GC堆栈，下次扫描时拿到这个引用，由于有RSet的存在，不需要扫面整个堆去查找指向白色的引用，效率较高【SATB + RSet】

#### 颜色指针：

### 垃圾收集器参数
| 参数                   | 描述                                                         |
| ---------------------- | ------------------------------------------------------------ |
| UseSerialGC            | 使用 Serial + SerialOld 组合进行垃圾回收                     |
| UseParNewGC            | 使用 ParNew + SerialOld 组合进行垃圾回收                     |
| UseConcMarkSweepGC     | 使用 ParNew + CMS + SerialOld 组合进行垃圾回收，SerialOld作为CMS的后备 |
| UseParallelGC          | 使用 PS + SerialOld 组合进行垃圾回收                         |
| UseParallelOldGC       | 使用 PS + PO 组合进行垃圾回收                                |
| SurvivalRatio          | 新生代中 eden 与 survival 区的容量比值，默认为 8:1:1         |
| PretenureSizeThreshold | 直接晋升到老年代的对象的大小，大于该阈值的对象，会直接晋升到老年代 |
| MaxTenuringThreshold   | 直接晋升到老年代的年龄，年龄超过该阈值的对象，会直接晋升到老年代 |
| UseAdaptiveSizePolicy  | 动态调整java堆中各个区域的大小以及进入老年代的年龄           |
| ParallelGCThreads      | 设置并行GC时进行内存回收的线程数                             |

## 引用
引用分为：__强引用__ & __软引用__ & __弱引用__ & __虚引用__
- 强引用：只要强引用还在，垃圾收集器就不可能回收掉被引用的对象 __（NormalReference）__
- 软引用：只有在系统将要发生内存溢出之前，才会把软引用指向的对象列进回收范围之中进行第二次回收。如果软内存引用被回收后内存还不够，才会抛出内存溢出异常 __（SoftReference）__
- 弱引用：弱引用只要遭遇垃圾回收就会被回收掉只被弱引用的对象，除非该对象还被其它强引用所指向 __（WeakReference）__
- 虚引用：又称幽灵引用或幻影引用，它是最弱的一种引用关系，一个对象设置虚引用的 __唯一目的__ 就是能在这个对象被收集器回收时收到一个系统通知 __（PhantomReference）__，虚引用必须结合ReferenceQueue使用，主要用来管理堆外内存