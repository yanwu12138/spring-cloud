#### gataway

##### 图1：青龙主站了两台服务器在`12:02`分左右进行了主备切换

![image-20210929152748242](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/09/2021092915274848.png)

##### 图2：主备服务器的message日志

![image-20210929153247720](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/09/2021092915324747.png)

##### 图3：主备服务器的时钟

![image-20210929153512345](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/09/2021092915351212.png)

##### 图4：青龙·主 - 切换过程

![image-20210929172542909](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/09/2021092917254343.png)

##### 图5：青龙·备 - 切换过程

![image-20210929172626668](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/09/2021092917262626.png)

##### 图6：GC日志截取

![image-20210929162259358](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/09/2021092916225959.png)

##### 前提

-   青龙主站服务器的备机比主机时钟快了20秒左右

-   keepalived主配置

    ```shell
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
    	interface em1									##### 网卡名称
    	track_interface {
    		em1											##### 网卡名称
    	}
    	virtual_router_id 100
    	mcast_src_ip 10.193.48.85						##### 本机IP
    	priority 50										##### 主的权重
    	advert_int 1
    	authentication {
    		auth_type PASS								##### 校验方式
    		auth_pass 1111								##### 密码，可以随意，但是主备必须一致
    	}
    	virtual_ipaddress {
    		10.193.48.84/28								##### 虚拟IP
    	}
    	track_script {
    		check_running
    	}
    	notify_master /etc/keepalived/MASTER.sh			##### 切换成主的脚本
    	notify_backup  /etc/keepalived/BACKUP.sh		##### 切换成备的脚本
    }
    ```

-   keepalived备配置

    ```shell
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
    	interface em1									##### 网卡名称
    	track_interface {
    		em1											##### 网卡名称
    	}
    	virtual_router_id 100
    	mcast_src_ip 10.193.48.86						##### 本机IP
    	priority 45										##### 备的权重
    	advert_int 1
    	authentication {
    		auth_type PASS								##### 校验方式
    		auth_pass 1111								##### 密码，可以随意，但是主备必须一致
    	}
    	virtual_ipaddress {
    		10.193.48.84/28								##### 虚拟IP
    	}
    	track_script {
    		check_running
    	}
    	notify_master /etc/keepalived/MASTER.sh			##### 切换成主的脚本
    	notify_backup  /etc/keepalived/BACKUP.sh		##### 切换成备的脚本
    }
    ```

-   其它脚本

    -   检测服务状态脚本：RUNTIME_CHECK.sh

        ```shell
        #!/bin/bash
        count=`curl 127.0.0.1:7101/lookup`
        if [ "$count" = "true" ]; then
           echo $count
           exit 0
        fi
        echo 1
        exit 1
        ```

    -   切换成主脚本：MASTER.sh

        ```shell
        #!/bin/bash
        curl 127.0.0.1:7101/master
        exit 0
        ```

    -   切换成备脚本：BACKUP.sh

        ```shell
        #!/bin/bash
        curl 127.0.0.1:7101/backup
        exit 0
        ```

        

##### 事件过程

-   `2021-09-29 12:02:49.474` - 备机被设置为：`master`
-   `2021-09-29 12:02:48.949` - 主机被设置为：`master`
-   `2021-09-29 12:02:48.966` - 主机被设置为：`backup`
-   `2021-09-29 12:04:06.683` - 备机被设置为：`backup`

-   在最后一次备机被切换为`backup`后，主机并未切换成`master`，此时对于`server-control`来讲，没有`master`，所以消息到了`server-control`就没办法往下走了，因为找不到`gateway-proxy`，导致主站响应的消息无法下发，导致业务异常
-   直到`021-09-29 13:31:05.919`重启主机，业务恢复正常

##### 问题排查

-   结合**图6**，检查`GC`日志，在`12:02`分左右，并未发现有因为`GC`导致的长时间`STW`，所以排除`GC`问题

-   结合**图4**，从`messages`日志来看，主机在`12:02:26`时，调用检测脚本超时，从而导致主备切换
-   结合**图4、图5**，可以得出结论，当某台服务器被切换成`master`后，其与`keepalived`相关的接口调用会比较慢
-   结合**图4**：
    -   message日志：`12:02:29`切换为`backup`；`12:02:47`切换为`master`
    -   主站服务日志：`12:02:48.949`切换为`master`；`12:02:48.966`切换为`backup`
-   结合**图5**：
    -   message日志：`12:02:49`切换为`master`；`12:03:07`切换为`backup`
    -   主站服务日志：`12:02:49.474`切换为`master`；`12:04:06.683`切换为`backup`

##### 原因

当某台服务器状态为`master`时，业务处理比较慢，业务需要排队。在`keepalived`进行状态轮询调用`/lookup`接口时，可能会超时。如果超时则会进行主备切换。由于在调用`/master`或`/backup`接口之前，各个服务器还是自己本身之前的状态，所以状态为`master`的那台服务器处理主备切换操作也会需要排队然后争抢锁，当处理`/master`接口的线程先抢到锁时，主备切换的执行顺序就会被打乱。从而导致两台服务器状态都被切换为`backup`的状态。

##### 解决方案

1.   改变检测脚本：由原本的接口调用改为判断`7101`端口（主站服务端口）是否启用，避免调用`/lookup`接口超时，从而降低主在正常运行时的主备切换概率

     ```shell
     #!/bin/bash
     count=`netstat -anp | grep 7101 | grep java | wc -l`
     if [ "$count" -eq 0 ]; then 
        echo 1
        exit 1
     fi
     echo 0
     exit 0
     ```

2.   给`/master`或`/backup`单独开出一个单线程的线程池，队列为长度为2，丢弃策略为丢弃最老的任务，当又出现上面的问题时，保证服务器总能根据`keepalived`调用接口的时间顺序，切换到最终的状态。

