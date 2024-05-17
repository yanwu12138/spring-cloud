package com.yanwu.spring.cloud.postapi.congig;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;

/**
 * @author XuBaofeng.
 * @date 2024/5/17 14:19.
 * <p>
 * description:
 */
@Getter
public enum MethodType {

    GET("GET", HttpMethod.GET),
    PUT("PUT", HttpMethod.PUT),
    POST("POST", HttpMethod.POST),
    DELETE("DELETE", HttpMethod.DELETE),

    ;
    private final String methodName;
    private final HttpMethod method;

    MethodType(String methodName, HttpMethod method) {
        this.method = method;
        this.methodName = methodName;
    }

    public static MethodType of(String methodName) {
        if (StringUtils.isBlank(methodName)) {
            return GET;
        }
        for (MethodType methodType : MethodType.values()) {
            if (methodType.methodName.equals(methodName)) {
                return methodType;
            }
        }
        return GET;
    }

}
