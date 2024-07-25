#### 给docker容器安装vim编辑命令

##### 进入容器

```shell
docker exec -it {CONTAINER ID} /bin/bash
```

##### 修改安装源

```shell
##### 进入目录
cd /etc/apt
##### 备份原有安装源文件
cp sources.list sources.list.bak
##### 修改安装源
cat > /etc/apt/sources.list <<EOF
deb http://mirrors.aliyun.com/ubuntu/ focal main restricted universe multiverse
deb-src http://mirrors.aliyun.com/ubuntu/ focal main restricted universe multiverse
deb http://mirrors.aliyun.com/ubuntu/ focal-security main restricted universe multiverse
deb-src http://mirrors.aliyun.com/ubuntu/ focal-security main restricted universe multiverse
deb http://mirrors.aliyun.com/ubuntu/ focal-updates main restricted universe multiverse
deb-src http://mirrors.aliyun.com/ubuntu/ focal-updates main restricted universe multiverse
deb http://mirrors.aliyun.com/ubuntu/ focal-proposed main restricted universe multiverse
deb-src http://mirrors.aliyun.com/ubuntu/ focal-proposed main restricted universe multiverse
deb http://mirrors.aliyun.com/ubuntu/ focal-backports main restricted universe multiverse
deb-src http://mirrors.aliyun.com/ubuntu/ focal-backports main restricted universe multiverse
EOF
```

##### 安装vim

```SHELL
apt-get update
apt-get install -y vim
```

##### vim复制粘贴配置

>   编辑文件 `vim ~/.vimrc` ，加入以下代码

```shell
if has('mouse')
    set mouse-=a 
endif
```

