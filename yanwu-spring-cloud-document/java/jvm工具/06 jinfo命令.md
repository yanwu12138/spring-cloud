### jinfo（Java Configuration Info）

`jinfo`可以输出并修改运行时的`java` 进程的`opts`。用处比较简单，用于输出`JAVA`系统参数及命令行参数。

用法是`jinfo -opt pid` 如：查看`2788`的`MaxPerm`大小可以用 `jinfo -flag MaxPermSize 2788`