package com.yanwu.spring.cloud.common.utils;

import com.yanwu.spring.cloud.common.config.Contents;
import com.yanwu.spring.cloud.common.pojo.AccessToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.Date;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/4/3 17:58.
 * <p>
 * description:
 */
public class TokenUtil {

    /**
     * 登陆成功时, 根据用户ID生成token
     *
     * @param id userId
     * @return token
     */
    public static String loginSuccess(Long id) {
        AccessToken accessToken = new AccessToken();
        accessToken.setId(id);
        accessToken.setExpire(String.valueOf(System.currentTimeMillis() + Contents.TOKEN_TIME_OUT));
        return Aes128Util.encrypt(JsonUtil.toJsonString(accessToken));
    }

    /**
     * 校验token
     *
     * @param tokenStr token
     * @return token
     */
    public static AccessToken verifyToken(String tokenStr) {
        Assert.isTrue(!StringUtils.isBlank(tokenStr), "no token!");
        String tokenJson = Aes128Util.decrypt(tokenStr);
        AccessToken tokenObj = JsonUtil.toObject(tokenJson, AccessToken.class);
        Assert.notNull(tokenObj, "no token!");
        Date expireTime = new Date(Long.parseLong(tokenObj.getExpire()));
        Assert.notNull(expireTime, "The token is expired!");
        Assert.isTrue(expireTime.after(new Date()), "The token is expired!");
        return tokenObj;
    }

}
