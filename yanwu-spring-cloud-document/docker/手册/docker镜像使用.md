#### 查看本地镜像列表

```shell
########## 说明 ################
# REPOSITORY  # 表示镜像的仓库源
# TAG         # 镜像的标签
# IMAGE ID    # 镜像ID
# CREATED     # 镜像创建时间
# SIZE        # 镜像大小
###############################
docker images
```

![image-20210118162011189](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021011816201111.png)

>   同一仓库源可以有多个 TAG，代表这个仓库源的不同个版本，如 ubuntu 仓库源里，有 15.10、14.04 等多个不同的版本，我们使用 REPOSITORY:TAG 来定义不同的镜像。



#### 查找镜像

```shell
########## 说明 ########################
# NAME         # 镜像仓库源的名称
# DESCRIPTION  # 镜像的描述
# OFFICIAL     # 是否 docker 官方发布
# stars        # 类似 Github 里面的 star
# AUTOMATED    # 自动构建
#######################################
docker search httpd
```

![image-20210118162541811](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021011816254141.png)



#### 下载镜像

```SHELL
### 当不指定{TAG}时，自动获取最新版本镜像
docker pull {NAME}:{TAG}
```

![image-20210118163114736](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021011816311414.png)



#### 删除镜像

```SHELL
docker rmi {REPOSITORY}
docker rmi {IMAGE ID}
docker rmi {REPOSITORY}:{TAG}
```

![image-20210118163142246](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021011816314242.png)



#### 构建镜像

>   我们使用命令 **docker build** ， 从零开始来创建一个新的镜像。为此，我们需要创建一个 Dockerfile 文件，其中包含一组指令来告诉 Docker 如何构建我们的镜像

##### [DockerFile](./docker镜像Dockerfile.md)

```SHELL
[root@localhost docker-file]# cat Dockerfile

##### FROM # 指定使用哪个镜像源
FROM    centos:6.7
MAINTAINER      Fisher "fisher@sudops.com"
##### RUN # 指令告诉docker 在镜像内执行命令，安装了什么。。。
RUN     /bin/echo 'root:123456' |chpasswd
RUN     useradd runoob
RUN     /bin/echo 'runoob:123456' |chpasswd
RUN     /bin/echo -e "LANG=\"en_US.UTF-8\"" >/etc/default/local
EXPOSE  22
EXPOSE  80
CMD     /usr/sbin/sshd -D
```

##### 构建新镜像

```SHELL
######### 说明 ################
# -t  # 指定要创建的目标镜像名
# .   # Dockerfile 文件所在目录
##############################
docker build -t runoob/centos:6.7 .
```

![image-20210118163959793](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021011816395959.png)

##### 设置镜像标签

```SHELL
docker tag {IMAGE ID} runoob/centos:dev
```

![image-20210118164443221](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021011816444343.png)