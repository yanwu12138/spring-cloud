#### 给docker容器安装vim编辑命令

##### 进入容器

```shell
docker exec -it {CONTAINER ID} /bin/bash
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
