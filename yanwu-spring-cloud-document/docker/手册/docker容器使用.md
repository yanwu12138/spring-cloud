#### [命令](https://www.runoob.com/docker/docker-command-manual.html)

>   输入 docker 命令来查看到 Docker 客户端的所有命令选项

![image-20210118164942004](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021011816494242.png)

##### 命令的帮助

>   使用`--help`了解指定的命令的使用方法

![image-20210118165111013](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021011816511111.png)



#### 容器使用

##### 运行容器

```SHELL
#######################################################
# yanwu12138                  # root账号密码          #
# ~/mysql/data:/var/lib/mysql # mysql数据文件存放位置 #
# mysql:5.7                   # 镜像名称与镜像版本    #
# 3306:3306                   # 容器端口映射          #
# restart=always              # 自动重启mysql容器     #
#######################################################
docker run -d --name mysql --restart=always -v ~/mysql/data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=yanwu12138 -p 3306:3306 mysql:5.7
```

##### 查看容器

```shell
### 查看所有列
docker ps -a
### 查看指定列
docker ps --format "table {{.ID}}\t{{.Names}}\t{{.Status}}\t{{.Ports}}"
```

![image-20210118165429316](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021011816542929.png)

##### 停止容器

```
docker stop {CONTAINER ID}
```

##### 启动停止的容器

```SHELL
docker start {CONTAINER ID}
```

##### 重启容器

```shell
docker restart {CONTAINER ID}
```

##### 进入容器

```SHELL
docker exec -it {CONTAINER ID} /bin/bash
```

##### 删除容器

```SHELL
docker rm -f {CONTAINER ID}
```



#### 导入 & 导出容器

##### 导出容器

```
docker export {CONTAINER ID} > {filename}.tar
```

![image-20210118165945098](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021011816594545.png)

##### 导入容器

```shell
##### 指定文件导入
cat {filename}.tar | docker import - test/{NAME}:{TAG}
##### 指定URL导入
docker import http://example.com/exampleimage.tgz example/imagerepo
```

![image-20210118170344039](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021011817034444.png)



#### 容器端口

>   通过 **docker ps** 命令可以查看到容器的端口映射，**docker** 还提供了另一个快捷方式 **docker port**，使用 **docker port** 可以查看指定 （ID 或者名字）容器的某个确定端口映射到宿主机的端口号

```shell
docker port {CONTAINER ID}
docker port {NAMES}
```

**![image-20210118170649453](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021011817064949.png)**



#### 容器日志

```SHELL
docker logs -f --tail 10 {CONTAINER ID}
```

![image-20210118170852533](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021011817085252.png)



#### 容器进程

```SHELL
docker top {CONTAINER ID}
```

![image-20210118170953447](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021011817095353.png)



#### 检查容器配置和状态

```SHELL
docker inspect {CONTAINER ID}
```

![image-20210118171123008](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021011817112323.png)



