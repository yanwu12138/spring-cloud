package com.yanwu.spring.cloud.common.utils.buc.ap;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.util.DigestUtils;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

@Data
@Accessors(chain = true)
public class ApiBasePO<T> implements Serializable {
    private static final long serialVersionUID = -8002925221601356828L;

    /*** 计费系统ID，由AP服务分配 ***/
    private String code;

    /*** 计费系统唯一标识key，由AP服务分配 ***/
    private String key;

    /*** 计费系统调用接口时当前的系统UTC时间戳 ***/
    private Long timestamp;

    /*** 根据key和当前系统时间计算得出 ***/
    private String token;

    private T param;

    public static <T> ApiBasePO<T> getInstance(String key, T param) {
        long millis = System.currentTimeMillis();
        String md5 = DigestUtils.md5DigestAsHex((key + millis).getBytes(StandardCharsets.UTF_8));
        String token = DigestUtils.md5DigestAsHex(md5.getBytes(StandardCharsets.UTF_8));
        return new ApiBasePO<T>().setKey(key).setToken(token).setTimestamp(millis).setParam(param);
    }

}