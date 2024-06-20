package com.yanwu.spring.cloud.common.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yanwu.spring.cloud.common.pojo.RequestInfo;
import com.yanwu.spring.cloud.common.pojo.Result;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author XuBaofeng.
 * @date 2024/1/4 11:22.
 * <p>
 * description:
 */
public class WechatUtils {

    private static final String APP_ID = "123456789_wx4e2f305590026c5b_123456789";
    private static final String SECRET = "123456789_de3903bde2cafd6236a9c82d53e98e71_123456789";
    private static final String PATH = "pages/startup/startup?shopId=234";

    public static void main(String[] args) throws Exception {
        String accessToken = getAccessToken();
        String qrCodeUrl = "https://api.weixin.qq.com/wxa/getwxacode?access_token=" + accessToken;
        HashMap<String, String> param = new HashMap<>();
        param.put("path", PATH);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<HashMap<String, String>> entity = new HttpEntity<>(param, headers);
        ResponseEntity<byte[]> exchange = new RestTemplate().exchange(qrCodeUrl, HttpMethod.POST, entity, byte[].class);

        String path = "/Users/xubaofeng/Downloads/ishot/" + System.currentTimeMillis() + ".jpg";
        FileUtil.appendWrite(path, exchange.getBody());
    }

    public static String getAccessToken() {
        String tokenUrl = "https://api.weixin.qq.com/cgi-bin/token";
        RequestInfo<Object, TokenInfo> instance = RequestInfo.getInstance(tokenUrl, TokenInfo.class);
        instance.buildParams("grant_type", "client_credential")
                .buildParams("appid", APP_ID)
                .buildParams("secret", SECRET);
        Result<TokenInfo> execute = RestUtil.execute(instance);
        return execute.getData().getAccessToken();
    }

    @Data
    @Accessors(chain = true)
    public static class TokenInfo implements Serializable {
        private static final long serialVersionUID = 7110678240464499367L;
        @JsonProperty("access_token")
        private String accessToken;
        @JsonProperty("expires_in")
        private Long expiresIn;
    }


}
