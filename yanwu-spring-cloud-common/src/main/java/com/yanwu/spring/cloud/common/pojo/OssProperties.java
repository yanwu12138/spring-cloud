package com.yanwu.spring.cloud.common.pojo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author Baofeng Xu
 * @date 2020/9/24 10:16.
 * <p>
 * description: 阿里云OSS相关对象
 */
@Getter
@ToString
@EqualsAndHashCode
public class OssProperties implements Serializable {
    private static final long serialVersionUID = 1432090277591945572L;

    /*** OSS AccessKey ***/
    private String accessKeyId;
    /*** OSS AccessKeySecret ***/
    private String accessKeySecret;
    /*** OSS Endpoint ***/
    private String endpoint;

    private OssProperties() {
    }

    public static OssProperties getInstance(String keyId, String keySecret, String endpoint) {
        OssProperties properties = new OssProperties();
        properties.endpoint = endpoint;
        properties.accessKeyId = keyId;
        properties.accessKeySecret = keySecret;
        return properties;
    }

}
