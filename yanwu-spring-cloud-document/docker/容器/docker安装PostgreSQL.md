```shell
docker pull postgres


docker run -d -it --restart=always --name postgresql -v /Users/xubaofeng/devTool/docker/postgreSQL/data:/var/lib/postgresql/data -e POSTGRES_PASSWORD='yanwu12138' -e ALLOW_IP_RANGE=0.0.0.0/0 -p 5432:5432 55f125070b3d


```

