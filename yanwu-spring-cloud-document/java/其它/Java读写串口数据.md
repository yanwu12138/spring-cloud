### 使用java读取串口数据

#### pom依赖

```xml
<dependency>
	<groupId>org.bidib.jbidib.org.qbang.rxtx</groupId>
	<artifactId>rxtxcomm</artifactId>
	<version>2.2</version>
</dependency>
```



#### 配置动态库

##### 下载动态库

地址：[http://fizzed.com/oss/rxtx-for-java](http://fizzed.com/oss/rxtx-for-java)

##### [Windows](./ext/rxtx2.2/Windows)

| 动态库文件       | 存放目录             |
| ---------------- | -------------------- |
| rxtxParallel.dll | {JAVA_HOME}/jre/bin/ |
| rxtxSerial.dll   | {JAVA_HOME}/jre/bin/ |
| RXTXcomm.jar     | {JAVA_HOME}/jre/lib/ |

##### [Linux](./ext/rxtx2.2/Linux)

>   [Linux定位JDK目录](../../centos/centos定位JDK目录.md)

| 动态库文件       | 存放目录                   |
| ---------------- | -------------------------- |
| librxtxSerial.so | {JAVA_HOME}/jre/lib/amd64/ |
| RXTXcomm.jar     | {JAVA_HOME}/jre/lib/       |

##### MacOS

..........



#### 代码示例

##### channel（串口监听初始化）

```java
import com.yanwu.spring.cloud.netty.config.NettyConfig;
import com.yanwu.spring.cloud.netty.handler.SerialPortHandler;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.Enumeration;
import java.util.TooManyListenersException;

/**
 * @author Baofeng Xu
 * @date 2021/7/29 10:31.
 * <p>
 * description:
 */
@Slf4j
@Component
public class SerialPortServer {
    /*** 串口号 ***/
    @Value("${serialport.serialNumber:COM3}")
    private String serialNumber;
    /*** 波特率 ***/
    @Value("${serialport.baudRate:11255}")
    private Integer baudRate;
    /*** 校验位 ***/
    @Value("${serialport.checkoutBit:0}")
    private Integer checkoutBit;
    /*** 数据位 ***/
    @Value("${serialport.dataBit:8}")
    private Integer dataBit;
    /*** 停止位 ***/
    @Value("${serialport.stopBit:1}")
    private Integer stopBit;

    @Getter
    private SerialPort serialPort;

    @Resource
    private SerialPortHandler serialPortHandler;

    @PostConstruct
    public void init() {
        // ----- 获取系统中所有的通讯端口
        Enumeration portList = CommPortIdentifier.getPortIdentifiers();
        boolean exists = false;
        while (portList.hasMoreElements()) {
            CommPortIdentifier commPortId = (CommPortIdentifier) portList.nextElement();
            //  ----- 判断是否是串口 && 比较串口名称是否是指定串口
            if (commPortId.getPortType() == CommPortIdentifier.PORT_SERIAL && serialNumber.equals(commPortId.getName())) {
                exists = true;
                // ----- 打开串口
                try {
                    // ----- open:（应用程序名【随意命名】; 阻塞时等待的毫秒数）
                    serialPort = (SerialPort) commPortId.open("serial-port-channel", 2000);
                    // ----- 设置串口监听
                    serialPort.addEventListener(serialPortHandler);
                    // ----- 设置串口数据时间有效(可监听)
                    serialPort.notifyOnDataAvailable(true);
                    // ----- 设置串口通讯参数:波特率，数据位，停止位,校验方式
                    serialPort.setSerialPortParams(baudRate, dataBit, stopBit, checkoutBit);
                    log.info("serial port listener success. serialPort: {}", serialNumber);
                } catch (PortInUseException e) {
                    throw new RuntimeException("The serial port is occupied");
                } catch (TooManyListenersException e) {
                    throw new RuntimeException("Too many listeners");
                } catch (UnsupportedCommOperationException e) {
                    throw new RuntimeException("Unsupported COMM port operation is abnormal");
                }
                // 结束循环
                break;
            }
        }
        // 若不存在该串口则抛出异常
        if (!exists) {
            log.error("this serial port does not exist. serialPort: {}", serialNumber);
        }
    }

    /**
     * 关闭串口
     */
    @PreDestroy
    public void closeSerialPort() {
        if (serialPort != null) {
            serialPort.notifyOnDataAvailable(false);
            serialPort.removeEventListener();
            serialPort.close();
            serialPort = null;
        }
    }
}
```

##### handler（串口数据读写操作）

```java
import com.yanwu.spring.cloud.common.utils.ByteUtil;
import com.yanwu.spring.cloud.netty.server.SerialPortServer;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Baofeng Xu
 * @date 2021/7/29 10:48.
 * <p>
 * description:
 */
@Slf4j
@Component
public class SerialPortHandler implements SerialPortEventListener {

    @Resource
    private SerialPortServer serialPortServer;

    @Override
    public void serialEvent(SerialPortEvent event) {
        switch (event.getEventType()) {
            // ===== 解析串口读取的数据
            // ----- 通讯中断
            case SerialPortEvent.BI:
                // ----- 溢位错误
            case SerialPortEvent.OE:
                // ----- 帧错误
            case SerialPortEvent.FE:
                // ----- 奇偶校验错误
            case SerialPortEvent.PE:
                // ----- 载波检测
            case SerialPortEvent.CD:
                // ----- 清除发送
            case SerialPortEvent.CTS:
                // ----- 数据设备准备好
            case SerialPortEvent.DSR:
                // ----- 响铃侦测
            case SerialPortEvent.RI:
                // ----- 输出缓冲区已清空
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
            default:
                break;
            // ===== 有数据到达，调用读取数据的方法
            case SerialPortEvent.DATA_AVAILABLE:
                readMessage();
                break;
        }
    }

    /**
     * 读取串口数据
     */
    public void readMessage() {
        try (InputStream inputStream = serialPortServer.getSerialPort().getInputStream()) {
            byte[] bytes = new byte[inputStream.available()];
            while (inputStream.available() > 0) {
                inputStream.read(bytes);
            }
            log.info("reader message: {}", new String(bytes));
        } catch (Exception e) {
            log.error("read serial port message error.", e);
        }
    }

    /**
     * 发送信息到串口
     */
    public void sendMessage(String data) {
        byte[] writerBuffer = ByteUtil.strToAsciiBytes(data);
        try (OutputStream outputStream = serialPortServer.getSerialPort().getOutputStream()) {
            outputStream.write(writerBuffer);
            outputStream.flush();
            log.info("sender message: {}", data);
        } catch (NullPointerException e) {
            throw new RuntimeException("Can't find the serial port.");
        } catch (IOException e) {
            throw new RuntimeException("IO exception occurred when sending information to the serial port.");
        }
    }
}
```



#### 串口测试

##### 虚拟串口

现有笔记本一般情况下都没有串口的支持，此时，如果需要做串口数据的模拟测试，就需要虚拟串口出来。

`vspdconfig`安装包：`./../../tool/vspd.exe`

![image-20210820162030145](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/08/2021082016203030.png)

##### 串口调试工具

工具：`./../../tool/UartAssist.exe`

![image-20210820163211188](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/08/2021082016321111.png)
