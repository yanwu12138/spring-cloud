### rocketMQ

>   __运行rocket之前先安装JDK__

##### 查找镜像

```SHELL
docker search rocketmq
```

![image-20210105140529465](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021010514052929.png)

##### 拉取镜像

```SHELL
docker pull rocketmqinc/rocketmq
```

![image-20210105140710949](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021010514071111.png)

#### 启动namesrv

##### namesrv容器启动脚本

```SHELL
###########################################################################################
# 9876:9876                               # 容器端口映射
# -v                                      # 参数使用 volume 功能，把你本地的目录映射到容器内的目录上
# /root/log/rocketMQ/data/namesrv/logs    # 宿主机想保存 MQ 的日志
# /root/log/rocketMQ/data/namesrv/store   # 宿主机想保存 MQ 的数据
# restart=always                          # 自动重启namesrv容器
###########################################################################################
docker run -d -p 9876:9876 -v /root/log/rocketMQ/data/namesrv/logs:/root/logs -v /root/log/rocketMQ/data/namesrv/store:/root/store --restart=always --name rmqnamesrv -e "MAX_POSSIBLE_HEAP=100000000" rocketmqinc/rocketmq sh mqnamesrv
```

![image-20210105141227142](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021010514122727.png)

#### 启动broker

##### 新建配置文件

```SHELL
cd /root/log/rocketMQ/conf
vim broker.conf

##### 以下为文件 broker.conf 内容
brokerClusterName = DefaultCluster
brokerName = broker-a
brokerId = 0
deleteWhen = 04
fileReservedTime = 48
brokerRole = ASYNC_MASTER
flushDiskType = ASYNC_FLUSH
brokerIP1 = {docker宿主机IP}
```

##### broker容器启动脚本

```SHELL
############################################################
# 10911:10911 & 10909:10909            # 容器端口映射
# /root/log/rocketMQ/data/broker/logs  # 宿主机想保存 MQ 的日志
# /root/log/rocketMQ/data/broker/store # 宿主机想保存 MQ 的数据
# /root/log/rocketMQ/conf/broker.conf  # broker配置文件
# --restart=always                     # 自动重启broker容器
############################################################
docker run -d -p 10911:10911 -p 10909:10909 -v /root/log/rocketMQ/data/broker/logs:/root/logs -v /root/log/rocketMQ/data/broker/store:/root/store -v /root/log/rocketMQ/conf/broker.conf:/opt/rocketmq/conf/broker.conf --restart=always --name rmqbroker --link rmqnamesrv:namesrv -e "NAMESRV_ADDR=namesrv:9876" -e "MAX_POSSIBLE_HEAP=200000000" rocketmqinc/rocketmq sh mqbroker -c /opt/rocketmq/conf/broker.conf
```

![image-20210105141640589](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021010514164040.png)

### rocket-console

##### 查找镜像

```SHELL
docker search rocketmq-console
```

![image-20210105141756330](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021010514175656.png)

##### 拉取镜像

```SHELL
docker pull styletang/rocketmq-console-ng
```

![image-20210105141845132](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021010514184545.png)

##### console容器启动脚本

```SHELL
#############################################################################################
# JAVA_OPTS 			# 参数因不同的console系统内容可能不同，可以结合console系统中的配置文件做相应修改
# --restart=always      # 自动重启console容器
# 192.168.56.150:9876   # MQ地址
# 8080:8080             # 容器端口映射
#############################################################################################
docker run -d --restart=always -e "JAVA_OPTS=-Drocketmq.config.namesrvAddr=192.168.56.150:9876 -Drocketmq.config.isVIPChannel=false" -p 8080:8080 -t styletang/rocketmq-console-ng
```

##### 查看控制台

http://{docker宿主机ip}:8080/	>>	看到如下界面，表示安装成功

![image-20210105142301923](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/202101051423022.png)