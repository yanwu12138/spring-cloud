### 安装Solr cloud

#### 安装tomcat

##### 下载

```shell
wget https://mirrors.bfsu.edu.cn/apache/tomcat/tomcat-8/v8.5.61/bin/apache-tomcat-8.5.61.tar.gz
```

##### 解压缩

```shell
tar -zxvf apache-tomcat-8.5.61.tar.gz
mv apache-tomcat-8.5.61 tomcat
```

##### 修改tomcat默认端口

```shell
##### 将tomcat默认的8080端口改成其他需要的端口
vim tomcat/conf/server.xml
```

#### 安装solr

##### 下载

```shell
wget http://archive.apache.org/dist/lucene/solr/7.1.0/solr-7.1.0.zip
```

##### 解压缩

```shell
unzip -oq solr-7.1.0.zip -d /usr/local/src/tools/
```

##### 将solr文件放到tomcat中

```shell
##### 在tomcat中创建solr目录，可以选择将不需要项目的都删掉 
cd tomcat/webapps
rm -rf * 
mkdir solr 
cd solr

##### 将solr运行时需要的文件放到tomcat中
cp -r solr-7.1.0/server/solr-webapp/webapp/*    tomcat/webapps/solr/
cp -r solr-7.1.0/server/lib/ext/*               tomcat/webapps/solr/WEB-INF/lib/
cp -r solr-7.1.0/server/lib/metrics*.*          tomcat/webapps/solr/WEB-INF/lib/
cp -r solr-7.1.0/dist/solr-dataimporthandler-*  tomcat/webapps/solr/WEB-INF/lib/

##### 日志配置
mkdir -p tomcat/webapps/solr/WEB-INF/classes 
cp -r solr-7.1.0/server/resources/log4j.properties tomcat/webapps/solr/WEB-INF/classes/
```

##### 修改solr配置文件web.xml

```xml
<!-- 放开env-entry -->
<env-entry>
	<env-entry-name>solr/home</env-entry-name>
	<env-entry-value>/usr/local/src/solr</env-entry-value>
	<env-entry-type>java.lang.String</env-entry-type>
</env-entry>

<!-- 注释掉security-constraint -->
<!-- Get rid of error message -->
<!--
<security-constraint>
	<web-resource-collection>
		<web-resource-name>Disable TRACE</web-resource-name>
		<url-pattern>/</url-pattern>
		<http-method>TRACE</http-method>
	</web-resource-collection>
	<auth-constraint/>
</security-constraint>
<security-constraint>
	<web-resource-collection>
	<web-resource-name>Enable everything but TRACE</web-resource-name>
	<url-pattern>/</url-pattern>
	<http-method-omission>TRACE</http-method-omission>
	</web-resource-collection>
</security-constraint>
-->
```

##### 配置solr运行的配置

```shell
cp -r solr-7.1.0/server/solr/*  solr/
cp -r solr-7.1.0/contrib/* 		solr/
cp -r solr-7.1.0/dist/* 		solr/
```

##### 启动tomcat

查看：http://{ip}:{port}/solr/index.html#/ 主页，检查启动是否正常

![image-20210125174528440](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021012517452828.png)

#### 配置zookeeper

##### [安装](../分布式/Zookeeper.md)

##### 配置

>   修改tomcat启动文件，添加zookeeper的地址信息，这样tomcat启动后，solr服务就可以到zookeeper中注册自己的信息，或者获取其它节点信息。
>
>   ```shell
>   ################### 说明 #######################################
>   # -Dsolr.solr.solr.home # 指定的是Solr索引库位置
>   # -DzkHost              # 指定的是三个zookeeper的ip和客户端端口信息
>   ###############################################################
>   export JAVA_OPTS="-Dsolr.solr.home=/usr/local/myapp/solr-4.10.2/example/solr -DzkHost=192.168.56.11:2181,192.168.56.12:2181,192.168.56.13:2181"
>   ```
>
>   