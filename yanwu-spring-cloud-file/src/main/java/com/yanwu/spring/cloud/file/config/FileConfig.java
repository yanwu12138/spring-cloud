package com.yanwu.spring.cloud.file.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Baofeng Xu
 * @date 2021/1/21 14:58.
 * <p>
 * description:
 */
@Data
@Component
public class FileConfig {

    /*** ----------- 二维码相关配置 ----------- ***/
    @Value("${qrcode.check.url}")
    private String checkQrCodeUrl;
    @Value("${qrcode.file.path}")
    private String codeFilePath;
    /*** ----------- 二维码相关配置 ----------- ***/
    @Value("${lucene.index}")
    private String luceneIndex;

}
