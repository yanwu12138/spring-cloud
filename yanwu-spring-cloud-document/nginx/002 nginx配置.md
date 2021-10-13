### nginx配置

>   nginx配置文件所在目录：`/usr/local/nginx/conf/nginx.conf`

#### 使用ngixn搭建文件服务器

```shell
##### cat ../conf/nginx.conf
user  root;
worker_processes  1;
pid /usr/local/nginx/logs/nginx.pid;
events {
    worker_connections  1024;
}

http {
    include       mime.types;
    default_type  application/octet-stream;
    sendfile        on;
    keepalive_timeout  65;

    ##### 文件服务器 #####
    server {
		listen 80 default_server;
		server_name localhost;
        charset utf-8;
		location / {
	    	root /yanwu/file;
    	    autoindex on;               ##### 显示目录
    	    autoindex_exact_size on;    ##### 显示文件大小
    	    autoindex_localtime on;     ##### 显示文件时间
        }
    }
}
```

