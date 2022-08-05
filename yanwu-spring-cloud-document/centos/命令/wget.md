### Linux资源下载

#### wget

>   wget是Linux中的一个下载文件的工具。wget工具体积小但功能完善，它支持断点下载功能，同时支持FTP和HTTP下载方式，支持代理服务器和设置起来方便简单。

```shell
#################################################################################
# nohup.....&						# 后台下载									#
# -4								# 仅连接到IPV4地址						  #
# -c								# 断点续传									#
# -t 0								# 重试次数【0：无限重试，直到下载完成】		#
# -T 60								# 超时时间【60：超时时间为60秒】				#
# https://....jar					# 文件地址									#
# -O ./edge-center.jar_bak			# 下载到本地后指定的资源路径和文件名			 #
#################################################################################
nohup wget -4c -t 0 -T 60 https://bird-common.oss-cn-shanghai.aliyuncs.com/release/gateway-proxy/edge-center.jar -O ./edge-center.jar_bak &
```

##### 相关参数

###### 基础下载参数

| 参数 | 说明                                  |
| ---- | ------------------------------------- |
| -o   | 将软件输出信息保存到文件              |
| -a   | 将软件输出信息追加到文件              |
| -d   | 打印大量调试信息                      |
| -q   | 安静下载                              |
| -i   | 下载在本地或外部文件中的URL           |
| -t   | 重试次数【0：无限重试，直到下载完成】 |
| -O   | 下载到本地后指定的资源路径和文件名    |
| -c   | 断点续传                              |
| -S   | 打印服务器响应                        |
| -T   | 超时时间【60：超时时间为60秒】        |
| -4   | 仅连接到 IPv4 地址                    |
| -6   | 仅连接到 IPv6 地址                    |
| -nd  | 不创建目录                            |
| -x   | 强制创建目录                          |
| -r   | 指定递归下载                          |
| -l   | 最大递归深度（0：无限）               |

###### HTTP相关选项

| 参数                  | 说明                                   |
| --------------------- | -------------------------------------- |
| --http-user=USER      | 将 http 用户设置为 USER                |
| --http-password=PASS  | 设置 http 密码为 PASS                  |
| --no-cache            | 禁止服务器缓存数据                     |
| --adjust-extension    | 保存具有适当扩展名的 HTML/CSS 文档     |
| --ignore-length       | 忽略“Content-Length”标头字段           |
| --proxy-user=USER     | 将 USER 设置为代理用户名               |
| --proxy-password=PASS | 将 PASS 设置为代理密码                 |
| --referer=URL         | 在 HTTP 请求中包含 `Referer: URL' 标头 |
| --user-agent=AGENT    | 标识为 AGENT 而不是 Wget/VERSION       |
| --no-http-keep-alive  | 禁用 HTTP keep-alive（持久连接）       |
| --no-cookies          | 不使用cookies                          |
| --post-data=STRING    | 使用 POST 方法；发送 STRING 作为数据   |
| --post-file=FILE      | 使用 POST 方法；发送 FILE 的内容       |
| --content-on-error    | 输出接收到的服务器错误内容             |

###### HTTPS相关选项

| 参数                    | 说明                                                         |
| ----------------------- | ------------------------------------------------------------ |
| --secure-protocol=PR    | 选择安全协议，自动、SSLv2 之一。【SSLv3、TLSv1、TLSv1_1、TLSv1_2】 |
| --no-check-certificate  | 不验证服务器的证书                                           |
| --certificate=FILE      | 客户端证书文件                                               |
| --certificate-type=TYPE | 客户端证书类型，PEM 或 DER                                   |
| --private-key=FILE      | 私钥文件                                                     |
| --private-key-type=TYPE | 私钥类型，PEM 或 DER                                         |
| --ca-certificate=FILE   | 包含 CA 包的文件                                             |
| --ca-directory=DIR      | 存储 CA 哈希列表的目录                                       |

###### FTP选项

| 参数                   | 说明                               |
| ---------------------- | ---------------------------------- |
| --ftp-user=USER 将     | ftp 用户设置为 USER                |
| --ftp-password=PASS    | 将 ftp 密码设置为 PASS             |
| --no-remove-listing    | 不删除“.listing”文件               |
| --no-glob              | 关闭 FTP 文件名通配                |
| --no-passive-ftp       | 禁用“被动”传输模式                 |
| --preserve-permissions | 保留远程文件权限                   |
| --retr-symlinks        | 递归时，获取链接到文件（不是目录） |



#### mwget

>   wget相对于浏览器来说，速度会比较偏慢，特别是国外的网站。mwget是一个多线程下载应用，可以明显提高下载速度。

```shell
mwget -c 0 -n 3 https://bird-common.oss-cn-shanghai.aliyuncs.com/release/gateway-proxy/edge-center.jar -d ./ -f edge-center.jar_bak
```

![image-20220805152302806](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2022/08/20220805152409249.png)

##### 安装

```shell
##### 下载安装包
wget http://jaist.dl.sourceforge.net/project/kmphpfm/mwget/0.1/mwget_0.1.0.orig.tar.bz2
##### 安装gcc
yum install bzip2 gcc-c++ openssl-devel intltool -y
##### 解压
bzip2 -d mwget_0.1.0.orig.tar.bz2
tar -xvf mwget_0.1.0.orig.tar 
cd mwget_0.1.0.orig
##### 编译与安装
./configure
make
make install
```

##### 相关参数

| 参数 | 说明                                            |
| ---- | ----------------------------------------------- |
| -b   | 显示调试信息                                    |
| -c   | 重试次数【0：无限重试，直到下载完成；默认为99】 |
| -d   | 设置本地目录                                    |
| -f   | 设置本地文件名                                  |
| -i   | 设置 ftp 重试间隔时间，默认为5秒                |
| -n   | 使用连接个数，默认为4个连接                     |
| -r   | 在 HTTP 请求中包含 `Referer: [URL]' 标头        |
| -t   | 设置连接超时时间，默认为：30                    |

