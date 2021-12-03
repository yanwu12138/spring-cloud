### CentOS安装docker

>   Docker从1.13版本之后采用时间线的方式作为版本号，分为社区版CE和企业版EE。
>
>   社区版是免费提供给个人开发者和小型团体使用的，企业版会提供额外的收费服务，比如经过官方测试认证过的基础设施、容器、插件等。
>
>   社区版按照stable和edge两种方式发布，每个季度更新stable版本，如17.06，17.09；每个月份更新edge版本，如17.09，17.10。

#### 使用官方文档安装脚本自动安装

-   卸载旧版本(如果安装过旧版本的话)

    ```SHELLE
    sudo yum remove docker  docker-common docker-selinux docker-engine
    ```

    -   安装新版本

    ```shell
    curl -fsSL https://get.docker.com | bash -s docker --mirror Aliyun
    ```

-   启动并加入开机启动

    ```SHELL
    sudo systemctl start docker
    sudo systemctl enable docker
    sudo systemctl daemon-reload
    ```

-   验证安装是否成功(有client和service两部分表示docker安装启动都成功了)

    ```SHELL
    docker version
    ```

    ![](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021011816063636.png)



#### 手动安装docker

-   Docker 要求 CentOS 系统的内核版本高于 3.10 ，查看本页面的前提条件来验证你的CentOS 版本是否支持 Docker 。

    ```SHELL
    ### 通过 uname -r 命令查看你当前的内核版本
    uname -r
    ```

-   使用 `root` 权限登录 Centos。确保 yum 包更新到最新。

    ```SHELL
    sudo yum update
    ```

-   卸载旧版本(如果安装过旧版本的话)

    ```SHELL
    sudo yum remove docker docker-common docker-selinux docker-engine
    ```

-   安装需要的软件包， yum-util 提供yum-config-manager功能，另外两个是devicemapper驱动依赖的

    ```SHELL
    sudo yum install -y yum-utils device-mapper-persistent-data lvm2
    ```

-   设置yum源

    ```SHELL
    ### 官方镜像
    sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
    ### 阿里云镜像
    sudo yum-config-manager --add-repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
    ```

-   可以查看所有仓库中所有docker版本，并选择特定版本安装

    ```SHELL
    yum list docker-ce --showduplicates | sort -r
    ```

-   安装docker

    ```SHELL
    ### 默认安装最高版本
    sudo yum install docker-ce -y
    ```

-   启动并加入开机启动

    ```shell
    sudo systemctl start docker
    sudo systemctl enable docker
    sudo systemctl daemon-reload
    ```

-   验证安装是否成功(有client和service两部分表示docker安装启动都成功了)

    ```shell
    docker version
    ```

    ![image-20210118160635947](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/01/2021011816063636.png)