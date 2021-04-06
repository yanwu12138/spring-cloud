#### 查看当前系统时间

```shell
date
```

![image-20210406114118401](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/04/2021040611411818.png)



#### 修改当前系统时间

##### 修改日期

```shell
date -s 2021-04-06
```



##### 修改时间

```shell
date -s 20:13:00
```



#### 修改系统时区

```
timedatectl set-timezone Asia/Shanghai
```



#### 增加时间NTP同步

```shell
ntpdate ntp.ubuntu.com
```



#### 查看系统硬件时间

```shell
hwclock --show
```



#### 同步系统时间和硬件时间

```shell
hwclock --hctosys
```



#### 本地时间写入硬件时间

```shell
timedatectl set-local-rtc 1
```

