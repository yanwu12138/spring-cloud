#### 本文档使用docker容器运行Redis:5.0.8版本

##### 下载镜像

```shell
docker pull redis:5.0.8
```

 ##### 查看镜像下载是否成功

```shell
docker image ls
```

##### redis容器启动脚本

```shell
#################################################
# 6379:6379    		    	# 容器端口映射
# redis:5.0.8   	    	# 镜像名称与镜像版本
# yanwu12138    	    	# redis密码
# restart=always	    	# 自动重启redis容器
# -v /root/redis/data:/data # 将redis数据映射出来
# --appendonly yes	    	# 避免容器重启时丢失数据
#################################################
docker run -d --restart=always --name redis -p 6379:6379 -v /root/redis/data:/data redis:5.0.8 --requirepass "yanwu12138" --appendonly yes
```

![image-20210105142934701](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021010514293434.png)