package com.yanwu.spring.cloud.common.test;

import com.yanwu.spring.cloud.common.pojo.OssProperties;
import com.yanwu.spring.cloud.common.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author XuBaofeng.
 * @date 2024/7/5 09:46.
 * <p>
 * description:
 */
@Slf4j
public class AliOssTest {

    public static void main(String[] args) throws Exception {
        String accessId = "LTAI5tFudRXZ1t9dNFfbg4Zf", accessKey = "lmqpltx40vcpOsmDnHcz1PzJb7XfvY", endpoint = "oss-cn-hangzhou.aliyuncs.com", bucket = "yanwu-spring-cloud";
        OssProperties properties = OssProperties.newInstance(accessId, accessKey, endpoint);
        AliOssUtil instance = AliOssUtil.newInstance(properties);
        new Thread(() -> {
            try {
                instance.upload(bucket, "", "/Users/xubaofeng/Downloads/网安整改说明.zip");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
        new Thread(() -> {
            try {
                instance.upload(bucket, "", "/Users/xubaofeng/Downloads/网安整改说明.zip");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
