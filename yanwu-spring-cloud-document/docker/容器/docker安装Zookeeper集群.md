#### 本文档使用docker容器运行zookeeper集群

##### 查找镜像

```shell
docker search zookeeper
```

![image-20210422172234283](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/04/2021042217223434.png)

##### 拉取镜像

```shell
docker pull zookeeper
```

![image-20210422172704617](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/04/202104221727044.png)

##### 创建对应的目录

```shell
##### 数据目录
mkdir -p /root/zookeeper/data/server_1
mkdir -p /root/zookeeper/data/server_2
mkdir -p /root/zookeeper/data/server_3
##### 日志目录
mkdir -p /root/zookeeper/logs/server_1
mkdir -p /root/zookeeper/logs/server_2
mkdir -p /root/zookeeper/logs/server_3
```

##### [创建网络](./../手册/docker容器使用.md)

##### zookeeper的启动脚本

```shell
#####################################################################
# -p 2181:2181								# 端口映射				 #
# --network zoonet --ip 172.18.0.11			# 设置固定IP			 #
# --name zookeeper_node1					# 节点名称				 #
# --restart always							# 自动重启zookeeper容器	#
# -v /root/zookeeper/data/server_1:/data	# 数据目录映射			#
# -v /root/zookeeper/logs/server_1:/datalog # 日志目录映射			#
# -v /root/zookeeper/logs/server_1:/logs	# 日志目录映射			#
# -e ZOO_MY_ID=1							# 节点编号				 #
# -e "ZOO_SERVERS=server.1..."				# 集群配置				 #
#####################################################################
##### node1
docker run -d -p 2181:2181 --name zookeeper_node1 --privileged --restart always --network zoonet --ip 172.18.0.11 -v /root/zookeeper/data/server_1:/data -v /root/zookeeper/logs/server_1:/datalog -v /root/zookeeper/logs/server_1:/logs -e ZOO_MY_ID=1 -e "ZOO_SERVERS=server.1=172.18.0.11:2888:3888;2181 server.2=172.18.0.12:2888:3888;2182 server.3=172.18.0.13:2888:3888;2183" zookeeper

sleep 10s
echo docker zookeeper node2 start...

##### node2
docker run -d -p 2182:2181 --name zookeeper_node2 --privileged --restart always --network zoonet --ip 172.18.0.12 -v /root/zookeeper/data/server_2:/data -v /root/zookeeper/logs/server_2:/datalog -v /root/zookeeper/logs/server_2:/logs -e ZOO_MY_ID=2 -e "ZOO_SERVERS=server.1=172.18.0.11:2888:3888;2181 server.2=172.18.0.12:2888:3888;2182 server.3=172.18.0.13:2888:3888;2183" zookeeper

sleep 10s
echo docker zookeeper node2 start...

##### node1
docker run -d -p 2183:2181 --name zookeeper_node3 --privileged --restart always --network zoonet --ip 172.18.0.13 -v /root/zookeeper/data/server_3:/data -v /root/zookeeper/logs/server_3:/datalog -v /root/zookeeper/logs/server_3:/logs -e ZOO_MY_ID=3 -e "ZOO_SERVERS=server.1=172.18.0.11:2888:3888;2181 server.2=172.18.0.12:2888:3888;2182 server.3=172.18.0.13:2888:3888;2183" zookeeper

sleep 10s
echo docker zookeeper node2 start...
```

![image-20210422190533924](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/04/2021042219053333.png)

##### 检查节点是否正常工作

```shell
##### 进入节点
docker exec -ti zookeeper_node1 bash
##### 查看状态
zkServer.sh status
```

![image-20210422200510242](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/04/2021042220051010.png)

##### 进入终端

```shell
##### 进入终端
zkCli.sh -server 192.168.56.150:2181,192.168.56.150:2182,192.168.56.150:2183
```

![image-20210423095631065](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/04/2021042309563131.png)

##### 放行端口

```shell
##### 放行
firewall-cmd --zone=public --add-port=2181/tcp --permanent
firewall-cmd --zone=public --add-port=2182/tcp --permanent
firewall-cmd --zone=public --add-port=2183/tcp --permanent
firewall-cmd --reload
##### 查看端口放行
firewall-cmd --zone=public --list-ports
```

![image-20210422192636920](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/04/2021042219263636.png)