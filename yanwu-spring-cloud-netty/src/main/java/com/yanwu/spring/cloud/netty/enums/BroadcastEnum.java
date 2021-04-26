package com.yanwu.spring.cloud.netty.enums;

import lombok.Getter;

/**
 * @author Baofeng Xu
 * @date 2021/4/25 16:29.
 * <p>
 * description:
 */
public enum BroadcastEnum {
    /*** 升级文件组播 ***/
    UPGRADE("upgrade", "/tmp/file/upgrade/"),
    /*** 资源文件组播 ***/
    RESOURCE("resource", "/tmp/file/resource/"),
    ;

    /*** 组播的类型 ***/
    @Getter
    private final String type;
    /*** 文件的目录 ***/
    @Getter
    private final String path;

    BroadcastEnum(String type, String path) {
        this.type = type;
        this.path = path;
    }

}
