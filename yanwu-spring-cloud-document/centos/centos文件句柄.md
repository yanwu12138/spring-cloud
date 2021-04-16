### 打开的文件过多

>   如果碰到以下异常，说明java进程打开的文件过多，超出了centos的进程句柄数限制，可以通过以下方法进行排查与解决

![image-20210414100506533](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/04/202104141005066.png)



#### 操作系统的句柄数

##### 查看操作系统允许打开的最大文件数

```shell
cat /proc/sys/fs/file-max
```

![image-20210414101441903](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/04/2021041410144141.png)

##### 查看每个用户允许打开的最大文件数

>   通常情况下，操作系统默认的允许打开最大文件数都为1024，当某个用户或某个进程到开的文件超过这个限制时，就会抛出"打开的文件过多"异常

```shell
ulimit -a
```

![image-20210414101949345](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/04/2021041410194949.png)



#### 方法1：修改用户允许打开的最大文件数

__注意：__次方法治标不治本，根据次方法修改后，如果不对程序进行检查，最终还是可能会超过修改后的句柄数

```shell
ulimit -n 4096
```

![image-20210414102056250](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/04/2021041410205656.png)



#### 方法2：查看java程序打开后没有释放的文件

##### 通过 `lsof命令` 查看JAVA进程打开的文件

```shell
##### 查看进程
ps -ef | grep java
##### 查看进程打开的所有文件
lsof -p 22690
##### 根据条件过滤
lsof -p 22690 | grep "/home/admin"
```

![image-20210414102429410](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/04/2021041410242929.png)



##### 通过 `proc目录` 查看JAVA进程打开的文件

```shell
##### 查看进程
ps -ef | grep java
##### 根据条件过滤
ls -al /proc/29267/fd  | grep "/home/admin/tmp/file/"
```

![image-20210416160035873](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/04/2021041616003535.png)



##### 根据查询结果定位问题

遇到这类异常时，绝大部分都是由于程序内打开了文件却没有将对应的`Stream`进行释放导致的，根据查看JAVA进程打开的文件结果可以判断出，是打开的那类的文件没有释放，从而定位到具体的业务模块进行排查，找到没有释放的`Stream`将其`close()`掉才能解决根本问题