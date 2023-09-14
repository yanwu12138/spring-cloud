package com.yanwu.spring.cloud.common.utils;

import com.yanwu.spring.cloud.common.pojo.RequestInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpMethod;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * @author XuBaofeng.
 * @date 2023/9/5 17:02.
 * <p>
 * description:
 */
@Slf4j
public class BxtApiTestUtil {


    public static void main(String[] args) {
        String appId = "30674910590919340749";
        String accessToken = "1KwR2lac1pWEYLrVAnU8Kq4j85sagxLDqRaOuyhRcxwcKEbfQSakw7kAt9b7FTtBAOgBBXvWfwwQ5QMvOya3wClTPhJaSQksNWmQcvTFSjIVVPm9URbuRNnYRUP8SuB9";
//        testLogin(appId, System.currentTimeMillis());
//        testGetBalance(appId, System.currentTimeMillis(), accessToken);
        testBobyPay(appId, System.currentTimeMillis(), accessToken);
    }


    private static void testLogin(String appId, long timestamp) {
        String url = "http://test1bxtapi.boxingtong.net:6241/bxt-api/user/login";
        HashMap<String, String> param = new HashMap<>();
        param.put("phone", "13121909171");
        param.put("password", "123456");
        RequestInfo<Object> instance = RequestInfo.getInstance(HttpMethod.POST, url, Object.class);
        instance.buildHeaders("Content-Type", "application/json")
                .buildHeaders("AppId", appId)
                .buildHeaders("Timestamp", String.valueOf(timestamp))
                .buildHeaders("BXT_Token", getBxtToken(appId, timestamp))
                .buildBody(param);
        log.info("test rest, url: {}, param: {}, result: {}", url, param, RestUtil.execute(instance));
    }

    private static void testGetBalance(String appId, long timestamp, String accessToken) {
        String url = "http://test1bxtapi.boxingtong.net:6241/bxt-api/user/getBalance";
        RequestInfo<Object> instance = RequestInfo.getInstance(url, Object.class);
        instance.buildHeaders("Content-Type", "application/json")
                .buildHeaders("AppId", appId)
                .buildHeaders("Timestamp", String.valueOf(timestamp))
                .buildHeaders("BXT_Token", getBxtToken(appId, timestamp))
                .buildHeaders("AccessToken", accessToken)
                .buildParams("userId", "C10000336");
        log.info("test rest, url: {}, param: {}, result: {}", url, "C10000336", RestUtil.execute(instance));
    }

    private static void testBobyPay(String appId, long timestamp, String accessToken) {
        String url = "http://test1bxtapi.boxingtong.net:6241/bxt-api/user/bobyPay";
        HashMap<String, String> param = new HashMap<>();
        param.put("userId", "C10000336");
        param.put("boby", "6");
        param.put("orderNum", "111111111114");
        param.put("describe", "订单备注信息");
        RequestInfo<Object> instance = RequestInfo.getInstance(HttpMethod.POST, url, Object.class);
        instance.buildHeaders("Content-Type", "application/json")
                .buildHeaders("AppId", appId)
                .buildHeaders("Timestamp", String.valueOf(timestamp))
                .buildHeaders("BXT_Token", getBxtToken(appId, timestamp))
                .buildHeaders("AccessToken", accessToken)
                .buildBody(param);
        log.info("test rest, url: {}, param: {}, result: {}", url, "C10000336", RestUtil.execute(instance));
    }

    private static String getBxtToken(String appId, long timestamp) {
        String secret = "809E42FAFE73C8DC386D427234208AFC";
        String md5Str = DigestUtils.md5Hex((appId + secret).getBytes(StandardCharsets.UTF_8));
        md5Str = DigestUtils.md5Hex((md5Str + timestamp).getBytes(StandardCharsets.UTF_8));
        return md5Str;
    }

}
