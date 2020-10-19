package com.yanwu.spring.cloud.common.core.enums;

import lombok.Getter;

/**
 * @author Baofeng Xu
 * @date 2020/9/25 15:37.
 * <p>
 * description: 上传到文件到OSS时的类型, 按照不同的文件类型区分文件夹
 * * 如: DEFAULT, 则表示在 bucket 中以 /default/ 为根目录
 */
public enum OssFileTypeEnum {

    // ----- 默认目录
    DEFAULT("default"),
    ;

    @Getter
    private final String type;

    OssFileTypeEnum(String type) {
        this.type = type;
    }

    public OssFileTypeEnum fileTypeEnum(String type) {
        for (OssFileTypeEnum value : OssFileTypeEnum.values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return DEFAULT;
    }
}
