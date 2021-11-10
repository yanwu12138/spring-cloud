### nginx配置

>   nginx配置文件所在目录：`/usr/local/nginx/conf/nginx.conf`

#### 使用ngixn搭建文件服务器

##### 文件服务器设置访问密码

-   通过openssl对密码进行加密

    ```shell
    cd /usr/local/nginx/conf/
    echo -n "{USERNAMW}:" > {PASSWORD_FILE}							##### 指定用户名
    openssl passwd {PASSWORD} >> {PASSWORD_FILE}					##### 指定密码
    ```

-   修改nginx配置文件，指定密码文件

    ```shell
    auth_basic "Please input password";								##### 输入用户名密码提示框
    auth_basic_user_file /usr/local/nginx/conf/{PASSWORD_FILE};		##### 配置用户名密码验证文件路径
    ```

##### 文件服务器配置文件

```shell
##### cat /usr/local/nginx/conf/nginx.conf
user root;
worker_processes ;
pid /usr/local/nginx/logs/nginx.pid;
events {
    worker_connections 1024;
}

http {
    include mime.types;
    default_type application/octet-stream;
    sendfile on;
    keepalive_timeout 65;

    server {
		listen 80 default_server;
		server_name localhost;
        charset utf-8;
		location /fileG/ {
            alias /home/file/;
    	    autoindex on;
    	    autoindex_exact_size on;
    	    autoindex_localtime on;
        }

		location /fileR/ {
            alias /root/file/;
            autoindex on;
            autoindex_exact_size on;
            autoindex_localtime on;
	    	auth_basic "Please input password";
	    	auth_basic_user_file /usr/local/nginx/conf/admin_password;
        }

		location / {
        }
    }
}
```

#### nginx反向代理

```shell
worker_processes  1;

events {
    worker_connections  1024;
}

http {
    include       mime.types;
    default_type  application/octet-stream;

    sendfile        on;

    keepalive_timeout  65;
	
    server {
        listen       8080;
        server_name  localhost;
        location / {
        	##### 配置反向代理的地址
			proxy_pass http://192.168.56.150:80;
            root   html;
            index  index.html index.htm;
        }
    }
}
```

