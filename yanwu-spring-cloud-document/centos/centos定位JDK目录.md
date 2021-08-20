#### 定位JDK安装目录

##### echo

```shell
echo $JAVA_HOME
```

>   使用该方法的前提是配置过 /etc/profile 文件，如果该文件$JAVA_HOME配置错误，输出结果也是错误的。
>
>   ![image-20210820153111417](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/08/2021082015311111.png)

##### 查询 java 的执行路径（注：不是安装路径！）

```shell
which java
```

![image-20210820152912132](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/08/2021082015291212.png)

##### 根据 java 的执行路径找到安装目录

```shell
ls -lrt /usr/bin/java
```

![image-20210820153001690](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/08/202108201530011.png)

```shell
ls -lrt /etc/alternatives/java
```

![image-20210820154529908](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/08/2021082015452929.png)

