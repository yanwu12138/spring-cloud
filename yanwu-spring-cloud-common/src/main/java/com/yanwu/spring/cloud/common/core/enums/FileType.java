package com.yanwu.spring.cloud.common.core.enums;

import com.yanwu.spring.cloud.common.core.common.Contents;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-08-26 14:22.
 * <p/>
 * description:
 * 文件类型
 */
public enum FileType implements AbstractBaseEnum {

    /*** 文件类型 ***/
    OTHERS(""),
    WORD_03(".doc"),
    WORD_07(".docx"),
    EXCEL_03(".xls"),
    EXCEL_07(".xlsx"),
    PPT_03(".ppt"),
    PPT_07(".pptx"),
    PDF(".pdf"),
    SQL(".sql"),
    JSON(".json"),
    TXT(".txt"),
    ZIP(".zip"),
    JPG(".jpg"),
    ;

    @Getter
    private final String suffix;

    FileType(String suffix) {
        this.suffix = suffix;
    }

    public static FileType findByCode(Integer code) {
        for (FileType fileType : FileType.values()) {
            if (fileType.ordinal() == code) {
                return fileType;
            }
        }
        return null;
    }

    /**
     * 根据文件名获取文件类型
     *
     * @param fileName 文件名
     * @return 文件类型
     */
    public static FileType getFileTypeByName(String fileName) {
        Assert.isTrue(StringUtils.isNotBlank(fileName), "file name is empty.");
        if (fileName.contains(Contents.POINT)) {
            String suffix = fileName.substring(fileName.lastIndexOf(Contents.POINT));
            FileType fileType = FileType.getTypeBySuffix(suffix);
            return fileType != null ? fileType : FileType.OTHERS;
        }
        return FileType.OTHERS;
    }

    public static FileType getTypeBySuffix(String suffix) {
        for (FileType fileType : FileType.values()) {
            if (fileType.suffix.equals(suffix)) {
                return fileType;
            }
        }
        return null;
    }

    public static boolean checkUniqueness() {
        Set<String> keySet = new HashSet<>();
        for (FileType value : FileType.values()) {
            if (keySet.contains(value.suffix)) {
                return false;
            }
            keySet.add(value.suffix);
        }
        return true;
    }

}
