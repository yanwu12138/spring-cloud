#### 配置阿里云镜像

##### 登录阿里云，找到镜像加速器

![image-20210118161618866](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021011816161818.png)

##### 配置镜像

修改`daemon`配置文件`/etc/docker/daemon.json`来使用加速器

```SHELL
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": ["{加速器地址}"]
}
EOF
sudo systemctl daemon-reload
```

![image-20211127103118488](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/11/2021112710311818.png)

##### 重启docker生效

```SHELL
sudo systemctl restart docker
```

