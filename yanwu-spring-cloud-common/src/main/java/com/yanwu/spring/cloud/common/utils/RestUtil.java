package com.yanwu.spring.cloud.common.utils;

import com.yanwu.spring.cloud.common.core.common.HttpConstants;
import com.yanwu.spring.cloud.common.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author XuBaofeng.
 * @date 2023/7/3 17:58.
 * <p>
 * description:
 */
@Slf4j
@SuppressWarnings("unused")
public class RestUtil {

    private static RestTemplate template = null;
    private static final Object LOCK = new Object();

    private static RestTemplate getTemplate() {
        synchronized (LOCK) {
            if (template == null) {
                synchronized (LOCK) {
                    template = ContextUtil.getBean(RestTemplate.class);
                    if (template == null) {
                        template = initRestTemplate();
                    }
                }
            }
            return template;
        }
    }

    public static void main(String[] args) {
        String url = "https://monitor.boxingtong.net:9014/sea/fishery/shipList";
        Result<Object> result = executeRest(HttpMethod.GET, url, Object.class);
        log.info("get ship list, result: {}", result);

        System.out.println();
        System.out.println();
        System.out.println();
        url = "https://monitor.boxingtong.net:9014/sea/fishery/gb/hls";
        HashMap<String, String> params = new HashMap<>();
        params.put("cameraMac", "18:68:cb:15:8b:a8");
        result = executeRest(HttpMethod.GET, url, params, Object.class);
        log.info("get hls url, result: {}", result);

        System.out.println();
        System.out.println();
        System.out.println();
        url = "https://monitor.boxingtong.net:9014/sea/fishery/gb/recordList";
        params.put("day", "2023-07-03");
        HashMap<String, String> headers = new HashMap<>();
        headers.put("appId", "421433d4133348c52265");
        headers.put("secret", "7c74136544d75684c788588836868d7c");
        result = executeRest(HttpMethod.GET, url, headers, params, Object.class);
        log.info("get record list, result: {}", result);
    }

    private static RestTemplate initRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setBufferRequestBody(false);
        factory.setConnectTimeout(30 * 1_000);
        factory.setReadTimeout(300 * 1_000);
        RestTemplate template = new RestTemplate(factory);
        StringHttpMessageConverter messageConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        List<HttpMessageConverter<?>> messageConverters = template.getMessageConverters();
        messageConverters.removeIf(converter -> converter instanceof StringHttpMessageConverter);
        messageConverters.add(messageConverter);
        return template;
    }

    /**
     * 执行rest请求
     *
     * @param method 方法类型
     * @param url    地址
     * @param clazz  响应类型
     */
    public static <T> Result<T> executeRest(HttpMethod method, String url, Class<T> clazz) {
        return executeRest(method, url, null, clazz);
    }

    /**
     * 执行rest请求
     *
     * @param method   方法类型
     * @param url      地址
     * @param paramMap 参数
     * @param clazz    响应类型
     */
    public static <T> Result<T> executeRest(HttpMethod method, String url, Map<String, String> paramMap, Class<T> clazz) {
        return executeRest(method, url, null, paramMap, clazz);
    }

    /**
     * 执行rest请求
     *
     * @param method    方法类型
     * @param url       地址
     * @param headerMap 请求头
     * @param paramMap  参数
     * @param clazz     响应类型
     */
    public synchronized static <T> Result<T> executeRest(HttpMethod method, String url, Map<String, String> headerMap, Map<String, String> paramMap, Class<T> clazz) {
        if (method == null) {
            log.error("execute rest request failed, because method is empty.");
            return Result.failed();
        }
        if (StringUtils.isBlank(url)) {
            log.error("execute rest request failed, because url is empty.");
            return Result.failed();
        }
        if (getTemplate() == null) {
            log.error("execute rest request failed, because template is empty.");
            return Result.failed();
        }
        String txId = UUID.randomUUID().toString();
        try {
            url = disposeRestUrl(url, paramMap);
            HttpHeaders headers = disposeHeader(headerMap);
            log.info("execute rest request start, txId: {}, method: {}, url: {}, params: {}", txId, method, url, paramMap);
            ResponseEntity<T> response = template.exchange(url, method, new HttpEntity<>(headers), clazz);
            log.info("execute rest request success, txId: {}, result: {}", txId, response.getBody());
            return disposeResult(response, clazz);
        } catch (Exception e) {
            log.error("execute rest request failed, txId: {}, method: {}, url: {}, param: {}", txId, method, url, paramMap, e);
            return Result.failed();
        }
    }

    /***
     * 组装URL
     */
    private static String disposeRestUrl(String url, Map<String, String> paramMap) {
        // ----- 参数: RequestParam
        if (MapUtils.isNotEmpty(paramMap)) {
            List<String> urlParams = new ArrayList<>();
            paramMap.forEach((key, value) -> urlParams.add(key + HttpConstants.KEY_VALUE_SP + value));
            url += HttpConstants.QUERY_SP + StringUtils.join(urlParams, HttpConstants.PARAMS_SP);
        }
        return url;
    }

    /***
     * 组装请求头
     */
    private static HttpHeaders disposeHeader(Map<String, String> headerMap) {
        // ----- 参数: headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (MapUtils.isNotEmpty(headerMap)) {
            headerMap.forEach(headers::add);
        }
        return headers;
    }

    /***
     * 组装响应
     */
    private static <T> Result<T> disposeResult(ResponseEntity<T> response, Class<T> clazz) {
        if (response == null) {
            return Result.failed();
        }
        if (!HttpStatus.OK.equals(response.getStatusCode())) {
            return Result.failed("执行失败");
        }
        if (Void.class.equals(clazz)) {
            return Result.success();
        }
        return Result.success(response.getBody());
    }

}
