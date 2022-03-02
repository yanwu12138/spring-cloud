### keepalived（2.1.3）

#### 安装

##### 安装keepalived(2.1.3)

```bash
##### 安装 gcc
yum -y install gcc
yum -y install openssl-devel
yum install -y curl gcc openssl-devel libnl3-devel net-snmp-devel

##### 安装keepalived
cd /usr/local/src
wget https://www.keepalived.org/software/keepalived-2.1.3.tar.gz
tar xvf keepalived-2.1.3.tar.gz
cd keepalived-2.1.3
./configure --prefix=/usr/local/keepalived
make && make install

##### 放置默认配置
mkdir /etc/keepalived
cp /usr/local/keepalived/etc/keepalived/keepalived.conf /etc/keepalived/keepalived.conf

##### 完成后会增加这些文件
### /usr/local/etc/keepalived/keepalived.conf
### /usr/local/etc/sysconfig/keepalived
### /usr/local/sbin/keepalived

##### 配置开机自启动
systemctl enable keepalived
systemctl start keepalived

##### 放行vrrp
firewall-cmd --zone=public --add-protocol=vrrp --permanent
firewall-cmd --reload
```

##### 检测服务是否正常运行脚本

```shell
[root@localhost keepalived]# cat RUNTIME_CHECK.sh
#!/bin/bash
count=`netstat -anp | grep 7101 | grep java | wc -l`
if [ "$count" -eq 0 ]; then 
   exit 1
fi
exit 0
```

##### 切换成主的脚本

```shell
[root@localhost keepalived]# cat MASTER.sh
#!/bin/bash
curl 127.0.0.1:7101/master
exit 0
```

##### 切换成备的脚本

```shell
#!/bin/bash
curl 127.0.0.1:7101/backup
exit 0
```

##### 主·keepalived配置

![image-20210618170513616](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/06/2021061817051313.png)

```shell
[root@localhost keepalived]# cat keepalived.conf
global_defs {
	script_user root
	enable_script_security
}

vrrp_script check_running {
	script "/etc/keepalived/RUNTIME_CHECK.sh"		##### 检测服务脚本
	interval 5
	weight 10
}

vrrp_instance VI_1 {
	state MASTER									##### 主
	interface enp0s31f6								##### 网卡名称
	track_interface {
		enp0s31f6									##### 网卡名称
    }
	virtual_router_id 100
	priority 50										##### 主的权重
	advert_int 1
	authentication {
		auth_type PASS								##### 校验方式
		auth_pass 1111								##### 密码，可以随意，但是主备必须一致
	}
	virtual_ipaddress {
		172.16.0.233/32								##### 虚拟IP
	}
	track_script {
		check_running
	}
	notify_master /etc/keepalived/MASTER.sh			##### 切换成主的脚本
	notify_backup  /etc/keepalived/BACKUP.sh		##### 切换成备的脚本
}
```

##### 备·keepalived配置

![image-20210618170446088](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/06/2021061817044646.png)

```shell
[root@localhost keepalived]# cat keepalived.conf
global_defs {
	script_user root
	enable_script_security
}

vrrp_script check_running {
	script "/etc/keepalived/RUNTIME_CHECK.sh"		##### 检测服务脚本
	interval 5
	weight 10
}

vrrp_instance VI_1 {
	state BACKUP									##### 备
	interface eth0									##### 网卡名称
	track_interface {
		eth0										##### 网卡名称
	}
	virtual_router_id 100
	priority 45										##### 备的权重
	advert_int 1
	authentication {
		auth_type PASS								##### 校验方式
		auth_pass 1111								##### 密码，可以随意，但是主备必须一致
	}
	virtual_ipaddress {
		172.16.0.233/32								##### 虚拟IP
	}
	track_script {
		check_running
	}
	notify_master /etc/keepalived/MASTER.sh			##### 切换成主的脚本
	notify_backup  /etc/keepalived/BACKUP.sh		##### 切换成备的脚本
}
```



#### 卸载

##### 切换到源码所在目录进行卸载

```shell
cd /usr/local/src/keepalived-2.1.3
##### 卸载
make uninstall
```

##### 删除相关文件

```shell
rm -rf /usr/local/src/keepalived-2.1.3
rm -rf /usr/local/keepalived
rm -rf /etc/keepalived
```

