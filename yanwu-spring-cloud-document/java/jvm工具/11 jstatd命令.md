### jstatd（Java Statistics Monitoring Daemon）

`jstatd`是一个基于`RMI（Remove Method Invocation）`的服务程序，它用于监控基于`HotSpot`的`JVM`中资源的创建及销毁，并且提供了一个远程接口允许远程的监控工具连接到本地的`JVM`执行命令。

`jstatd`是基于`RMI`的，所以在运行`jstatd`的服务器上必须存在`RMI`注册中心，如果没有通过选项`"-p port"`指定要连接的端口，`jstatd`会尝试连接`RMI`注册中心的默认端口。后面会谈到如何连接到一个默认的`RMI`内部注册中心，如何禁止默认的`RMI`内部注册中心的创建，以及如何启动一个外部注册中心。

#### 参数

-   -nr：如果`RMI`注册中心没有找到，不会创建一个内部的`RMI`注册中心
-   -p：`port RMI`注册中心的端口号，默认为`1099`
-   -n：`rminame` 默认为`JStatRemoteHost`；如果同一台主机上同时运行了多个`jstatd`服务，`rminame`可以用于唯一确定一个`jstatd`服务；这里需要注意一下，如果开启了这个选项，那么监控客户端远程连接时，必须同时指定`hostid`及`vmid`，才可以唯一确定要连接的服务，这个可以参看`jps`章节中列出远程服务器上`Java`进程的示例
-   -J：用于传递`jvm`选项到由`javac`调用的`java`加载器中，例如，`“-J-Xms48m”`将把启动内存设置为`48M`，使用-J选项可以非常方便的向基于`Java`的开发的底层虚拟机应用程序传递参数

#### 安全性

`jstatd`服务只能监视具有适当的本地访问权限的`JVM`，因此`jstatd`进程与被监控的`JVM`必须运行在相同的用户权限中。但是有一些特殊的用户权限，如基于`UNIX（TM）`为系统的`root`用户，它有权限访问系统中所有`JVM`的资源，如果`jstatd`进程运行在这种权限中，那么它可以监视系统中的所有`JVM`，但是这也带来了额外的安全问题。

`jstatd`服务不会对客户端进行任何的验证，因此运行了`jstatd`服务的`JVMs`，网络上的任何用户的都具有访问权限，这种暴露不是我们所希望的，因此在启动`jstatd`之前本地安全策略必须要加以考虑，特别是在生产环境中或者是在不安全的网络环境中。

如果没有其他安全管理器被安装，`jstatd`服务将会安装一个`RMISecurityPolicy`的实例，因此需要在一个安全策略文件中指定，该策略文件必须符合的默认策略实施的策略文件语法。

  下面的这个示例策略将允许`jstatd`服务具有`JVM`全部的访问权限：    

```shell
grant codebase "file:${java.home}/../lib/tools.jar" {
   permission java.security.AllPermission;
};
```

__注__：此处策略中的 `java.home`，和 `JAVA_HOME` 不是一个概念，童鞋们不要搞错了，此处的 `java.home` 指的是 `JRE` 的路径，这个是 `Java` 的系统属性，不需要手工指定，通常是这个 `jdk` 下面的 `jre` 路径 , 即可以认为` ${java.home}` 和` ${JAVA_HOME}/jre` 是等价，如果想查看这个变量的值，可以任意找一个运行着的 `Java` 应用，找到它的 `PID` ，然后通过如下 `jinfo` 命令查看就可以查看到 `java.home` 的值：

```shell
jinfo ${PID} | grep java.home
```

也可以在`Java`代码中通过如下方式获取到：

```java
System.out.println(System.getProperty("java.home"))
```

将上面的策略内容拷贝一个文件中，文件名可以随意取，为了形象我们将文件名命名为`jstatd.all.policy`，文件存放的路径也可以随意，只有你当前登陆的用户具有访问权限就可以，然后执行以下命令就可以启动`jstatd`服务：

```shell
jstatd -J-Djava.security.policy=jstatd.all.policy
```

如果是在具有安全限制的环境中，`jstatd`的策略安全一定要设置得当，并且只允许受信任的服务器或者网络访问，以免遭受网络攻击，如果存在安全隐患，最好不要启动`jstatd`服务，就在本地使用`jstat`及`jps`等工具对`JVM`进行监控了。

### 示例

#### 示例一：使用内部RMI注册中心

下面这个示例演示了通过内部`RMI`注册中心启动`jstatd`，这个示例假设没有其它的服务绑定到默认的`RMI`注册中心端口（默认端口是`1099`）

```shell
jstatd -J-Djava.security.policy=jstatd.all.policy
```

注：如果基于默认端口`1099`的`RMI`注册中心原来没有被启动过，那么上面运行的命令首先会启动端口为`1099`的`RMI`注册中心，然后再启动`jstatd`服务，此时即使`jstatd`停止了，`RMI`注册中心也不会停止；如果是再次执行上面的命令，就不会再次启动`RMI`注册中心，`jstatd`会直接注册到注册中心

#### 示例二：使用外部RMI注册中心

这个示例演示了使用一个外部的`RMI`注册中心来启动`jstatd`，如果默认的内部注册中心已经被启动了，下面的这个示例就会抛出“端口`1099`已经被占用”的异常，因为它尝试在`1099`端口启动外部`RMI`注册中心：

```shell
rmiregistry&jstatd -J-Djava.security.policy=all.policy
```

这个示例演示了使用一个外部的`RMI`注册中心来启动`jstatd`，此注册中心的端口为`2020`

```shell
rmiregistry 2020&jstatd -J-Djava.security.policy=all.policy -p 2020
```

这个示例演示了使用一个外部的 `RMI` 注册中心来启动 `jstatd` ，此注册中心的端口为 `2020` ，并且绑定到 `RMI` 注册中心的名为 `AlternateJstatdServerName`

```shell
rmiregistry 2020&jstatd -J-Djava.security.policy=all.policy -p 2020 -n AlternateJstatdServerName
```

#### 示例三：禁止内部RMI注册中心的创建

这个示例演示了`jstatd`在启动的时候，如果没有找到默认的`RMI`注册中心，也不会创建默认的注册中心。这个示例中如果没有`RMI`注册中心在运行，此示例就会报错，如果存在就会正常运行

```shell
jstatd -J-Djava.security.policy=all.policy -nr
```

#### 示例四：开启RM日记记录

这个示例演示的是`jstatd`运行在开启了日志记录功能的`RMI`注册中，这个对于问题查找或监控服务状态非常有用

```shell
jstatd -J-Djava.security.policy=all.policy -J-Djava.rmi.server.logCalls=true
```

