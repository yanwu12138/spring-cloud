### jps（Java Virtual Machine Process Status Tool）

用来查看基于`HotSpot`的`JVM`里面中，所有具有访问权限的`Java`进程的具体状态, 包括进程`ID`，进程启动的路径及启动参数等等，与`unix`上的`ps`类似，只不过`jps`是用来显示`java`进程，可以把`jps`理解为`ps`的一个子集

使用`jps`时，如果没有指定`hostid`，它只会显示本地环境中所有的`Java`进程；如果指定了`hostid`，它就会显示指定`hostid`上面的`java`进程，不过这需要远程服务上开启了`jstatd`服务，可以参看前面的`jstatd`章节来启动`jstad`服务



#### 命令格式

```shell
jps [ options ] [ hostid ]
```

![image-20210602171637213](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/06/2021060217163737.png)

#### 参数

| 参数 | 说明                                                         |
| ---- | ------------------------------------------------------------ |
| -q   | 忽略输出的类名、`Jar`名以及传递给`main`方法的参数，只输出`pid` |
| -m   | 输出传递给`main`方法的参数，如果是内嵌的`JVM`则输出为`null`  |
| -l   | 输出应用程序主类的完整包名，或者是应用程序`JAR`文件的完整路径 |
| -v   | 输出传给`JVM`的参数                                          |
| -V   | 输出通过标记的文件传递给`JVM`的参数（`.hotspotrc`文件，或者是通过参数`-XX:Flags=<filename>`指定的文件） |
| -J   | 用于传递`jvm`选项到由`javac`调用的`java`加载器中，例如，`“-J-Xms48m”`将把启动内存设置为`48M`，使用`-J`选项可以非常方便的向基于`Java`的开发的底层虚拟机应用程序传递参数 |

#### 服务器标识

`hostid`指定了目标的服务器，它的语法如下：

-   `protocol`：如果`protocol`及`hostname`都没有指定，那表示的是与当前环境相关的本地协议，如果指定了`hostname`却没有指定`protocol`，那么`protocol`的默认就是`rmi`
-   `hostname`：服务器的`IP`或者名称，没有指定则表示本机。
-   `port`：远程`rmi`的端口，如果没有指定则默认为`1099`。
-   `Servername`：注册到`RMI`注册中心中的`jstatd`的名称。

```shell
[protocol:][[//]hostname][:port][/servername]
```

