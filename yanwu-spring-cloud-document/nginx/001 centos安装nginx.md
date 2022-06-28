### centOS安装nginx

>   Nginx 是 C语言 开发，建议在 Linux 上运行，当然，也可以安装 Windows 版本，本篇则使用 [CentOS](http://www.linuxidc.com/topicnews.aspx?tid=14) 7 作为安装环境。

#### 安装环境

##### gcc 安装

>   安装 nginx 需要先将官网下载的源码进行编译，编译依赖 gcc 环境，如果没有 gcc 环境，则需要安装：

```shell
yum install gcc-c++ -y
```

##### PCRE pcre-devel 安装

>   PCRE(Perl Compatible Regular Expressions) 是一个Perl库，包括 perl 兼容的正则表达式库。nginx 的 http 模块使用 pcre 来解析正则表达式，所以需要在 linux 上安装 pcre 库，pcre-devel 是使用 pcre 开发的一个二次开发库。nginx也需要此库。命令：

```shell
yum install -y pcre pcre-devel
```

##### zlib 安装

>   zlib 库提供了很多种压缩和解压缩的方式， nginx 使用 zlib 对 http 包的内容进行 gzip ，所以需要在 Centos 上安装 zlib 库。

```shell
yum install -y zlib zlib-devel
```

##### OpenSSL 安装

>OpenSSL 是一个强大的安全套接字层密码库，囊括主要的密码算法、常用的密钥和证书封装管理功能及 SSL 协议，并提供丰富的应用程序供测试或其它目的使用。
>nginx 不仅支持 http 协议，还支持 https（即在ssl协议上传输http），所以需要在 Centos 安装 OpenSSL 库。

```shell
yum install -y openssl openssl-devel
```

#### 安装

##### 下载

-   直接下载`.tar.gz`安装包，地址：https://nginx.org/en/download.html

    ![image-20211011162758506](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/10/2021101116301818.png)

-   使用`wget`命令下载（推荐）。

    >   ```shell
    >   wget -c https://nginx.org/download/nginx-1.12.0.tar.gz
    >   ```
    >
    >   ![image-20211011164817584](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/10/2021101116481717.png)

##### 解压

```shell
tar -zxvf nginx-1.12.0.tar.gz
```

##### 编译安装

```shell
cd nginx-1.12.0
./configure && make
./configure && make install
```

#### 启动与停止

```shell
cd /usr/local/nginx/sbin/
##### 启动
./nginx
##### 退出：此方式停止步骤是待nginx进程处理任务完毕进行停止。
./nginx -s quit
##### 停止：此方式相当于先查出nginx进程id再使用kill命令强制杀掉进程。
./nginx -s stop
##### 重启
./nginx -s reload
```

![image-20211011190312759](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/10/2021101119031212.png)

#### 开机自启动

```shell
vi /etc/rc.local
##### 增加一行
/usr/local/nginx/sbin/nginx
```

