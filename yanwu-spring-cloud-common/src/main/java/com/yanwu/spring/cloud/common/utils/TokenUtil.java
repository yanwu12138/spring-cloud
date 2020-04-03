package com.yanwu.spring.cloud.common.utils;

import com.yanwu.spring.cloud.common.config.Contents;
import com.yanwu.spring.cloud.common.pojo.AccessToken;
import lombok.extern.slf4j.Slf4j;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/4/3 17:58.
 * <p>
 * description:
 */
@Slf4j
public class TokenUtil {

    /**
     * 登陆成功时, 根据用户ID生成token
     *
     * @param id
     * @return
     * @throws Exception
     */
    public static String loginSuccess(Long id) throws Exception {
        AccessToken accessToken = new AccessToken();
        accessToken.setId(id);
        accessToken.setExpire(String.valueOf(System.currentTimeMillis() + Contents.TOKEN_TIME_OUT));
        return Aes128Util.encrypt(JsonUtil.toJsonString(accessToken));
    }

//    /**
//     * 校验token
//     *
//     * @param tokenStr token
//     * @return
//     */
//    public static AccessToken verifyToken(String tokenStr) {
//        Assert.isTrue(!StringUtils.isBlank(tokenStr), "no token!");
//        String tokenJson = Aes128Util.decrypt(tokenStr);
//        AccessToken tokenObj = JsonUtil.toObject(tokenJson, AccessToken.class);
//        Assert.isNull(tokenObj, "no token!");
//
//        Date expireTime = null;
//        try {
//            expireTime = new Date(Long.parseLong(tokenObj.getExpire()));
//        } catch (NumberFormatException e) {
//            log.error("Parse the expire time failed!", e);
//        }
//        if (expireTime == null) {
//            log.info("The token is invalid because its expire time is invalid! " + tokenJson);
//            throw new AuthorizationException(AuthorizationException.EXCEPTIONCODE_AUTH_TOKEN_EXPIRED,
//                    "request.token.expired", "expireTime wrong");
//        }
//        if (expireTime.before(new Date())) {
//            log.info("The token is expired! " + tokenJson);
//            throw new AuthorizationException(AuthorizationException.EXCEPTIONCODE_AUTH_TOKEN_EXPIRED,
//                    "request.token.expired", "token expired");
//        }
//        return tokenObj;
//    }
}
