## AIS相关

### VDM信息

`AIS`广播信息分类两种，即`VDM`和`VDO`。其中，`VDM`是本船收到的其它船舶的信息，而`VDO`是船舶自身的广播信息。`VDM`消息和`VDO`消息的格式完全相同。



### VDM消息格式描述

`VDM`消息可以分解为多条发送。因为`VDM`消息的语句长度是有限制的，最大不超过`82`个字节。如果压缩的通讯消息长度太长，那么，`VDM`消息必须分解为多条子消息来发送。
分解为多条发送的方法是：将通讯消息拆开到多条语句中。并且将每条消息中的”消息分解总条数”都设置为拆分成的总条数（不超过`9`个）。然后依照顺序为每条子消息编号，这个编号记录到“语句序号”中。并且用“连续消息鉴别码”来指定该条拆分消息的鉴别号。这个鉴别号依照顺序从`0`到`9`循环。

示例如下：`NMEA` 消息由多个以__逗号__分隔的字段构成

>   ```tex
>   !AIVDM,1,1,,A,18UG;P0012G?Uq4EdHa=c;7@051@,0*53
>   ```
>
>   -   !AIVDM 是 NMEA 消息类型。这告诉我们此 NMEA 消息包含通过 AIS 接收的 AIS 数据。[NMEA 0183 规范](https://www.nmea.org/Assets/20130801 0183 identifier list.pdf)中定义了其他可能的值。
>   -   第一个`“1”`告诉我们用于传输此 `AIS` 消息的 `NMEA` 消息的数量。有时它说`“2”`——但是当像我们这里的例子那样有`“1”`时，事情很容易——我们只需要考虑这个单一的句子。
>   -   第二个`“1”`告诉我们句子编号。所以这是 `NMEA` 消息编号 `1`，共 `1` 个。
>   -   第三个为暂时将忽略的空白字段。
>   -   然后是`“A”`。这告诉我们，这个 `AIS` 消息是在 `AIS` 通道 `A` 上接收到的。
>   -   重要的部分——有效载荷，看起来像这样：`“18UG;P0012G?Uq4EdHa=c;7@051@”`。这是通过 `VHF` 无线电接收到的实际 `AIS` 消息 - 此处表示为`“6 位 ASCII”`格式的字符串。我们可以称其为“编码的 `AIS` 数据”。
>   -   `“0*53”`是 `NMEA` 校验和，用于验证此消息的完整性。

总共有 27 种不同类型的消息可以通过 AIS 发送。这些都在国际电信联盟的技术出版物“ [ITU-R M.1371-5 建议书](http://www.itu.int/rec/R-REC-M.1371-5-201402-I)”中详细定义。上面的示例是类型 1 消息 。



### VDM消息类型

`VDM`消息类型信息是记录在`VDM`消息的压缩数据中的。因此必须对压缩信息进行解压后才能判断出消息类型。`VDM`消息共分为`27`种不同类型

| 消息ID码 | 名称                           |
| -------- | ------------------------------ |
| 1、2、3  | 船位报告                       |
| 4        | 基站报告                       |
| 5        | 船舶静态和航行相关数据         |
| 6        | 寻址二进制消息                 |
| 7        | 二进制确认                     |
| 8        | 二进制广播消息                 |
| 9        | 标准的SAR航空器位置报告        |
| 10       | UTC/日期询问                   |
| 11       | UTC/日期响应                   |
| 12       | 寻址安全相关信息               |
| 13       | 安全相关确认                   |
| 14       | 安全相关广播消息               |
| 15       | 询问                           |
| 16       | 指配模式命令                   |
| 17       | GNSS广播二进制消息             |
| 18       | 标准的B类设备位置报告          |
| 19       | 扩展的B类设备位置报告          |
| 20       | 数据链路管理消息               |
| 21       | 助航设备报告                   |
| 22       | 信道管理                       |
| 23       | 群组指配命令                   |
| 24       | 静态数据报告                   |
| 25       | 单时隙二进制消息               |
| 26       | 带有通信状态的多时隙二进制消息 |
| 27       | 大量程AIS广播消息              |



#### 消息类型1

消息`1`是接收到的船舶的动态信息。包含消息`1`的`VDM`消息样例如下：

```tex
!AIVDM,1,1,,A,177?s>001V8eBRhF=:l7CUI20D0T,0*40
```

其中压缩信息为：

```tex
177?s>001V8eBRhF=:l7CUI20D0T
```

压缩信息内容描述：（共`168`比特）

| 内容                 | 说明         | 位置       | 所占位数 | 取值范围     | 备注                                            |
| -------------------- | ------------ | ---------- | -------- | ------------ | ----------------------------------------------- |
| Message ID           | 信息识别码   | [0, 5]     | 6        | 1            | 信息1、2、3的标识符                             |
| Repeat Indicator     | 重复次数指示 | [6, 7]     | 2        | [0, 3]       | 指示应该重发的次数：【0：缺省0；3：不再重发】   |
| User ID              | 用户识别码   | [8, 37]    | 30       |              | MMSI编号                                        |
| Navigation status    | 航行状态     | [38, 41]   | 4        | [0, 15]      | 参考下表：【航行状态表】                        |
| Rate of turn(ROT)    | 转向率       | [42, 49]   | 8        | [-127, +127​] |                                                 |
| SOG                  | 对地航速     | [50, 59]   | 10       | [0, 1022]    | 以$1/10$节距为单位：【1023：无；1022：102.2节】 |
| Position accuracy    | 船位精确度   | [60]       | 1        | [0, 1]       | 精度：【0：低精度；1：高精度】                  |
| Longitude            | 经度         | [61, 88]   | 28       | [-180, +180] | 以$1/10000$分表示的经度【东：+；西：-】         |
| Latitude             | 纬度         | [89, 115]  | 27       | [-90, +90]   | 以$1/10000$分表示的纬度【北：+；南：-】         |
| COG                  | 对地航向     | [116, 127] | 12       | [0, 3599]    | 以$1/10$度表示的航向                            |
| True heading         | 船首真航向   | [128, 136] | 9        | [0, 359]     |                                                 |
| Time stamp           | 时间标记     | [137, 142] | 6        |              |                                                 |
| Regional Application |              | [143, 146] | 4        |              |                                                 |
| Spare                |              | [147]      | 1        |              |                                                 |
| RAIM Flag            |              | [148]      | 1        |              |                                                 |
| Communication state  |              | [149, 167] | 19       |              |                                                 |

##### 航行状态表

| 状态编号 | 说明         |
| -------- | ------------ |
| 0        | 动力航行中   |
| 1        | 锚泊         |
| 2        | 未受令       |
| 3        | 机动性受限   |
| 4        | 受吃水限制   |
| 5        | 锚链系泊     |
| 6        | 搁浅         |
| 7        | 捕捞中       |
| 8        | 风帆动力航行 |
| 9 ~ 15   | 保留         |



#### 消息类型5

消息类型`5`是接收到的船舶的静态和航行相关信息。包含消息`5`的`VDM`消息样例如下：

```tex
!AIVDM,2,1,8,A,569>;gP0000088``001TTpN0QD4000000000000t4IU7=4cG0@10H32@C`3l,0*79
!AIVDM,2,2,8,A,T1CQp30B@00,2*1A
```

消息`5`将被分解为两条连续的`AIVDM`消息进行发送，因此完整的压缩信息为：

```tex
569>;gP0000088``001TTpN0QD4000000000000t4IU7=4cG0@10H32@C`3l T1CQp30B@00
```

压缩信息内容描述：（共`424`比特）

| 内容                                      | 说明                     | 位置      | 所占位数 | 取值范围      |
| ----------------------------------------- | ------------------------ | :-------- | :------- | :------------ |
| Message ID                                | 信息识别码               | 0 - 5     | 6        | 5             |
| Repeat Indicator                          | 重复次数指示             | 6 - 7     | 2        | 0 - 3         |
| User ID                                   | MMSI编号                 | 8 - 37    | 30       |               |
| AIS version indicator                     | AIS版本                  | 38 - 39   | 2        | 0 - 3         |
| IMO number                                | IMO编号                  | 40 - 69   | 30       | 1 - 999999999 |
| Call sign                                 | 呼号                     | 70 - 111  | 42       |               |
| Name                                      | 船名                     | 112 - 231 | 120      |               |
| Type of ship and cargo type               | 船舶和货物类型           | 232 - 239 | 8        |               |
| Dimension/reference for position          | 船舶尺寸以及定位设备位置 | 240 - 269 | 30       |               |
| Type of electronic position fixing device | 定位设备类型             | 270 - 273 | 4        | 0 - 15        |
| ETA                                       | 预计航行时间             | 274 - 293 | 20       |               |
| Maximum present static draught            | 最大吃水深度             | 294 - 301 | 8        | 0 - 255       |
| Destination                               | 目的地                   | 302 - 421 | 120      |               |
| DTE                                       | 数据终端准备             | 422       | 1        |               |
| Spare                                     |                          | 423       | 1        |               |



#### VDM消息的报告频率

-   `Static information: Every 6 min or, when data has been amended, on request.`
    -   静态消息：每六分钟，或者当数据被修正
-   `Dynamic information: Dependent on speed and course alteration according to Tables 1a and b.`
    -   动态消息：根据速度和航向改变状况。
-   `Voyage related information: Every 6 min or, when data has been amended, on request.`
    -   航行相关信息：每六分钟或者当数据被修正
-   `Safety related message: As required.`
    -   安全相关信息：根据请求



### VDM的压缩码

对于`VDM`消息中的压缩码，编码格式是根据以下对照表来进行的。制定这种编码格式的目的一是为了压缩信息内容，二是要求压缩以后的信息能够以`ASCII`码显示，以便使用文本方式传输（如果直接压缩，可能会产生不可见字符，这就是一般压缩文件以二进制方式存储的原因）。

| Chr  | Binary | Dec  | Hex  | ASCII-Hex | Chr  | Binary | Dec  | Hex  | ASCII-Hex |
| :--: | :----: | :--: | :--: | :-------: | :--: | :----: | :--: | :--: | :-------: |
|  0   | 000000 |  0   | 0x00 |   0x30    |  P   | 100000 |  32  | 0x20 |   0x50    |
|  1   | 000001 |  1   | 0x01 |   0x31    |  Q   | 100001 |  33  | 0x21 |   0x51    |
|  2   | 000010 |  2   | 0x02 |   0x32    |  R   | 100010 |  34  | 0x22 |   0x52    |
|  3   | 000011 |  3   | 0x03 |   0x33    |  S   | 100011 |  35  | 0x23 |   0x53    |
|  4   | 000100 |  4   | 0x04 |   0x34    |  T   | 100100 |  36  | 0x24 |   0x54    |
|  5   | 000101 |  5   | 0x05 |   0x35    |  U   | 100101 |  37  | 0x25 |   0x55    |
|  6   | 000110 |  6   | 0x06 |   0x36    |  V   | 100110 |  38  | 0x26 |   0x56    |
|  7   | 000111 |  7   | 0x07 |   0x37    |  W   | 100111 |  39  | 0x27 |   0x57    |
|  8   | 001000 |  8   | 0x08 |   0x38    |  `   | 101000 |  40  | 0x28 |   0x60    |
|  9   | 001001 |  9   | 0x09 |   0x39    |  a   | 101001 |  41  | 0x29 |   0x61    |
|  :   | 001010 |  10  | 0x0A |   0x3A    |  b   | 11010  |  42  | 0x2A |   0x62    |
|  ;   | 001011 |  11  | 0x0B |   0x3B    |  c   | 101011 |  43  | 0x2B |   0x63    |
|  <   | 001100 |  12  | 0x0C |   0x3C    |  d   | 101100 |  44  | 0x2C |   0x64    |
|  =   | 001101 |  13  | 0x0D |   0x3D    |  e   | 101101 |  45  | 0x2D |   0x65    |
|  >   | 001110 |  14  | 0x0E |   0x3E    |  f   | 101110 |  46  | 0x2E |   0x66    |
|  ?   | 001111 |  15  | 0x0F |   0x3F    |  g   | 101111 |  47  | 0x2F |   0x67    |
|  @   | 010000 |  16  | 0x10 |   0x40    |  h   | 110000 |  48  | 0x30 |   0x68    |
|  A   | 010001 |  17  | 0x11 |   0x41    |  i   | 110001 |  49  | 0x31 |   0x69    |
|  B   | 010010 |  18  | 0x12 |   0x42    |  j   | 110010 |  50  | 0x32 |   0x6A    |
|  C   | 010011 |  19  | 0x13 |   0x43    |  k   | 110011 |  51  | 0x33 |   0x6B    |
|  D   | 010100 |  20  | 0x14 |   0x44    |  l   | 110100 |  52  | 0x34 |   0x6C    |
|  E   | 010101 |  21  | 0x15 |   0x45    |  m   | 110101 |  53  | 0x35 |   0x6D    |
|  F   | 010110 |  22  | 0x16 |   0x46    |  n   | 110110 |  54  | 0x36 |   0x6E    |
|  G   | 010111 |  23  | 0x17 |   0x47    |  o   | 110111 |  55  | 0x37 |   0x6F    |
|  H   | 011000 |  24  | 0x18 |   0x48    |  p   | 111000 |  56  | 0x38 |   0x70    |
|  I   | 011001 |  25  | 0x19 |   0x49    |  q   | 111001 |  57  | 0x39 |   0x71    |
|  J   | 011010 |  26  | 0x1A |   0x4A    |  r   | 111010 |  58  | 0x3A |   0x72    |
|  K   | 011011 |  27  | 0x1B |   0x4B    |  s   | 111011 |  59  | 0x3B |   0x73    |
|  L   | 011100 |  28  | 0x1C |   0x4C    |  t   | 111100 |  60  | 0x3C |   0x74    |
|  M   | 011101 |  29  | 0x1D |   0x4D    |  u   | 111101 |  61  | 0x3D |   0x75    |
|  N   | 011110 |  30  | 0x1E |   0x4E    |  v   | 111110 |  62  | 0x3E |   0x76    |
|  O   | 011111 |  31  | 0x1F |   0x4F    |  w   | 111111 |  63  | 0x3F |   0x77    |



## JAVA示例

### pom 依赖

```xml
<!-- AIS 相关工具 -->
<dependency>
    <groupId>dk.tbsalling</groupId>
    <artifactId>aismessages</artifactId>
    <version>2.2.3</version>
</dependency>
```

### simple demo

```java
import dk.tbsalling.aismessages.AISInputStreamReader;
import dk.tbsalling.aismessages.ais.messages.AISMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Baofeng Xu
 * @date 2021/7/5 18:22.
 * <p>
 * description: AIS 原始报文解码工具
 */
@Slf4j
public class AisUtil {

	public static void main(String[] args) throws IOException {
        log.info("-------------------- AISMessages Demo App Start --------------------");
        List<AISMessage> message = getMessage(DEMO_NMEA_STRINGS);
        message.forEach((aisMessage) -> log.info("Received AIS message: {}", aisMessage));
        log.info("-------------------- AISMessages Demo App End--------------------");
    }

    private AisUtil() {
    }

    public static List<AISMessage> getMessage(String message) {
        return StringUtils.isNotBlank(message) ? getMessage(message.getBytes()) : Collections.emptyList();
    }

    public static List<AISMessage> getMessage(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return Collections.emptyList();
        }
        List<AISMessage> result = new ArrayList<>();
        try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
            new AISInputStreamReader(inputStream, result::add).run();
        } catch (Exception e) {
            log.error("ais getMessage error. bytes: {}", ByteUtil.printHexStrByBytes(bytes), e);
        }
        return result;
    }

    private final static String DEMO_NMEA_STRINGS = "!AIVDM,1,1,,A,18UG;P0012G?Uq4EdHa=c;7@051@,0*53\n" +
            "!AIVDM,2,1,1,,539L8BT29ked@90F220I8TE<h4pB22222222220o1p?4400Ht00000000000,0*49\n" +
            "!AIVDM,2,2,1,,00000000008,2*6C\n" +
            "!AIVDM,1,1,,A,15NIrB0001G?endE`CpIgQSN08K6,0*02\n" +
            "!AIVDM,1,1,,B,152Hn;?P00G@K34EWE0d>?wN28KB,0*12\n" +
            "!AIVDM,1,1,,B,138Ngv0OinG>DFnDekIF6lkN00Rk,0*2E\n" +
            "!AIVDM,1,1,,B,15N06LPP00G?Sf6Egkh0TwwL0HKO,0*2B\n" +
            "!AIVDM,1,1,,A,15N:Ie0P00G@6VpEa4n68?wL0HKf,0*2C\n" +
            "!AIVDM,1,1,,B,15MqdBP000G@qoLEi69PVGaN0D0=,0*3A\n" +
            "!AIVDM,1,1,,B,B5NJ;PP005l4onUIsc@03woUoP06,0*3A\n" +
            "!AIVDM,1,1,,B,15Mv4a0P00G?<pHEeU59nwwN08L3,0*7D\n" +
            "!AIVDM,1,1,,A,35Ml=50Oh@o>Lf2EVPJI>nqP017A,0*51\n" +
            "!AIVDM,1,1,,A,15Mw0J0P01G?aLVE`VfaM?wN00RV,0*3B\n" +
            "!AIVDM,1,1,,B,16:252002lo=Gn8E7k?=0bGN0@LH,0*04\n" +
            "!AIVDM,1,1,,A,19NWsbP000o@58pE`8pHhSGP00SE,0*0B\n" +
            "!AIVDM,1,1,,B,35AjiT5000G@4vhE`ok8a6sR0Dbb,0*06\n" +
            "!AIVDM,1,1,,B,15MwksP000G@6TDEa501Uc5P08Cq,0*3B\n" +
            "!AIVDM,1,1,,A,15N59@PP00G?iGhEW<9P0?wL0HLg,0*3E\n" +
            "!AIVDM,1,1,,B,15N:`e0000G@6IlEa5O`V93L0@Lt,0*22\n" +
            "!AIVDM,1,1,,B,15Ms0FPP00o?arNEdfdUw?wR08M3,0*09\n" +
            "!AIVDM,1,1,,B,13U8W:002;o>lC`EWMwaaWiR8D10,0*09\n" +
            "!AIVDM,1,1,,B,35MA9T0Oino<fFPE1=cG75iR0000,0*4D\n" +
            "!AIVDM,1,1,,B,4h3Ovk1udq`Dio>jPHEdjdW008MI,0*63\n" +
            "!AIVDM,1,1,,B,4h3Ovl1udq`DioCkldEpGh70051@,0*25\n" +
            "!AIVDM,1,1,,B,4h3OvkQudq`Djo?UhFEf=Ko00<18,0*43\n" +
            "!AIVDM,1,1,,B,4h3Ovl1udq`DjoCkllEpGh70051@,0*2E\n" +
            "!AIVDM,1,1,,B,35OqO05vh0G@8GREWEmVVwwT0000,0*3D\n" +
            "!AIVDM,1,1,,B,35Ml=5000=o>LeVEVPH96ns`0000,0*50\n" +
            "!AIVDM,1,1,,B,15>gpr0PAuG=AglDjcc68Ts200S2,0*4A\n" +
            "!AIVDM,1,1,,B,18UG;P000pG?UgdEdOeeec6t08DW,0*0A\n" +
            "!AIVDM,1,1,,B,85MwpKiKf0wLgSt5BlHF<3FMlaSRCjf1?Nq;4TAA7Mj:oOH5bs=8,0*7D\n" +
            "!AIVDM,1,1,,A,152Hn;?P00G@K3HEWDot<gw82HDi,0*5B\n" +
            "!AIVDM,1,1,,B,152SGj001so?U5fEg5j8?VU808Dm,0*19\n" +
            "!AIVDM,1,1,,B,15NIrB0001G?envE`Cp9gQG80D18,0*09\n" +
            "!AIVDM,1,1,,B,15MwpWhP1so?KpFEaiOL<Ow60HE>,0*14\n" +
            "!AIVDM,1,1,,B,16:252002uo=FHHE86H=8:G600S?,0*5F\n" +
            "!AIVDM,1,1,,A,138Ngv001uG>EINDeV;654k:0@EJ,0*69\n" +
            "!AIVDM,1,1,,B,15N:Ie0P00G@6W>Ea4ollOw600S0,0*53\n" +
            "!AIVDM,1,1,,A,15N06LPP00G?SdvEgki0Tww80@ET,0*02\n" +
            "!AIVDM,1,1,,B,13U8W:002@o>ipDEWH19d7k88@El,0*5A\n" +
            "!AIVDM,1,1,,B,18UG7V0019G?ithE`a;m;D;600SB,0*2B\n" +
            "!AIVDM,1,1,,B,15MA9T001no<fEpE0wno25i:0@F7,0*49\n" +
            "!AIVDM,1,1,,B,33TWed1001G?tg@EUg3cBV?80000,0*38\n" +
            "!AIVDM,1,1,,A,15MwksP000G@6T`Ea501Ms5:0D0w,0*77\n" +
            "!AIVDM,1,1,,A,15MiuGg000o?<b6EeVq8;aW:0HF=,0*50\n" +
            "!AIVDM,1,1,,B,19NWsbP000o@59BE`8qFJ3G<0HFK,0*79\n" +
            "!AIVDM,1,1,,B,15Ml=50P@Do>LR`EVNsHQFc>00RJ,0*77\n" +
            "!AIVDM,1,1,,B,15Mw0J0P02G?aLRE`Vf`mOw<08Fd,0*32\n" +
            "!AIVDM,1,1,,A,15Mv4a0P00G?<plEeU3anww<0HFi,0*56\n" +
            "!AIVDM,1,1,,A,15N:`e0000G@6InEa5OTDq160<11,0*71\n" +
            "!AIVDM,1,1,,B,15N59@PP00G?iGhEW<9P0?w:0<16,0*13\n" +
            "!AIVDM,1,1,,A,35MA9T001no<fF6E0wVG25k>0000,0*1A\n" +
            "!AIVDM,1,1,,A,Dh3Ovk0nIN>4,0*38\n" +
            "!AIVDM,1,1,,B,15ND4kP001G@6I@Ea5AM;I3>0<0w,0*04\n" +
            "!AIVDM,1,1,,B,Dh3Ovl0sqN>4,0*19\n" +
            "!AIVDM,1,1,,A,Dh3Ovl0mUN>4,0*20\n" +
            "!AIVDM,1,1,,B,Dh3Ovk0tMN>4,0*25\n" +
            "!AIVDM,1,1,,A,Dh3Ovl0mMN>4,0*38\n" +
            "!AIVDM,1,1,,A,13:112002?o@FRnDS<bdu:E:08GQ,0*77\n" +
            "!AIVDM,1,1,,A,4h3Ovk1udq`FWo>jPHEdjdW0051H,0*2C\n" +
            "!AIVDM,1,1,,A,15N6r>P000G<dG0Esaod<:U@08GM,0*53\n" +
            "!AIVDM,1,1,,A,4h3OvkQudq`F`o?UhFEf=Ko00D1;,0*33\n" +
            "!AIVDM,1,1,,B,15Ph;00Oi@o@V?PDmKanwUaB08Gs,0*02\n" +
            "!AIVDM,1,1,,A,15Mva0P00no?Ui>EdS;MobMB08Gt,0*19\n" +
            "!AIVDM,1,1,,B,15NGH8POi8G?ii4E`bPE74?p0U1H,0*58\n" +
            "!AIVDM,1,1,,A,15MwDf0P00G?<k4EeSU@Ugw@00Sm,0*1C\n" +
            "!AIVDM,1,1,,B,15MvlfP000G?lwrEd9aJIicD0D1;,0*2B\n" +
            "!AIVDM,1,1,,A,16:252002io=FE@E87S=3:IB0<09,0*47\n" +
            "!AIVDM,1,1,,B,15MwlV0P00G@6N8Ea5FujwwD08I0,0*7B\n" +
            "!AIVDM,1,1,,A,15NGdT?001G?eWRE`E9r8QoF2D11,0*10\n" +
            "!AIVDM,1,1,,A,15ND4kP000G@6I@Ea5AGhI3D0HI6,0*69\n" +
            "!AIVDM,1,1,,B,15M67FO000G@7EHEa28cvRsF251H,0*4B\n" +
            "!AIVDM,1,1,,B,15NH7?PP00G@>aTEWwd<<wwJ0@It,0*25\n" +
            "!AIVDM,1,1,,A,15MQqQ0P00G?iH>EW<<@0?wD08J4,0*01\n" +
            "!AIVDM,1,1,,B,15NHHAP000G@rn<Ei:<5c1eJ00Ss,0*2B\n" +
            "!AIVDM,1,1,,A,15?ECL001=G<wHPEON52>QeH08JK,0*47\n" +
            "!AIVDM,1,1,,B,13:112002?o@FNbDS=Ntu:EF00ST,0*3C\n" +
            "!AIVDM,1,1,,A,15>gpr001sG=AnHDjb>V3TwF08Jd,0*72\n" +
            "!AIVDM,1,1,,A,152SGj001to?TvlEg4`H?6UL08Jo,0*36";
}
```

![image-20210706190053952](https://typroa12138.oss-cn-hangzhou.aliyuncs.com/image/2021/07/2021070619005454.png)

### aismessages 核心类

```java
/**
 * @see dk.tbsalling.aismessages.ais.messages.AISMessage
 */
public abstract class AISMessage implements Serializable, CachedDecodedValues {
    // .......
    
    /**
     * 根据NMEAMessage获取对应VDM消息类型的AISMessage对象
     */
    public static AISMessage create(NMEAMessage... nmeaMessages) {
    	// ......
    }
       
    /**
     * 校验VDM报文是否合法
     */
    public boolean isValid() {
        // ......
    }
        
    /**
     * VDM的压缩码
     */
    private final static Map<String, String> charToSixBit = new TreeMap<>();
    static {
        // ......
    }
    
    // .......
}
```



## 参考文档

-   https://wenku.baidu.com/view/9baec600996648d7c1c708a1284ac850ad0204b3.html
-   https://wenku.baidu.com/view/d10419c058f5f61fb73666f3.html?rec_flag=default&fr=pc_newview_relate-1001_1-3-wk_rec_doc2-1001_1-6-d10419c058f5f61fb73666f3&sxts=1625540902052
-   https://blog.csdn.net/happyparrot/article/details/1585185
-   https://blog.csdn.net/happyparrot/article/details/1584079
-   https://www.doc88.com/p-2117087607521.html?r=1