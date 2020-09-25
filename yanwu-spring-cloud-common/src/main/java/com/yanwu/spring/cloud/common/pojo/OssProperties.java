package com.yanwu.spring.cloud.common.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author Baofeng Xu
 * @date 2020/9/24 10:16.
 * <p>
 * description: 阿里云OSS相关对象
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class OssProperties implements Serializable {
    private static final long serialVersionUID = 1432090277591945572L;

    /*** OSS AccessKey ***/
    private String accessKeyId;
    /*** OSS AccessKeySecret ***/
    private String accessKeySecret;
    /*** OSS Endpoint ***/
    private String endpoint;
    /*** OSS bucket ***/
    private String bucket;

    public static OssProperties getInstance(String accessKeyId, String accessKeySecret, String endpoint, String bucket) {
        return new OssProperties().setAccessKeyId(accessKeyId).setEndpoint(endpoint)
                .setAccessKeySecret(accessKeySecret).setBucket(bucket);
    }
}
