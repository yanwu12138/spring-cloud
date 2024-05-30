### nginx配置

>   nginx配置文件所在目录：`/usr/local/nginx/conf/nginx.conf`

#### 使用ngixn搭建文件服务器

##### 文件服务器设置访问密码

-   通过openssl对密码进行加密

    ```shell
    cd /usr/local/nginx/conf/
    echo -n "{USERNAME}:" > {PASSWORD_FILE} 							##### 指定用户名
    openssl passwd {PASSWORD} >> {PASSWORD_FILE}					##### 指定密码
    ```

-   修改nginx配置文件，指定密码文件

    ```nginx
    auth_basic "Please input password"; 													##### 输入用户名密码提示框
    auth_basic_user_file /usr/local/nginx/conf/{PASSWORD_FILE};		##### 配置用户名密码验证文件路径
    ```

##### 文件服务器配置文件

```nginx
user root;
worker_processes 1;
pid /usr/local/nginx/logs/nginx.pid;
events {
    worker_connections 1024;
}

http {
    include 						mime.types;
    default_type 				application/octet-stream;
    sendfile 						on;
    keepalive_timeout 	65;

    server {
        listen 80 default_server;
        server_name localhost;
        charset utf-8;
		
        location /fileG/ {
            alias /home/file/;
            autoindex on;
            autoindex_localtime on;
            autoindex_exact_size off;
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

#### 配置HTTPS转发至HTTP

```nginx
user root;
worker_processes 1;
pid /usr/local/nginx/logs/nginx.pid;
events {
    worker_connections 1024;
}

http {
    include 						mime.types;
    default_type 				application/octet-stream;
    sendfile 						on;
    keepalive_timeout 	65;

    server {
        listen       19001 ssl http2 default_server;									##### 监听19001
        listen       [::]:19001 ssl http2 default_server;

        server_name _;
        charset utf-8;
        ssl_certificate /usr/local/nginx/conf/cert/xxx.net.pem;				##### SSL-nginx证书pem文件绝对路径
        ssl_certificate_key /usr/local/nginx/conf/cert/xxx.net.key;		##### SSL-nginx证书key文件绝对路径
        ssl_session_cache shared:SSL:1m;
        ssl_session_timeout  10m;

        location / {
            proxy_pass http://zzz.xxx.net:9001; 											##### 将HTTPS域名转发给当前域名，并将19001端口转发到9001端口
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }
    }
}
```

#### nginx反向代理

```nginx
worker_processes  1;

events {
    worker_connections  1024;
}

http {
    include 						mime.types;
    default_type 				application/octet-stream;
    sendfile 						on;
    keepalive_timeout 	65;
	
    server {
        listen       8080;
        server_name  localhost;
        location / {
            proxy_pass http://192.168.56.150:80;  ##### 配置反向代理的地址
            root   html;
            index  index.html index.htm;
        }
    }
}
```

