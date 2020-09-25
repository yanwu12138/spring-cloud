package com.yanwu.spring.cloud.common.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author Baofeng Xu
 * @date 2020/9/25 16:38.
 * <p>
 * description:
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class OssResult<T> implements Serializable {
    private static final long serialVersionUID = -2127605773528437627L;

    private Boolean status;

    private T data;

    private String message;

    public static <T> OssResult<T> success() {
        return success(null);
    }

    public static <T> OssResult<T> success(T data) {
        return getInstance(Boolean.TRUE, data, null);
    }

    public static <T> OssResult<T> failed() {
        return failed(null);
    }

    public static <T> OssResult<T> failed(String message) {
        return getInstance(Boolean.FALSE, null, message);
    }

    public static <T> OssResult<T> failed(T data) {
        return getInstance(Boolean.FALSE, data, null);
    }

    public static <T> OssResult<T> failed(T data, String message) {
        return getInstance(Boolean.FALSE, data, message);
    }

    private static <T> OssResult<T> getInstance(Boolean status, T data, String message) {
        OssResult<T> result = new OssResult<>();
        return result.setStatus(status).setData(data).setMessage(message);
    }
}
