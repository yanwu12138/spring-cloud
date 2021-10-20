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
    /*** 报文类型：1 - 定义了 帧头 && 长度 ***/
    TYPE_1(new byte[]{(byte) 0xAA, (byte) 0xAA}, 2, 2, null),
    /*** 报文类型：2 - 定义了 帧头 && 帧尾 ***/
    TYPE_2(new byte[]{(byte) 0xBB, (byte) 0xBB}, null, null, new byte[]{(byte) 0xCC, (byte) 0xCC}),
    /*** 报文类型：3 - 定义了 帧头 && 长度 && 帧尾 ***/
    TYPE_3(new byte[]{(byte) 0xDD, (byte) 0xDD}, 2, 2, new byte[]{(byte) 0xEE, (byte) 0xEE}),

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
        this.head = head;
        this.tail = tail;
        this.lengthLen = lengthLen;
        this.lengthIdx = lengthIdx;
        checkInstance(this);
    }

    @SuppressWarnings("all")
    private static void checkInstance(PackageAnalyzeEnum analyze) {
        if (!analyze.isHead()) {
            throw new UnsupportedOperationException("PackageAnalyzeEnum Initialization exception, Because head is empty. this: " + analyze);
        }
        if (!analyze.isTail() && !analyze.isLength()) {
            throw new UnsupportedOperationException("PackageAnalyzeEnum Initialization exception, Because tail & length must exist at the same time. this: " + analyze);
        }
        if (analyze.lengthIdx != null && (analyze.lengthLen == null || analyze.lengthLen == 0)) {
            throw new UnsupportedOperationException("PackageAnalyzeEnum Initialization exception, Because lengthIdx & lengthLen must exist at the same time. this: " + analyze);
        }
        if (analyze.lengthLen != null && (analyze.lengthIdx == null || analyze.lengthIdx == 0)) {
            throw new UnsupportedOperationException("PackageAnalyzeEnum Initialization exception, Because lengthIdx & lengthLen must exist at the same time. this: " + analyze);
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
