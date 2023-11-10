package com.yanwu.spring.cloud.common.utils;

import com.yanwu.spring.cloud.common.pojo.RequestInfo;
import lombok.Data;
import lombok.experimental.Accessors;
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
@SuppressWarnings("all")
public class BxtApiTestUtil {


    public static void main(String[] args) {
        String appId = "73039084026629282443";
        String accessToken = "Y6gD6ziZQz63MoFlnapRO6RyfezALSRht1cU1ffCMypoUCB2BpBs2IwyVa32u8dROeVhEEv8RO93AH1XCwk6NXxubyx2VxLYL8hGYsnUtXmCF2CqfI2bNbv1ZXVaUv3I";
//        testLogin(appId, System.currentTimeMillis());
//        testGetBalance(appId, System.currentTimeMillis(), accessToken);
        testAntennaStatus();
    }

    private static void testAntennaStatus() {
        TAntennaStatus status = new TAntennaStatus();
        status.setGpsSpeedKnots(10.0D).setGpsHeadingDegs(12.312D);
        String url = "http://192.168.18.254:8001/api/amu/antennaStatus";
        RequestInfo<Object> instance = RequestInfo.getInstance(HttpMethod.POST, url, Object.class);
        instance.buildHeaders("Content-Type", "application/json");

        // ----- 正常情况，信号量大与11
        for (int i = 0; i < 120; i++) {
            instance.buildBody(status.setSatLongitudeDegs(134.1D).setGpsLongitudeDegs(122.001D).setGpsLatitudeDegs(31.002D).setDemodConDbAvg(15.3235D));
            log.info("request: {}, response: {}", instance, RestUtil.execute(instance));
            ThreadUtil.sleep(5_000L);
        }

        // ----- 信号量6.5～11
        for (int i = 0; i < 750; i++) {
            instance.buildBody(status.setSatLongitudeDegs(134.1D).setGpsLongitudeDegs(122.001D).setGpsLatitudeDegs(31.002D).setDemodConDbAvg(9.3235D));
            log.info("request: {}, response: {}", instance, RestUtil.execute(instance));
            ThreadUtil.sleep(5_000L);
        }

        // ----- 信号量<6.5
        for (int i = 0; i < 360; i++) {
            instance.buildBody(status.setSatLongitudeDegs(134.1D).setGpsLongitudeDegs(120.001D).setGpsLatitudeDegs(30.002D).setDemodConDbAvg(4.3235D));
            log.info("request: {}, response: {}", instance, RestUtil.execute(instance));
            ThreadUtil.sleep(5_000L);
        }
    }

    @Data
    @Accessors(chain = true)
    public static class TAntennaStatus {
        private Double gpsLongitudeDegs;
        private Double gpsLatitudeDegs;
        private Double gpsSpeedKnots;
        private Double gpsHeadingDegs;
        private Double satLongitudeDegs;
        private Double demodConDbAvg;
    }

    private static void testLogin(String appId, long timestamp) {
        String url = "http://bxtapi.boxingtong.net:6241/bxt-api/user/login";
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
        String url = "http://bxtapi.boxingtong.net:6241/bxt-api/user/getBalance";
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
        String url = "http://bxtapi.boxingtong.net:6241/bxt-api/user/bobyPay";
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
        String secret = "B05DF532E466FE1C783213AFA8F6DAB9";
        String md5Str = DigestUtils.md5Hex((appId + secret).getBytes(StandardCharsets.UTF_8));
        md5Str = DigestUtils.md5Hex((md5Str + timestamp).getBytes(StandardCharsets.UTF_8));
        return md5Str;
    }

}
