package com.yanwu.spring.cloud.netty.enums;

import lombok.Getter;

/**
 * @author Baofeng Xu
 * @date 2021/9/8 9:59.
 * <p>
 * description: 定义各种报文的类型，使用 PackageUtil 对报文进行粘包和分包的处理
 * @see com.yanwu.spring.cloud.netty.util.PackageUtil
 */
@Getter
public enum PackageAnalyzeEnum {
    /*** 报文类型：1 - 模拟包含 帧头、帧尾、长度信息 的报文类型 ***/
    TYPE_1(new byte[]{}, null, null, null),
    /*** 报文类型：2 - 模拟包含 帧头、长度信息 的报文类型 ***/
    TYPE_2(new byte[]{}, 2, 2, null),
    /*** 报文类型：3 - 模拟包含 帧头、帧尾 的报文类型 ***/
    TYPE_3(new byte[]{}, null, null, new byte[]{}),
    /*** 报文类型：4 - 模拟包含 帧头 的报文类型 ***/
    TYPE_4(new byte[]{}, null, null, null),
    /*** 报文类型：5 - 模拟包含 帧尾 的报文类型 ***/
    TYPE_5(null, null, null, new byte[]{}),


    ;

    /*** 包头 ***/
    private final byte[] head;
    /*** 数据包长度字段起始位置 ***/
    private final Integer lengthIdx;
    /*** 数据包长度字段占字节数 ***/
    private final Integer lengthLen;
    /*** 包尾 ***/
    private final byte[] tail;

    PackageAnalyzeEnum(byte[] head, Integer lengthIdx, Integer lengthLen, byte[] tail) {
        checkLength(lengthIdx, lengthLen);
        this.head = head;
        this.tail = tail;
        this.lengthLen = lengthLen;
        this.lengthIdx = lengthIdx;
    }

    @SuppressWarnings("all")
    private static void checkLength(Integer lengthIdx, Integer lengthLen) {
        if (lengthIdx != null && (lengthLen == null || lengthLen == 0)) {
            throw new UnsupportedOperationException("PackageAnalyzeEnum Initialization exception, Because lengthIdx & lengthLen must exist at the same time. lengthIdx: " + lengthIdx + ", lengthLen: " + lengthLen);
        }
        if (lengthLen != null && (lengthIdx == null || lengthIdx == 0)) {
            throw new UnsupportedOperationException("PackageAnalyzeEnum Initialization exception, Because lengthIdx & lengthLen must exist at the same time. lengthIdx: " + lengthIdx + ", lengthLen: " + lengthLen);
        }
    }

}
