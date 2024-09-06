package com.yanwu.spring.cloud.common.utils;

import com.yanwu.spring.cloud.common.core.common.HttpConstants;
import com.yanwu.spring.cloud.common.pojo.RequestInfo;
import com.yanwu.spring.cloud.common.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author XuBaofeng.
 * @date 2023/7/3 17:58.
 * <p>
 * description: 通过RestTemplate发送rest请求，包含请求头的处理
 */
@Slf4j
@SuppressWarnings("unused")
public class RequestUtil {

    private static volatile RestTemplate template = null;
    private static final String INIT_TEMPLATE_LOCK = "INIT_TEMPLATE_LOCK";

    /**
     * 执行rest请求
     *
     * @param request 请求的各类参数
     */
    public static <P, R> Result<R> execute(RequestInfo<P, R> request) {
        if (request == null) {
            log.error("execute rest request failed, because request info is empty.");
            return Result.failed();
        }
        String requestId = ThreadUtil.sequenceNo();
        log.info("execute rest begin, txId: {}, request: {},", requestId, request);
        try {
            String url = disposeRestUrl(request);
            HttpEntity<Object> httpEntity = disposeEntity(request);
            ResponseEntity<R> response = initTemplate().exchange(url, request.getMethod(), httpEntity, request.getClazz());
            log.info("execute rest done, txId: {}, response: {}", requestId, response.getBody());
            return disposeResult(response, request.getClazz());
        } catch (Exception e) {
            log.error("execute rest request failed, txId: {}", requestId, e);
            return Result.failed();
        }
    }

    /*** 懒加载的方式初始化template ***/
    private static synchronized RestTemplate initTemplate() {
        if (template != null) {
            return template;
        }
        template = ContextUtil.getBean(RestTemplate.class);
        if (template != null) {
            return template;
        }
        template = createRestTemplate();
        return template;
    }

    private static RestTemplate createRestTemplate() {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectTimeout(30 * 1_000);
        httpRequestFactory.setReadTimeout(300 * 1_000);
        httpRequestFactory.setHttpClient(HttpUtil.HTTP_CLIENT);
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
        restTemplate.getMessageConverters().removeIf(converter -> converter instanceof StringHttpMessageConverter);
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate;
    }

    /***
     * 组装URL
     */
    private static <P, R> String disposeRestUrl(RequestInfo<P, R> restInfo) {
        String url = restInfo.getUrl();
        // ----- 参数: PathVariable
        if (ArrayUtils.isNotEmpty(restInfo.getVariable())) {
            String variable = String.join("/", restInfo.getVariable());
            url = String.join("/", url, variable);
        }
        // ----- 参数: RequestParam
        if (MapUtils.isNotEmpty(restInfo.getParam())) {
            List<String> urlParams = new ArrayList<>();
            restInfo.getParam().forEach((key, value) -> urlParams.add(key + HttpConstants.KEY_VALUE_SP + value));
            url += HttpConstants.QUERY_SP + StringUtils.join(urlParams, HttpConstants.PARAMS_SP);
        }
        return url;
    }

    /***
     * 组装请求头
     */
    private static <P, R> HttpEntity<Object> disposeEntity(RequestInfo<P, R> restInfo) {
        // ----- 参数: RequestHeader
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (MapUtils.isNotEmpty(restInfo.getHeader())) {
            restInfo.getHeader().forEach(headers::add);
        }
        // ----- 参数: RequestBody
        if (restInfo.getBody() == null) {
            return new HttpEntity<>(headers);
        } else {
            return new HttpEntity<>(restInfo.getBody(), headers);
        }
    }

    /***
     * 组装响应
     */
    private static <R> Result<R> disposeResult(ResponseEntity<R> response, Class<R> clazz) {
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
