#### 安装JDK

##### 检查是否已有JDK

```shell
rpm -qa | grep java
rpm -qa | grep jdk
rpm -qa | grep gcj
```

-   如果没有输入信息表示没有安装。

-   如果安装可以使用 `rpm -qa | grep java | xargs rpm -e --nodeps` 批量卸载所有带有Java的文件

##### 安装JDK1.8

```
yum install java-1.8.0-openjdk* -y
```

##### 检查是够安装成功

```
java -version
```