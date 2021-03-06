### RocketMQ

#### 安装

##### 环境变量配置

安装rocketMQ前需安装JDK1.7或以上版本

##### 安装RocketMQ

###### 下载安装包

​	安装包地址: ../tool/alibaba-rocketmq-3.2.6.tar.gz

###### 安装

-   将安装包拷贝到对应的安装目录, 如: /usr/local/src/

```shell
cd /usr/local/src
tar -zxvf alibaba-rocketmq-3.2.6.tar.gz
mv alibaba-rocketmq rocketmq
```

-   进入`rocketMQ`的`bin`核心命令文件目录

```shell
cd rocketmq/bin/
```

-   设置可执行权限

```shell
chmod +x mqadmin mqbroker mqfiltersrv mqshutdown  mqnamesrv
```

-   修改`JVM`参数

```shell
##### 修改 runserver.sh && runbroker.sh 的jvm参数, 根据服务器内存大小进行设置, 超出内存大小可能会报错
JAVA_OPT="${JAVA_OPT} -server -Xms512m -Xmx512m -Xmn256m -XX:PermSize=128m -XX:MaxPermSize=320m"
```

-   启动`nameServer`并指定输出日志文件

```shell
nohup sh mqnamesrv >> nameserver.log 2>&1 &
```

-   配置broker

    -   创建broker配置文件

    ```shell
    mkdir ../conf/yanwu-2m-2s-async/
    sh mqbroker -m >../conf/yanwu-2m-2s-async/broker.p
    ```

    -   修改brokerIP

    ```shell
    vim ../conf/yanwu-2m-2s-async/broker.p
    ### 显示指定为虚拟机的外网IP，不要用localhost和127.0.0.1，因为远程主机会根据brokerIP1指定的地址去访问broker
    brokerIP1=39.97.229.71
    ```

- 启动broker

```shell
nohup sh mqbroker -n 39.97.229.71:9876 -c ../conf/yanwu-2m-2s-async/broker.p >> broker.log 2>&1 &
```

-   检查`nameServer`和`broker`是否启动成功

```shell
jps
##### 有以下进程说明启动成功
## 8464 NamesrvStartup
## 8618 BrokerStartup
```

###### 关闭`nameServer`和`broker`

```shell
sh mqshutdown broker
sh mqshutdown namesrv
```

#### 测试用例

##### 依赖

```xml
<!-- alibaba rocketMQ client: version版本需和Rocker服务版本一致 -->
<dependency>
    <groupId>com.alibaba.rocketmq</groupId>
    <artifactId>rocketmq-client</artifactId>
    <version>3.2.6</version>
</dependency>
```

##### 生产者: sender

```java
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.common.message.Message;
import com.yanwu.spring.cloud.common.utils.JsonUtil;
import com.yanwu.spring.cloud.message.bo.MessageBO;
import lombok.extern.slf4j.Slf4j;
import java.sql.Timestamp;

/**
 * @author Baofeng Xu
 * @date 2020/9/22 15:48.
 * <p>
 * description: 消息生产者
 */
@Slf4j
public class Sender {
    public static void main(String[] args) throws Exception {
        // ----- 创建一个消息生产者，并设置一个消息生产者组
        DefaultMQProducer producer = new DefaultMQProducer("yanwu_producer_group");
        // ----- 指定nameServer地址
        producer.setNamesrvAddr("39.97.229.71:9876");
        // ----- 初始化producer
        producer.start();
        log.info("sender start success...");
        for (int i = 0; i < 100; i++) {
            MessageBO<String> messageBO = new MessageBO<>();
            messageBO.setMessageId(String.valueOf(i)).setData("hello message: " + i)
                    .setCreate(new Timestamp(System.currentTimeMillis()));
            // ----- topic: 主题; tag: 标签; keys: 关键字; body: 消息内容;
            Message message = new Message("yanwu_topic", "yanwu_tag", String.valueOf(i), JsonUtil.toJsonString(messageBO).getBytes());
            // ----- 发送消息并返回结果
            log.info("send message: {}, result: {}", message, producer.send(message));
        }
        // ----- 一旦生产者实例不再被使用则将其关闭，包括清理资源，关闭网络连接等
        producer.shutdown();
    }
}
```

![image-20200922180242690](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2020/09/2020092218024242.png)

##### 消费者: reader

```java
import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.MessageExt;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

/**
 * @author Baofeng Xu
 * @date 2020/9/22 15:49.
 * <p>
 * description: 消息消费者
 */
@Slf4j
public class Reader {
    public static void main(String[] args) throws Exception {
        // ----- 创建一个消息消费者，并设置一个消息消费者组
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("yanwu_consumer_group");
        // ----- 指定nameServer地址
        consumer.setNamesrvAddr("39.97.229.71:9876");
        // ----- 设置 Consumer 第一次启动时从队列头部开始消费还是队列尾部开始消费
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        // ----- 订阅指定 Topic 下的所有消息
        consumer.subscribe("yanwu_topic", "yanwu_tag || lotus_tag");
        // ----- 注册消息监听器
        consumer.registerMessageListener((MessageListenerConcurrently) (list, context) -> {
            // ----- 默认 list 里只有一条消息，可以通过设置参数来批量接收消息
            if (CollectionUtils.isNotEmpty(list)) {
                for (MessageExt ext : list) {
                    log.info("read message: {}", new String(ext.getBody()));
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        // ----- 消费者对象在使用之前必须要调用 start 初始化
        consumer.start();
        log.info("reader start success...");
    }
}
```

![image-20200922180326153](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2020/09/2020092218032626.png)

