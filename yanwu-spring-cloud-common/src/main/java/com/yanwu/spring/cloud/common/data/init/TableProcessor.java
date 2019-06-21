package com.yanwu.spring.cloud.common.data.init;

import com.yanwu.spring.cloud.common.core.enums.FileType;
import lombok.Data;

@Data
public class TableProcessor {

    private String tableName;

    private String modelClassName;

    private String dataFileName;

    private FileType fileType;

    private InitMode initMode;

}
