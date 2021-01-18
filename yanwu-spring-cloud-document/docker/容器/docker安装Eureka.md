#### 本文档使用docker容器运行springcloud/eureka最后一个版本

##### 查询镜像

```SHELL
docker search eureka
```

![image-20210118105107988](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/202101181051088.png)

##### docker拉取镜像

```SHELL
docker pull springcloud/eureka
```

##### 查看镜像是否拉取成功

```shell
docker images
```

![image-20210118105225024](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021011810522525.png)

##### Eureka-server容器启动脚本

```SHELL
#####################################
# 8761:8761        # 容器端口映射
# restart=always   # 自动重启mysql容器
#####################################
docker run -d --name eureka --restart=always -p 8761:8761 -t springcloud/eureka
```

##### 访问Eureka-server查看是否启动成功

```reStructuredText
主页：http://{docker宿主机ip}:8761
```

![image-20210118105434810](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021011810543434.png)