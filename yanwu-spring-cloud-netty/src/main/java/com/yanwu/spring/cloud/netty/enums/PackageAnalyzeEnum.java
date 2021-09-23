package com.yanwu.spring.cloud.netty.enums;

import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author Baofeng Xu
 * @date 2021/9/8 9:59.
 * <p>
 * description: 定义各种报文的类型，使用 PackageUtil 对报文进行粘包和分包的处理
 * @see com.yanwu.spring.cloud.netty.util.PackageUtil
 */
@Getter
public enum PackageAnalyzeEnum {
    /*** 报文类型：1 - 只定义了 帧头 ***/
    TYPE_1(new byte[]{(byte) 0xAA, (byte) 0xFF}, null, null, null),
    /*** 报文类型：2 - 定义了 帧头 && 长度 ***/
    TYPE_2(new byte[]{(byte) 0xBB, (byte) 0xFF}, 2, 2, null),
    /*** 报文类型：3 - 定义了 战投 && 帧尾 ***/
    TYPE_3(new byte[]{(byte) 0xCC, (byte) 0xFF}, null, null, new byte[]{(byte) 0xDD, (byte) 0xFF}),
    /*** 报文类型：4 - 定义了 帧头 && 长度 && 帧尾 ***/
    TYPE_4(new byte[]{(byte) 0xEF, (byte) 0xFF}, 2, 2, new byte[]{(byte) 0xFF, (byte) 0xFF}),

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
        checkInstance(head, lengthIdx, lengthLen);
        this.head = head;
        this.tail = tail;
        this.lengthLen = lengthLen;
        this.lengthIdx = lengthIdx;
    }

    @SuppressWarnings("all")
    private static void checkInstance(byte[] head, Integer lengthIdx, Integer lengthLen) {
        if (ArrayUtils.isEmpty(head)) {
            throw new UnsupportedOperationException("PackageAnalyzeEnum Initialization exception, Because head is empty.");
        }
        if (lengthIdx != null && (lengthLen == null || lengthLen == 0)) {
            throw new UnsupportedOperationException("PackageAnalyzeEnum Initialization exception, Because lengthIdx & lengthLen must exist at the same time. lengthIdx: " + lengthIdx + ", lengthLen: " + lengthLen);
        }
        if (lengthLen != null && (lengthIdx == null || lengthIdx == 0)) {
            throw new UnsupportedOperationException("PackageAnalyzeEnum Initialization exception, Because lengthIdx & lengthLen must exist at the same time. lengthIdx: " + lengthIdx + ", lengthLen: " + lengthLen);
        }
    }

    /**
     * 是否包含帧头
     *
     * @return 【true: 包含; false: 不包含】
     */
    public boolean isHead() {
        return ArrayUtils.isNotEmpty(this.head);
    }

    /**
     * 是否包含长度信息
     *
     * @return 【true: 包含; false: 不包含】
     */
    public boolean isLength() {
        return this.lengthIdx != null && this.lengthIdx > 0 && this.lengthLen != null && this.lengthLen > 0;
    }

    /**
     * 是否包含帧尾
     *
     * @return 【true: 包含; false: 不包含】
     */
    public boolean isTail() {
        return ArrayUtils.isNotEmpty(this.tail);
    }

}
