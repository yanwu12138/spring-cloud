package com.yanwu.spring.cloud.common.core.utils;

import com.yanwu.spring.cloud.common.core.common.AccessToken;
import com.yanwu.spring.cloud.common.core.exception.AuthorizationException;
import com.yanwu.spring.cloud.common.data.base.HiveContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

@Slf4j
public class AccessTokenUtil {

    /**
     * 登陆成功时, 将用户信息保存到
     *
     * @param id
     * @param name
     * @return
     * @throws Exception
     */
    public static String loginSuccess(Long id, String name) throws Exception {
        AccessToken accessToken = new AccessToken();
        accessToken.setUserId(id);
        accessToken.setExpire(String.valueOf(System.currentTimeMillis() + 60 * 60 * 1000L));
        String token = AccessTokenUtil.generateToken(accessToken);
        // ===== 创建token,放入缓存 ===== //
        HiveContextHolder.getContext().setUserId(id);
        HiveContextHolder.getContext().setUserName(name);
        HiveContextHolder.getContext().setAccessToken(token);
        return token;
    }

    /**
     * 组装token
     *
     * @param accessToken
     * @return
     */
    public static String generateToken(AccessToken accessToken) {
        return Aes128Util.encrypt(JsonUtil.toJsonString(accessToken));
    }

    /**
     * 校验token
     *
     * @param token
     * @return
     */
    public static AccessToken verifyToken(String token) {
        if (StringUtils.isBlank(token)) {
            log.info("no token!");
            throw new AuthorizationException(AuthorizationException.EXCEPTIONCODE_AUTH_TOKEN_ISNULL,
                    "request.token.isnull", "token is null");
        }
        String tokenJson = Aes128Util.decrypt(token);
        AccessToken tokenObj = JsonUtil.toObject(tokenJson, AccessToken.class);
        if (tokenObj == null) {
            log.info("Convert token string to object failed: " + token);
            throw new AuthorizationException(AuthorizationException.EXCEPTIONCODE_AUTH_TOKEN_INVALID,
                    "request.invalid.token", "JsonUtil.toObject get AccessToken failed");
        }
        Date expireTime = null;
        try {
            expireTime = new Date(Long.parseLong(tokenObj.getExpire()));
        } catch (NumberFormatException e) {
            log.error("Parse the expire time failed!", e);
        }
        if (expireTime == null) {
            log.info("The token is invalid because its expire time is invalid! " + tokenJson);
            throw new AuthorizationException(AuthorizationException.EXCEPTIONCODE_AUTH_TOKEN_EXPIRED,
                    "request.token.expired", "expireTime wrong");
        }
        if (expireTime.before(new Date())) {
            log.info("The token is expired! " + tokenJson);
            throw new AuthorizationException(AuthorizationException.EXCEPTIONCODE_AUTH_TOKEN_EXPIRED,
                    "request.token.expired", "token expired");
        }
        return tokenObj;
    }

}