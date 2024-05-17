package com.yanwu.spring.cloud.postapi.bo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author XuBaofeng.
 * @date 2023/7/4 13:57.
 * <p>
 * description:
 */
@Getter
@EqualsAndHashCode
@ToString(exclude = {"clazz"})
@SuppressWarnings("unused")
public class RequestInfo<T> implements Serializable {
    private static final long serialVersionUID = 1531954575077039159L;

    /*** requestType ***/
    private HttpMethod method;

    /*** requestURL ***/
    private String url;

    /*** requestHeader ***/
    private Map<String, String> header;

    /*** pathVariable ***/
    private String[] variable;

    /*** requestParam ***/
    private Map<String, String> param;

    /*** requestBody ***/
    private Object body;

    /*** responseType ***/
    private Class<T> clazz;

    private RequestInfo() {
    }

    public static <T> RequestInfo<T> getInstance(String url, Class<T> clazz) {
        return getInstance(HttpMethod.GET, url, clazz);
    }

    public static <T> RequestInfo<T> getInstance(HttpMethod method, String url, Class<T> clazz) {
        if (method == null || StringUtils.isBlank(url)) {
            return null;
        }
        RequestInfo<T> instance = new RequestInfo<>();
        return instance.setMethod(method).setUrl(url).setClazz(clazz);
    }

    public RequestInfo<T> buildHeaders(String key, String value) {
        if (MapUtils.isEmpty(this.getHeader())) {
            this.header = new HashMap<>();
        }
        this.getHeader().put(key, value);
        return this;
    }

    public RequestInfo<T> buildVariable(String... variable) {
        if (ArrayUtils.isEmpty(variable)) {
            return this;
        }
        String[] sourceVar = getVariable();
        if (ArrayUtils.isEmpty(sourceVar)) {
            this.variable = variable;
            return this;
        }
        String[] temp = new String[sourceVar.length + variable.length];
        System.arraycopy(sourceVar, 0, temp, 0, sourceVar.length);
        System.arraycopy(variable, 0, temp, sourceVar.length, variable.length);
        this.variable = temp;
        return this;
    }

    public RequestInfo<T> buildParams(String key, String value) {
        return buildParams(Collections.singletonMap(key, value));
    }

    public RequestInfo<T> buildParams(Map<String, String> param) {
        if (MapUtils.isEmpty(this.getParam())) {
            this.param = new HashMap<>();
        }
        this.getParam().putAll(param);
        return this;
    }

    public RequestInfo<T> buildBody(Object body) {
        this.body = body;
        return this;
    }

    private RequestInfo<T> setMethod(HttpMethod method) {
        this.method = method;
        return this;
    }

    private RequestInfo<T> setUrl(String url) {
        this.url = url;
        return this;
    }

    private RequestInfo<T> setClazz(Class<T> clazz) {
        this.clazz = clazz;
        return this;
    }

}
