#### 安装crontab

```shell
yum install crontabs -y
```



#### 启用crontab

```shell
systemctl enable crond
systemctl start crond
```



#### 查看crontab运行状态

```shell
systemctl status crond
```

![image-20210406113633159](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/04/2021040611363333.png)



#### 添加定时任务

##### 进入crontab编辑器

```shell
crontab -e
```

##### 添加定时任务

```
*/30 * * * * /sbin/hwclock -w
```

##### 重启crontab

```shell
systemctl restart crond
```



#### 查看crontab

```shell
crontab -l
```

![image-20210406113946090](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/04/2021040611394646.png)