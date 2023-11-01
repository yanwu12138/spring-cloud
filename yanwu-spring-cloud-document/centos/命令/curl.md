### CURL

>curl 是一种命令行工具，作用是发出网络请求，然后获取数据，显示在"标准输出"（stdout）上面。它支持多种协议，下面列举其常用功能。

#### 查看网页源代码

-   直接在 curl 命令后加上网址，就可以看到网页源码。以网址 [www.sina.com](https://link.jianshu.com/?t=http://www.sina.com) 为例（选择该网址，主要因为它的网页代码较短）。

    ```shell
    curl www.sina.com
    ```

    ![image-20211019112409043](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/10/202110191124099.png)

-   如果要把这个网页保存下来，可以使用 `-o` 参数：

    ```shell
    curl -o sina.html www.sina.com
    ```

    ![image-20211019112530859](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/10/2021101911253030.png)

#### 自动跳转

-   有的网址是自动跳转的。使用 `-L` 参数，`curl` 就会跳转到新的网址。

    ```shell
    curl -L www.sina.com
    ```

#### 显示头信息

-   `-i` 参数可以显示 `http response` 的头信息，连同网页代码一起。`-I` 参数则只显示 `http response` 的头信息。

    ```shell
    curl -i www.sina.com
    ```

    ![image-20211019112915831](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/10/2021101911291515.png)

#### 显示通信过程

-   `-v` 参数可以显示一次 `http` 通信的整个过程，包括端口连接和 `http request` 头信息。

    ```shell
    curl -v www.sina.com
    ```

    ![image-20211019113025752](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/10/2021101911302525.png)

-    如果觉得上面的信息还不够，那么下面的命令可以查看更详细的通信过程。

```shell
curl --trace output.txt www.sina.com
```

或

```shell
curl --trace-ascii output.txt www.sina.com
```

#### 发送表单信息

发送表单信息有 `GET` 和 `POST` 两种方法。

-   `GET` 方法相对简单，只要把数据附在网址后面就行。

    ```shell
    curl example.com/form.cgi?data=xxx
    ```

-   `POST` 方法必须把数据和网址分开，`curl` 就要用到 `--data` 或者 `-d` 参数。

    ```shell
    curl -X POST --data "data=xxx" example.com/form.cgi
    ```

-   如果数据没有经过表单编码，还可以让 `curl` 编码，参数是 `--data-urlencode`。

    ```shell
    curl -X POST--data-urlencode "date=April 1" example.com/form.cgi
    ```

-   示例

    ```shell
    curl -H "Content-Type: application/json" -X POST -d "{\"minDistance\":9260,\"maxThreshold\":500,\"remind1Threshold\":300,\"remind2Threshold\":200,\"alarm1Threshold\":100,\"alarm2Threshold\":50}" http://127.0.0.1:7802/config/setCollisionThreshold
    ```

#### HTTP动词

-   `curl` 默认的 `HTTP` 动词是 `GET`，使用 `-X` 参数可以支持其他动词。

    ```shell
    curl -X POST www.example.com
    curl -X DELETE www.example.com
    ```

#### User Agent字段

这个字段是用来表示客户端的设备信息。服务器有时会根据这个字段，针对不同设备，返回不同格式的网页，比如手机版和桌面版。

-   浏览器的 `User Agent` 是：

    ```shell
    Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.84 Safari/537.36
    ```

-   `curl` 可以这样模拟：

    ```shell
    curl --user-agent "[User Agent]" [URL]
    ```

#### cookie

-   使用 `--cookie` 参数，可以让 `curl` 发送 `cookie`。至于具体的 `cookie` 的值，可以从 `http response` 头信息的 `Set-Cookie` 字段中得到。

    ```shell
    curl --cookie "name=xxx" www.example.com
    ```

#### 增加头信息

-   有时需要在 http request 之中，自行增加一个头信息。`--header` 参数就可以起到这个作用。

    ```shell
    curl --header "Content-Type:application/json" http://example.com
    ```

#### HTTP认证

-   有些网域需要 `HTTP` 认证，这时 `curl` 需要用到 `--user` 或者 `-u` 参数。

    ```shell
    curl --user name:password example.com
    ```



```shell
curl -X GET --header "Authorization:Basic Z3Vlc3Q6Z3Vlc3RndWVzdA==" http://192.168.0.1:9000/RestApi/AntennaController/Monitoring
```

