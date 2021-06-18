package com.yanwu.spring.cloud.netty.util;

import com.yanwu.spring.cloud.common.utils.ByteUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Baofeng Xu
 * @date 2021/6/11 16:56.
 * <p>
 * description: 做粘包和半包的处理
 */
public class PackageUtil {

    /***
     * 半包的缓存
     * KEY: netty的通道ID
     * VALUE: 通道ID对应的半包缓存
     */
    public static final Map<String, byte[]> CACHE = new ConcurrentHashMap<>();

    /**
     * 报文中只有帧头和帧尾的标识符，没有报文长度的处理方式
     * 注：如果报文中有长度，则建议使用长度进行拆包，该处理方式可能会导致以下问题
     * * 问题：报文中刚好有段内容为：帧尾 + 帧头
     * ** 如帧头为：0xAA, 0xBB；帧尾为：0xFF, 0xEE
     * ** 当报文为：{0xAA, 0xBB, 0x01, 0x02, 0xFF, 0xEE, 0xAA, 0xBB, 0x33, 0xFF, 0xEE} 时
     * ** 可能会将该报文拆解成：{0xAA, 0xBB, 0x01, 0x02, 0xFF, 0xEE} && {0xAA, 0xBB, 0x33, 0xFF, 0xEE} 这两个报文处理
     *
     * @param data 原始报文
     * @param head 包头
     * @param tail 包尾
     * @return 处理半包之后的结果
     */
    public static byte[] reader(String channelId, byte[] data, byte[] head, byte[] tail) {
        if (data == null || data.length == 0) {
            return new byte[]{};
        }
        // ----- 不以帧头开始，说明要么是个半包，要么丢包了
        if (!isHead(data, head)) {
            byte[] cache = CACHE.get(channelId);
            if (cache == null || cache.length == 0) {
                // ----- 如果之前没有半包的缓存，说明前面丢包了，不处理该报文
                return new byte[]{};
            }
            // ----- 有缓存，和缓存拼起来，尝试解包
            return reader(channelId, ByteUtil.merge(CACHE.get(channelId), data), head, tail);
        }
        if (isTail(data, tail)) {
            // ----- 以帧头开始且以帧尾结束，是个完整的报文，做解包操作，并把之前缓存的半包清掉
            CACHE.remove(channelId);
            return data;
        }
        // ----- 不以帧尾结束，缓存起来等下个报文，
        CACHE.put(channelId, data);
        return new byte[]{};
    }

    /**
     * 校验帧头
     *
     * @param data 原始数据
     * @param head 帧头
     * @return [true: 以帧头开始; false: 不以帧头开始]
     */
    private static boolean isHead(byte[] data, byte[] head) {
        if (data == null || head == null || head.length == 0 || data.length < head.length) {
            return false;
        }
        for (int i = 0; i < head.length; i++) {
            if (head[i] != data[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * 在原始报文中找帧尾
     *
     * @param data 原始数据
     * @param head 帧头
     * @param tail 帧尾
     * @return 帧尾的位置，当返回结果为-1时，说明改报文中没有帧尾
     */
    private static int findTail(byte[] data, byte[] head, byte[] tail) {
        boolean flag = data == null || head == null || head.length == 0 ||
                tail == null || tail.length == 0 || data.length < head.length + tail.length;
        if (flag || !isHead(data, head)) {
            return -1;
        }
        int tailLength = tail.length;
        boolean tailFlag = false;
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < tail.length; j++) {
                if (data[i + j] != tail[j]) {
                    break;
                }
                tailFlag = true;
            }
            if (tailFlag) {
                break;
            }
        }
        return -1;
    }


    public static void main(String[] args) {
        String channelId = "test-reader";
        byte[] head = new byte[]{(byte) 0xAA};
        byte[] tail = new byte[]{(byte) 0xFF};
        // ----- test
        byte[] data1 = {(byte) 0xAA, 0x01, 0x02};
        System.out.println("第一包结果: " + ByteUtil.printHexStrByBytes(reader(channelId, data1, head, tail)));
        byte[] data2 = {0x03, 0x04, 0x05};
        System.out.println("第二包结果: " + ByteUtil.printHexStrByBytes(reader(channelId, data2, head, tail)));
        byte[] data3 = {0x06, 0x07, 0x08};
        System.out.println("第三包结果: " + ByteUtil.printHexStrByBytes(reader(channelId, data3, head, tail)));
        byte[] data4 = {0x09, 0x010, (byte) 0xFF};
        System.out.println("第四包结果: " + ByteUtil.printHexStrByBytes(reader(channelId, data4, head, tail)));
    }
}
