#### 本文档使用docker容器运行MySQL:5.7版本

##### docker拉取MySQL:5.7镜像

```shell
docker pull mysql:5.7
```

##### 查看镜像是否拉取成功

```shell
docker image ls
```

##### MySQL容器启动脚本

```shell
#######################################################
# Js_2643.                    # root账号密码
# ~/mysql/data:/var/lib/mysql # mysql数据文件存放位置
# mysql:5.7                   # 镜像名称与镜像版本
# 3306:3306                   # 容器端口映射
# restart=always              # 自动重启mysql容器
#######################################################
docker run -d --restart=always --name mysql -v ~/mysql/data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=Js_2643. -p 3306:3306 mysql:5.7
```

##### 进入MySQL容器

```shell
docker exec -ti mysql bash
```

##### 开启远程连接

```shell
### 登陆mysql
mysql -uroot -pJs_2643\.
### 开启远程连接
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY 'Js_2643.' WITH GRANT OPTION;
FLUSH PRIVILEGES;
EXIT;
```

##### 解决MySQL中文乱码问题

-   给容器[安装vim命令](./docker安装vim.md)

-   修改`/etc/mysql/my.cnf`文件，加入以下配置

    ```shell
    [mysql]
    default-character-set=utf8
    [mysql.server]
    default-character-set=utf8
    [mysqld_safe]
    default-character-set=utf8
    [client]
    default-character-set=utf8
    [mysqld]
    character_set_server=utf8
    init_connect='SET NAMES utf8'
    default-time-zone='+8:00'
    lower_case_table_names=1
    ```

##### 重启MySQL容器

```shell
docker restart mysql
```

![image-20210105142910577](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021010514291010.png)