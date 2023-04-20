#### 本文档使用docker容器运行springcloud/eureka最后一个版本

##### 查询镜像

```SHELL
docker search arm64v8/centos
```

![image-20230414152603808](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2023/04/image-20230414152603808.png)

##### docker拉取镜像

```SHELL
docker pull arm64v8/centos
```

##### 查看镜像是否拉取成功

```shell
docker images
```

![image-20230414162432372](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2023/04/image-20230414162432372.png)

##### centos容器启动脚本

```SHELL
#########################################################################################
# 8022:22          			# 容器端口映射
# --privileged=true   	# 开启容器特权模式
# /usr/sbin/init				# 启动容器之后可以使用systemctl xxxxx start/stop/restart 等命令
#########################################################################################
docker run -itd -p 8022:22 --name=centos -v /Users/xubaofeng/devTool/docker/centos:/bodata -h yanwu_vm --privileged=true e6a0117ec169 /usr/sbin/init
```

![image-20230414163411368](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2023/04/image-20230414163411368.png)

##### 开启SSH通道