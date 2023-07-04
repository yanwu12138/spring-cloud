package com.yanwu.spring.cloud.common.utils;

import com.yanwu.spring.cloud.common.core.common.HttpConstants;
import com.yanwu.spring.cloud.common.pojo.PageParam;
import com.yanwu.spring.cloud.common.pojo.RequestInfo;
import com.yanwu.spring.cloud.common.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author XuBaofeng.
 * @date 2023/7/3 17:58.
 * <p>
 * description: 通过RestTemplate发送rest请求，包含请求头的处理
 */
@Slf4j
@SuppressWarnings("unused")
public class RestUtil {

    private static RestTemplate template = null;
    private static final Object LOCK = new Object();

    public static void main(String[] args) {
        HashMap<String, String> params = new HashMap<>();
        params.put("cameraMac", "18:68:cb:15:8b:a8");
        String url = "https://test1monitor.boxingtong.net:9014/sea/fishery/shipList";
        RequestInfo<Object> instance = RequestInfo.getInstance(url, Object.class)
                .buildHeaders("appId", "421433d4133348c52265")
                .buildHeaders("secret", "7c74136544d75684c788588836868d7c")
                .buildVariable("123", "234", "345")
                .buildParams(params)
                .buildParams("aaa", "bbb")
                .buildBody(new PageParam<>().setPage(1).setSize(10).setData("test"));
        log.info("get ship list, request: {}", instance);
        Result<Object> result = execute(instance);
        log.info("get ship list, response: {}", result);
    }

    /**
     * 执行rest请求
     *
     * @param request 请求的各类参数
     */
    public synchronized static <T> Result<T> execute(RequestInfo<T> request) {
        if (request == null) {
            log.error("execute rest request failed, because request info is empty.");
            return Result.failed();
        }
        if (getTemplate() == null) {
            log.error("execute rest request failed, because rest template is empty.");
            return Result.failed();
        }
        String requestId = ThreadUtil.sequenceNo();
        log.info("execute rest begin, txId: {}, request: {},", requestId, request);
        try {
            String url = disposeRestUrl(request);
            HttpEntity<Object> httpEntity = disposeEntity(request);
            ResponseEntity<T> response = template.exchange(url, request.getMethod(), httpEntity, request.getClazz());
            log.info("execute rest done, txId: {}, response: {}", requestId, response.getBody());
            return disposeResult(response, request.getClazz());
        } catch (Exception e) {
            log.error("execute rest request failed, txId: {}", requestId, e);
            return Result.failed();
        }
    }

    /*** 懒加载的方式初始化template ***/
    private static RestTemplate getTemplate() {
        synchronized (LOCK) {
            if (template == null) {
                synchronized (LOCK) {
                    template = ContextUtil.getBean(RestTemplate.class);
                    if (template == null) {
                        template = createRestTemplate();
                    }
                }
            }
            return template;
        }
    }

    private static RestTemplate createRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setBufferRequestBody(false);
        factory.setConnectTimeout(30 * 1_000);
        factory.setReadTimeout(300 * 1_000);
        RestTemplate template = new RestTemplate(factory);
        template.getMessageConverters().removeIf(converter -> converter instanceof StringHttpMessageConverter);
        template.getMessageConverters().add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return template;
    }

    /***
     * 组装URL
     */
    private static <T> String disposeRestUrl(RequestInfo<T> restInfo) {
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
    private static <T> HttpEntity<Object> disposeEntity(RequestInfo<T> restInfo) {
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
