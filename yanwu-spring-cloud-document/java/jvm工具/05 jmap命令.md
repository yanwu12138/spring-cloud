### jmap（Java Memory Map）

`JVM Memory Map`命令用于生成`heap dump`文件，如果不使用这个命令，还可以使用`-XX:+HeapDumpOnOutOfMemoryErro`r参数来让虚拟机出现`OOM`的时候自动生成`dump`文件。`jmap`不仅能生成`dump`文件，还可以查询`finalize`执行队列、`Java`堆和老年代的详细信息，如当前使用率、当前使用的是哪种收集器等。

### 参数

![image-20210602164016569](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/06/2021060216401616.png)

-   `option`：选项参数，不可同时使用多个选项参数
    -   `dump`：生成堆转储快照
    -   `heap`：显示`Java`堆详细信息
    -   `finalizerinfo`：显示在`F-Queue`队列等待`Finlizer`线程执行`finalizer`方法的对象
    -   `histo`：线下堆中对象的统计信息
    -   `clstats`：`Java`堆中内存的类加载器的统计信息
    -   `F`：当`-dump`没有响应时，强制生成`dump`快照
-   `pid`：`Java`进程`id`
-   `executable`：产生核心`dump`的`Java`可执行文件
-   `core`：需要打印配置信息的核心文件
-   `remote-hostname-or-ip`：远程调试的主机名或`ip`
-   `server-id`：可选的唯一`id`，如果相同的远程主机上运行了多台调试服务器，用此选项参数标示服务器

### 用法

#### -dump

这个命令执行，JVM会将整个heap的信息dump写入到一个文件，heap如果比较大的话，就会导致这个过程比较耗时，并且执行的过程中为了保证dump的信息是可靠的，所以会暂停应用

```shell
##### dump堆到文件；format指定输出格式；live指明是活着的对象；file指定文件名。
jmap -dump:live,format=b,file=dump.hprof {PID}
```

![image-20210602164350396](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/06/2021060216435050.png)

可以使用`MAT`进行分析

#### -heap

打印`heap`的概要信息，`GC`使用的算法，`heap`的配置和使用情况，可以用此来判断内存目前的使用情况以及垃圾回收情况

```shell
##### ./jmap -heap 10910
[admin@yanwu bin]$ ./jmap -heap 10910
Attaching to process ID 10910, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.181-b13

using parallel threads in the new generation.
using thread-local object allocation.
Concurrent Mark-Sweep GC

Heap Configuration:
   MinHeapFreeRatio         = 40
   MaxHeapFreeRatio         = 70
   MaxHeapSize              = 2147483648 (2048.0MB)
   NewSize                  = 1073741824 (1024.0MB)
   MaxNewSize               = 1073741824 (1024.0MB)
   OldSize                  = 1073741824 (1024.0MB)
   NewRatio                 = 2
   SurvivorRatio            = 10
   MetaspaceSize            = 268435456 (256.0MB)
   CompressedClassSpaceSize = 528482304 (504.0MB)
   MaxMetaspaceSize         = 536870912 (512.0MB)
   G1HeapRegionSize         = 0 (0.0MB)

Heap Usage:
New Generation (Eden + 1 Survivor Space):
   capacity = 984285184 (938.6875MB)
   used     = 542846408 (517.6986770629883MB)
   free     = 441438776 (420.9888229370117MB)
   55.151333863824576% used
Eden Space:
   capacity = 894828544 (853.375MB)
   used     = 537178160 (512.2930145263672MB)
   free     = 357650384 (341.0819854736328MB)
   60.03140641879211% used
From Space:
   capacity = 89456640 (85.3125MB)
   used     = 5668248 (5.405662536621094MB)
   free     = 83788392 (79.9068374633789MB)
   6.336307735233516% used
To Space:
   capacity = 89456640 (85.3125MB)
   used     = 0 (0.0MB)
   free     = 89456640 (85.3125MB)
   0.0% used
concurrent mark-sweep generation:
   capacity = 1073741824 (1024.0MB)
   used     = 730627744 (696.7809143066406MB)
   free     = 343114080 (327.2190856933594MB)
   68.04501116275787% used

63052 interned Strings occupying 7302440 bytes.
```

#### -finalizerinfo

打印等待回收的对象信息

```shell
##### Number of objects pending for finalization:0 说明当前F-Queue队列中并没有等待Finalizer线程执行finalizer方法的对象
[admin@yanwu bin]$ ./jmap -finalizerinfo 10910
Attaching to process ID 10910, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.181-b13
Number of objects pending for finalization: 0
```

#### -histo

打印堆的对象统计，包括对象数、内存大小等。jmap -histo:live这个命令执行，**JVM会先触发gc**，然后再统计信息

```shell
##################################
# num 			# 编号id
# instances 	# 实例个数
# bytes 		# 所有实例大小
# class name 	# 类名
##################################
[admin@yanwu bin]$ ./jmap -histo:live 10910 | more
 num     #instances         #bytes  class name
----------------------------------------------
   1:        195822      574989536  [C
   2:         81988        7214944  java.lang.reflect.Method
   3:        188380        6028160  java.util.concurrent.ConcurrentHashMap$Node
   4:        193607        4646568  java.lang.String
   5:         25560        2849256  java.lang.Class
   6:         69309        2772360  java.util.LinkedHashMap$Entry
   7:         49471        2374608  org.aspectj.weaver.reflect.ShadowMatchImpl
   8:         26906        2230992  [Ljava.util.HashMap$Node;
   9:         68042        2177344  java.util.HashMap$Node
  10:         10372        2170760  [B
```

#### -clstats

打印`Java`类加载器的智能统计信息，对于每个类加载器而言，对于每个类加载器而言，它的名称，活跃度，地址，父类加载器，它所加载的类的数量和大小都会被打印。此外，包含的字符串数量和大小也会被打印

```shell
[admin@iZuf66cdvzk4ri8fdu57xaZ bin]$ ./jmap -clstats 10910
Attaching to process ID 10910, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.181-b13
finding class loader instances ..done.
computing per loader stat ..done.
please wait.. computing liveness.liveness analysis may be inaccurate ...
class_loader	classes	bytes	parent_loader	alive?	type

<bootstrap>	3620	6285646	  null  	live	<internal>
0x00000000c3addba8	1	880	0x00000000c0000000	dead	sun/reflect/DelegatingClassLoader@0x000000010000a028
0x00000000c49f7498	1	1473	0x00000000c0000000	dead	sun/reflect/DelegatingClassLoader@0x000000010000a028
0x00000000c0ebb0e0	1	880	0x00000000c0000000	dead	sun/reflect/DelegatingClassLoader@0x000000010000a028
0x00000000c4c464c8	1	1473	0x00000000c0000000	dead	sun/reflect/DelegatingClassLoader@0x000000010000a028
```



### 注意

__`-dump`会导致应用暂停、`-histo`会触发`GC`，所以这两个命令在生产环境应当尽量避免使用__