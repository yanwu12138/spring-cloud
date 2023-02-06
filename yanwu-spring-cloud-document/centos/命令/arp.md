### 

#### 根据IP查MAC

```shell
ping {IP} -c 1 && arp -n | grep {IP}
### 或
ip neigh | grep {IP}
```

![image-20221227095823437](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2022/12/20221227095823626.png)
