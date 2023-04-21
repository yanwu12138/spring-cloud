package com.yanwu.spring.cloud.common.utils;

import com.yanwu.spring.cloud.common.pojo.HttpDelete;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

import static com.yanwu.spring.cloud.common.core.common.HttpConstants.*;

/**
 * @author Baofeng Xu
 * @date 2020/11/24 9:38.
 * <p>
 * description: Http请求工具
 */
@Slf4j
@SuppressWarnings("unused")
public class HttpUtil {
    /*** 客户端 */
    public static final CloseableHttpClient HTTP_CLIENT;

    static {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(20);
        HTTP_CLIENT = HttpClients.custom().setConnectionManager(cm).build();
    }

    private HttpUtil() {
        throw new UnsupportedOperationException("HttpUtil should never be instantiated");
    }

    /**
     * 发送GET无参请求
     *
     * @param url   请求地址
     * @param clazz 返回值类型
     * @param <T>   返回值泛型
     * @return 响应结果
     */
    public static <T> T get(String url, Class<T> clazz) throws IOException {
        return get(url, null, clazz);
    }

    /**
     * 发送GET带参请求
     *
     * @param url    请求地址
     * @param params 参数
     * @param clazz  返回值类型
     * @param <T>    返回值泛型
     * @return 响应结果
     */
    public static <T> T get(String url, Map<String, String> params, Class<T> clazz) throws IOException {
        if (MapUtils.isNotEmpty(params)) {
            List<String> urlParams = new ArrayList<>();
            params.forEach((key, value) -> urlParams.add(key + KEY_VALUE_SP + value));
            url += QUERY_SP + StringUtils.join(urlParams, PARAMS_SP);
        }
        return execute(new HttpGet(url), clazz);
    }

    /**
     * 发送POST无参请求
     *
     * @param url   请求地址
     * @param clazz 返回值类型
     * @param <T>   返回值泛型
     * @return 响应结果
     */
    public static <T> T post(String url, Class<T> clazz) throws IOException {
        return post(url, null, clazz);
    }

    /**
     * 发送POST带参请求
     *
     * @param url    请求地址
     * @param params 参数
     * @param clazz  返回值类型
     * @return 响应结果
     */
    public static <P, T> T post(String url, P params, Class<T> clazz) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        assemblyHeader(httpPost);
        assemblyParam(httpPost, params);
        return execute(httpPost, clazz);
    }

    /**
     * 发送DELETE无参请求
     *
     * @param url   请求地址
     * @param clazz 返回值类型
     * @param <T>   返回值泛型
     * @return 响应结果
     */
    public static <T> T delete(String url, Class<T> clazz) throws IOException {
        return delete(url, null, clazz);
    }

    /**
     * 发送DELETE带参请求
     *
     * @param url    请求地址
     * @param params 参数
     * @param clazz  返回值类型
     * @return 响应结果
     */
    public static <P, T> T delete(String url, P params, Class<T> clazz) throws IOException {
        HttpDelete httpDelete = new HttpDelete(url);
        assemblyHeader(httpDelete);
        assemblyParam(httpDelete, params);
        return execute(httpDelete, clazz);
    }

    /**
     * 执行请求并返回结果
     *
     * @param request HttpRequestBase.class
     * @param clazz   返回值类型
     * @param <T>     返回值泛型
     * @return 响应结果
     * @throws IOException IOException.class
     */
    private static <T> T execute(HttpRequestBase request, Class<T> clazz) throws IOException {
        try (CloseableHttpClient client = getHttpClient()) {
            HttpEntity entity = client.execute(request).getEntity();
            return JsonUtil.toObject(EntityUtils.toString(entity, UTF8), clazz);
        }
    }

    /**
     * 获取Http连接
     *
     * @return CloseableHttpClient.class
     */
    private static CloseableHttpClient getHttpClient() {
        return HttpClientBuilder.create().setDefaultRequestConfig(RequestConfig
                .custom()
                .setSocketTimeout(5000)
                .setConnectTimeout(1000)
                .build()).build();
    }

    /**
     * 封装请求头
     *
     * @param requestBase request
     */
    private static void assemblyHeader(HttpRequestBase requestBase) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.nonNull(requestAttributes)) {
            HttpServletRequest servletRequest = requestAttributes.getRequest();
            Enumeration<String> names = servletRequest.getHeaderNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                requestBase.setHeader(name, servletRequest.getHeader(name));
            }
        }
        requestBase.setHeader(ACCEPT, APPLICATION_JSON);
        requestBase.setHeader(CONTENT_TYPE, APPLICATION_JSON);
    }

    /**
     * 组装参数
     *
     * @param request request
     * @param param   参数
     */
    private static <P> void assemblyParam(HttpEntityEnclosingRequestBase request, P param) {
        if (Objects.isNull(param)) {
            return;
        }
        String paramStr = JsonUtil.toString(param);
        StringEntity entity = new StringEntity(paramStr, UTF8);
        entity.setContentType(APPLICATION_JSON);
        request.setEntity(entity);
    }

}
