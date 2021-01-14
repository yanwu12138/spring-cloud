#### 本文档使用docker容器运行nacos-server:1.1.3版本

##### docker拉取nacos-server:1.1.3镜像

```shell
docker pull nacos/nacos-server:1.1.3
```

##### 查看镜像是否拉取成功

```SHELL
docker image ls
```

##### nacos-server容器启动脚本

```SHELL
docker run --name nacos -d -p 8848:8848 --privileged=true --restart=always -e JVM_XMS=512m -e JVM_XMX=2048m -e MODE=standalone -e PREFER_HOST_MODE=hostname -v /home/nacos/logs:/home/nacos/logs  nacos/nacos-server:1.1.3
```

##### 访问nacos主页查看nacos-server是否启动成功

```reStructuredText
主页：http://{docker宿主机ip}:8848/nacos/#/index.html
账号：nacos
密码：nacos
```

![image-20210114182059869](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021011418205959.png)