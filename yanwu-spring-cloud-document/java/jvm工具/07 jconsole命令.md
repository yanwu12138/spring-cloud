### jconsole（Java Monitoring and Management Console）

`jconsole`:一个`java GUI`监视工具，可以以图表化的形式显示各种数据。并可通过远程连接监视远程的服务器`VM`。用`java`写的`GUI`程序，用来监控`VM`，并可监控远程的`VM`，非常易用，而且功能非常强。命令行里打 `jconsole`，选则进程就可以了。

需要注意的就是在运行`jconsole`之前，必须要先设置环境变量`DISPLAY`，否则会报错误，`Linux`下设置环境变量如下：

```
export DISPLAY=:0.0
```

![image-20210602182444232](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/06/2021060218244444.png)

![image-20210602182510711](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/06/2021060218251010.png)