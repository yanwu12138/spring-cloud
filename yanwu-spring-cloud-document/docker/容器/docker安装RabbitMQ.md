##### 获取镜像

```shell
#指定版本，该版本包含了web控制页面
docker pull rabbitmq:management
```

##### 运行镜像

```shell
#方式二：设置用户名和密码
docker run -d --restart=always --hostname my-rabbit --name rabbit -e RABBITMQ_DEFAULT_USER=[USER] -e RABBITMQ_DEFAULT_PASS=[PASSWORD] -p 15672:15672 -p 5672:5672 rabbitmq:management
```

##### 访问ui页面

http://{docker宿主机ip}:15672/	>>	看到如下界面，表示安装成功

![image-20210114181756648](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021011418175656.png)