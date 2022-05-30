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



#### Linux

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
./configure --enable-shared --prefix=/usr/local/ffmpeg
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



### 命令

#### ffmpeg

>   命令是在ffmpeg.exe可执行文件环境下执行，ffmpeg.exe用于音视频的转码，加水印，去水印，视频剪切，提取音频，提取视频，码率控制等等功能。



#### ffplay

>   
