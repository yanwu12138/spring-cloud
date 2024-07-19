### FFMPEG

### 安装

#### Windows

##### 下载

-   官网：https://ffmpeg.org/download.html
-   GitHub：https://github.com/BtbN/FFmpeg-Builds/releases

​	![image-20220527145809355](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2022/05/20220527145809484.png)

##### 解压后配置环境变量

![image-20220527145947612](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2022/05/20220527145947699.png)

##### 检查安装是否成功

```shell
ffmpeg -version
```

![image-20220527150116493](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2022/05/20220527150116622.png)



#### Linux（编译安装）

##### 下载

-   地址：https://johnvansickle.com/ffmpeg/release-source/

    ![image-20220526175715385](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2022/05/20220526175715663.png)

```shell
cd /use/local/
wget https://johnvansickle.com/ffmpeg/release-source/ffmpeg-4.1.tar.xz
```

##### 解压与重命名

```shell
tar -xvJf ffmpeg-4.1.tar.xz
mv ffmpeg-4.1 ffmpeg
```

![image-20220526180342955](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2022/05/20220526180343015.png)

##### 安装gcc & yasm

```shell
cd ffmpeg
yum install gcc -y
yum install yasm -y
```

##### 编译与安装

```shell
./configure --enable-shared --prefix=/usr/local/ffmpeg  --enable-openssl
make && make install
```

##### 修改文件: /etc/ld.so.conf

```shell
vim /etc/ld.so.conf
##### 插入下面这行到文件末尾
/usr/local/ffmpeg/lib/
##### 保存后执行命令
ldconfig
```

![image-20220526180314609](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2022/05/20220526180314651.png)

##### 配置环境变量

```shell
vim /etc/profile
##### set ffmpeg environment
PATH=$PATH:/usr/local/ffmpeg/bin
export PATH
##### 保存后执行命令
source /etc/profile
```

##### 检查安装是否成功

```shell
ffmpeg -version
```

![image-20220526180239553](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2022/05/20220526180239606.png)



#### Linux（yum安装）

> 使用 `yum` 从 `RPM Fusion` 存储库安装

- `RPM Fusion` 存储库依赖于`EPEL`软件存储库，如果您的系统上未启用`EPEL`，请使用以下命令：

```shell
sudo yum install epel-release
```

- 接下来，通过安装 `rpm` 包启用` RPM Fusion` 存储库 ：

```shell
sudo yum localinstall --nogpgcheck https://download1.rpmfusion.org/free/el/rpmfusion-free-release-7.noarch.rpm
```

- 启用存储库后，安装` FFmpeg`：

```shell
sudo yum install ffmpeg ffmpeg-devel
```

- 通过检查其版本来验证 `FFmpeg` 安装：

```shell
ffmpeg -version
```



### 命令

#### ffmpeg

>   命令是在ffmpeg.exe可执行文件环境下执行，ffmpeg.exe用于音视频的转码，加水印，去水印，视频剪切，提取音频，提取视频，码率控制等等功能。

```shell
ffmpeg -i https://monitorFlow.boxingtong.net/rtp/33020000001180001095_33020000001320003878.live.flv -vf "drawtext=fontfile=/usr/share/fonts/chinese/myfonts.ttc:text='顺行1':x=20:y=100:fontsize=30:fontcolor=white,drawtext=text='lat 48° 32′ 43′′ N':x=20:y=140:fontsize=30:fontcolor=white,drawtext=text='lon 161° 53′ 41′′ E':x=20:y=175:fontsize=30:fontcolor=white" -c:v h264 -f flv rtmp://47.116.178.27:1985/live/aaaaaa

ffmpeg -i https://monitorFlow.boxingtong.net/live/79881-1024/hls.m3u8 -vf "drawtext=fontfile=/usr/share/fonts/chinese/myfonts.ttc:text='顺行1':x=20:y=100:fontsize=30:fontcolor=white,drawtext=text='lat 48° 32′ 43′′ N':x=20:y=140:fontsize=30:fontcolor=white,drawtext=text='lon 161° 53′ 41′′ E':x=20:y=175:fontsize=30:fontcolor=white" -c:v h264 -pix_fmt yuv420p -preset veryfast -g 48 -sc_threshold 0 -b:v 2500k -maxrate 4500k -bufsize 7500k -c:a aac -b:a 160k -ac 2 -ar 44100 -f flv rtmp://127.0.0.1:1985/live/aaaaaa

http://47.116.178.27:11936/live?port=1985&app=live&stream=33020000001320005278-0


rtmp://47.100.10.179:1935/rtp/33020000001180001493_33020000001320005505
https://monitorFlow.boxingtong.net/rtp/33020000001180001095_33020000001320003878.live.flv


https://monitorFlow.boxingtong.net/live/68751-1024.live.flv
https://monitorFlow.boxingtong.net/live/68751-1024/hls.m3u8
rtmp://47.100.10.179:1935/rtp/68751-1024
```





#### ffplay

>   
