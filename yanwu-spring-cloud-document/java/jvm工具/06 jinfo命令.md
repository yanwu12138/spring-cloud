### jinfo（Java Configuration Info）

jinfo可以输出并修改运行时的java 进程的opts。用处比较简单，用于输出JAVA系统参数及命令行参数。

用法是jinfo -opt pid 如：查看2788的MaxPerm大小可以用 `jinfo -flag MaxPermSize 2788`