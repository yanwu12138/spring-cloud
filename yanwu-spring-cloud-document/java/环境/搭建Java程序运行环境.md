#### 运行环境配置：opt.tgz

将**环境配置文件：**`opt.tgz` 拷贝到服务器**根目录**下, 然后将 `opt.tgz` 解压:

```shell
tar -zxvf opt.tgz -C /
```



#### 应用程序配置：service.zip

将**启动脚本文件：** `service.tgz`拷贝到服务器**根目录**下, 然后将`service.tar`解压:

```shell
tar -zxvf service.tgz -C /
```

##### 修改脚本

-   修改内存配置

    ```shell
    ##### 根据机器的配置修改JVM参数
    vim /home/admin/service/bin/setenv.sh
    ## -Xms		最小堆内存
    ## -Xmx		最大堆内存
    ## -Xmn		最大永久代
    ```

-   修改服务配置文件

    ```shell
    ##### 根据环境修改对应的配置文件
    vim /home/admin/service/bin/setenv.sh
    ## -Dspring.profiles.active=***		对应环境的配置文件
    ```

-   `GC`日志配置

    >   修改`GC`日志文件名称，避免当一个服务器运行多个程序的时候，不通的程序都将`GC`日志写到同一个文件。

    -   setenv.sh

        ![image-20211019161450504](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/10/2021101916145050.png)

    -   appctl.sh

        ![image-20211019161425554](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/10/2021101916142525.png)

#### 应用程序包：service-1.0.0-SNAPSHOT.tgz

将应用程序包放入`/home/admin/service/target/`目录



#### 添加`admin`用户

```shell
##### 服务的启动需要用admin账号才行, 如果没有admin账号, 先通过 useradd 命令创建admin账号
adduser admin
##### 将对应的目录赋权给admin用户
chown -R admin:admin /home/admin
chmod -R 777 /home/admin/service/bin/
```



#### 启动

```shell
cd /home/admin/service/bin
### 启动
sudo -su admin ./appctl.sh start
### 重启
sudo -su admin ./appctl.sh restart
### 停止
sudo -su admin ./appctl.sh stop
```



#### 添加开机自启动脚本

-   在 `/home/admin/service/bin/` 目录添加 `poweron_starting.sh`

    ```shell
    #!/bin/bash
    su - admin <<EOF
    cd /home/admin/service/bin/
    source ./appctl.sh start
    exit;
    EOF
    ```

-   给 `poweron_starting.sh` 设置执行权限

    ```shell
    chmod +x poweron_starting.sh
    ```

-   在 `/etc/rc.local` 文件末尾增加以下内容

    ```shell
    vim /etc/rc.local
    # 将以下内容追加到末尾即可
    
    ########## start service ##########
    /home/admin/service/bin/poweron_starting.sh
    ```

-   给 `rc.local` 设置执行权限

    ```shell
    chmod +x /etc/rc.local
    ```

    