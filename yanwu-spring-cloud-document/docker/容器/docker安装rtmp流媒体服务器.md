#### 给docker容器安装rtmp流媒体服务器

##### 下载镜像文件

```shell
docker pull tiangolo/nginx-rtmp
```

![image-20220516105751556](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2022/05/20220516105753933.png)

##### 启动脚本

```shell
#################################################
# 1935:1935 && 1936:1936   	# 容器端口映射
# tiangolo/nginx-rtmp    	# 镜像名称与镜像版本
# restart=always	    	# 自动重启redis容器
#################################################
docker run -d --restart=always --name nginx-rtmp -p 1935:1935 -p 1936:1936 tiangolo/nginx-rtmp
```

##### 开放端口

```shell
firewall-cmd --zone=public --add-port=1935/tcp --permanent
firewall-cmd --zone=public --add-port=1936/tcp --permanent
firewall-cmd --reload
```

