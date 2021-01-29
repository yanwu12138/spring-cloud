### Elasticsearch

>   Elastic 的底层是开源库 [Lucene](https://lucene.apache.org/)。但是，你没法直接用 Lucene，必须自己写代码去调用它的接口。Elastic 是 Lucene 的封装，提供了 REST API 的操作接口，开箱即用。

#### centOS安装

Elastic 需要 Java 8 环境。安装Java环境后就可以跟着官方文档安装elastic。

##### 下载与解压

```shell
##### 下载
wget https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-5.5.1.zip

##### 解压缩
unzip elasticsearch-5.5.1.zip

##### 更改目录名
mv elasticsearch-5.5.1 elasticsearch
```

##### 创建用户

```shell
##### 创建用户
adduser elasticsearch

##### 设置密码
passwd elasticsearch

##### 将对应的文件夹权限赋给该用户
chown -R elasticsearch elasticsearch
```

##### 修改配置

```shell
vim elasticsearch/config/elasticsearch.yml

##### 添加以下配置

```



##### 启动

```shell
cd elasticsearch/bin
sudo -su elasticsearch ./elasticsearch
```

##### 检验是否安装成功

![image-20210129152141298](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021012915214141.png)



#### [docker安装](../docker/容器/docker安装ElasticSearch.md)





