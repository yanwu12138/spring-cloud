### 本文档使用docker容器运行elasticsearch的7.3.0版本

#### 安装ElasticSearch

##### 查找镜像

```shell
docker search elasticsearch
```

##### 下载镜像

```shell
docker pull docker.elastic.co/elasticsearch/elasticsearch:7.3.0
```

##### 查看镜像是否拉取成功

```shell
docker images
```

![image-20210129154116981](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021012915411717.png)

##### ElasticSearch启动脚本

```shell
#####################################################################################################
# 9200:9200 && 9300:9300        	# 容器端口映射
# discovery.type=single-node 		# 当前es以单节点模式运行
# ES_JAVA_OPTS="-Xms64m -Xmx128m" 	# 指定开发时es运行时的最小和最大内存占用为64M和128M，否则就会占用全部可用内存
# restart=always   					# 自动重启容器
#####################################################################################################
docker run --name elasticsearch -d -p 9200:9200 -p 9300:9300 --restart=always -e "discovery.type=single-node" -e ES_JAVA_OPTS="-Xms64m -Xmx128m" docker.elastic.co/elasticsearch/elasticsearch:7.3.0
```

##### 访问ElasticSearch查看是否启动成功

```reStructuredText
主页：http://{docker宿主机ip}:9200/
```

![image-20210129154341205](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021012915434141.png)



#### 安装kibana

##### 查找镜像

```shell
docker search kibana
```

##### 下载镜像

>   __注意：elasticSearch && kibana 的版本必须一致__

```shell
docker pull kibana:7.3.0
```

##### 查看镜像是否拉取成功

```shell
docker images
```

![image-20210129155121899](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021012915512121.png)

##### 配置宿主机

>   这是一个已知的 Bug，宿主机不允许来自本机的 Docker 容器访问。必须通过设置 firewalld 规则允许本机的 Docker 容器访问。

```shell
vim /etc/firewalld/zones/public.xml

##### 添加如下内容，注意这里的 172.17.0.0/16 可以匹配 172.17.xx.xx IP 段的所有 IP
<rule family="ipv4">
  <source address="172.17.0.0/16" />
  <accept />
</rule>
```

![image-20210201101850363](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/02/2021020110185050.png)

##### 重启防火墙

```shell
systemctl restart firewalld
```

##### 配置文件

>   kibana.yml
>
>   ```xml
>   #################################################
>   # Default Kibana configuration for docker target
>   # 172.17.0.10 #  elasticSearch容器IP
>   #################################################
>   server.name: kibana
>   server.host: "0"
>   elasticsearch.hosts: [ "http://{宿主机IP}:9200" ]
>   xpack.monitoring.ui.container.elasticsearch.enabled: true
>   ```

##### kibana启动脚本

```shell
######################################################
# 5601:5601				        	# 容器端口映射
# /root/kibana/kibana.yml	 		# kibana的配置文件
# restart=always   					# 自动重启容器
######################################################
docker run -d --restart=always --log-driver json-file --log-opt max-size=100m --log-opt max-file=2 --name kibana -p 5601:5601 -v /root/kibana/kibana.yml:/usr/share/kibana/config/kibana.yml kibana:7.3.0
```

##### 访问kibana查看是否启动成功

```text
主页：http://{docker宿主机ip}:5601/
```

![image-20210129181310087](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021012918131010.png)