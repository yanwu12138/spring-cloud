package com.yanwu.spring.cloud.common.core.enums;

public enum FileType {

    OTHERS, WORD, EXCEL, PPT, PDF, SQL, JSON, TXT;

    public static FileType findByCode(Integer code) {
        for (FileType fileType : FileType.values()) {
            if (fileType.ordinal() == code) {
                return fileType;
            }
        }
        return null;
    }
}
