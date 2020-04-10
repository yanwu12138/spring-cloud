package com.yanwu.spring.cloud.netty.util;

import com.yanwu.spring.cloud.common.utils.ByteUtil;
import com.yanwu.spring.cloud.common.utils.Crc16Util;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-11-19 11:53.
 * <p>
 * description:
 */
@Slf4j
public class AlarmLampUtil {
    private AlarmLampUtil() {
    }

    public static final String ALARM_LAMP_REDIS = "alarm_lamp";
    private static final byte[] HEADS = {0x48, 0x4C};
    private static final byte[] RESERVED = {0x00, 0x00, 0x00, 0x00};
    private static final byte[] ENDS = {0x4C, 0x48};
    private static final Map<String, Integer> SN_LIST = new HashMap<>();

    static {
        SN_LIST.put("hoolink_indicator_light_000001", 0);
        SN_LIST.put("hoolink_indicator_light_000002", 0);
    }

    /**
     * 解析报警灯报文
     *
     * @param bs
     * @return
     */
    public static byte[][] parsingMessage(byte[] bs) {
        byte[] head = parsingMessage(bs, 9, 0);
        byte[] data = parsingMessage(bs, bs.length - 13, 9);
        byte[] end = parsingMessage(bs, 4, bs.length - 4);
        return new byte[][]{head, data, end};
    }

    private static byte[] parsingMessage(byte[] source, int len, int point) {
        byte[] bytes = new byte[len];
        int index = 0;
        while (index < len) {
            bytes[index] = source[index + point];
            index++;
        }
        return bytes;
    }

    /**
     * 校验是否是报警灯报文
     *
     * @param head
     * @param data
     * @param end
     * @throws Exception
     */
    public static void checkMsg(byte[] head, String data, byte[] end) throws Exception {
        String len = Crc16Util.convertHighLow(Crc16Util.crc16ToHexStr(data.length()));
        String crc = Crc16Util.convertHighLow(Crc16Util.getCrc16HexStrByJson(data));
        String lstStr = ByteUtil.byteArr2HexStr(new byte[]{head[head.length - 2], head[head.length - 1]}).toUpperCase();
        String crxStr = ByteUtil.byteArr2HexStr(new byte[]{end[0], end[1]}).toUpperCase();
        if (!lstStr.equals(len) || !crxStr.equals(crc)) {
            throw new RuntimeException("报警灯报文错误!");
        }
    }

    /**
     * 判断告警灯身份是否合法
     *
     * @param sn
     * @param seq
     * @return
     */
    public static synchronized boolean checkDevice(String sn, Integer seq) {
        boolean flag = SN_LIST.containsKey(sn);
        if (flag) {
            SN_LIST.put(sn, seq);
        }
        return flag;
    }

    /**
     * 获取所有的报警灯sn
     *
     * @return
     */
    public static Set<String> getDevice() {
        return SN_LIST.keySet();
    }

    public static synchronized Integer getAlarmLampSeq(String sn) {
        Integer seq = SN_LIST.get(sn);
        if (seq != null) {
            seq = seq + 1 > 0xFFFF ? 0 : seq + 1;
            SN_LIST.put(sn, seq);
        }
        return seq;
    }

    /**
     * 组装报文
     *
     * @param param
     * @param upOrDown
     * @return
     * @throws Exception
     */
    public static byte[] getMessage(String param, boolean upOrDown) throws Exception {
        // ----- 请求\响应与是否分包标识
        byte[] hasMore = getHasMore(upOrDown, param.length() > 0xFFFF);
        // ----- 数据域长度
        byte[] len = Crc16Util.hexStrToByteArr(Crc16Util.convertHighLow(Crc16Util.crc16ToHexStr(param.length())));
        // ----- 数据域
        byte[] data = param.getBytes();
        // ----- crc
        byte[] crc = ByteUtil.hexStr2ByteArr(Crc16Util.convertHighLow(Crc16Util.getCrc16HexStrByJson(param)));
        return getMessage(new byte[][]{HEADS, hasMore, RESERVED, len, data, crc, ENDS}, param.length() + 13);
    }

    /**
     * 组装报文
     *
     * @param bytes
     * @param len
     * @return
     * @throws Exception
     */
    public static byte[] getMessage(byte[][] bytes, int len) {
        int index = 0;
        byte[] result = new byte[len];
        // ----- 组装数据
        for (byte[] bs : bytes) {
            for (byte b : bs) {
                result[index] = b;
                index++;
            }
        }
        return result;
    }

    /**
     * 组装hasMore字段
     *
     * @param upOrDown
     * @param upOrDown
     * @return
     * @throws Exception
     */
    private static byte[] getHasMore(boolean upOrDown, boolean hasMore) {
        byte[] bytes = new byte[1];
        if (upOrDown) {
            bytes[0] = hasMore ? (byte) 0x1 : (byte) 0x0;
        } else {
            bytes[0] = hasMore ? (byte) 0x3 : (byte) 0x2;
        }
        return bytes;
    }

}
