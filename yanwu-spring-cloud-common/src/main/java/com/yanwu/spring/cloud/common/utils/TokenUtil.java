package com.yanwu.spring.cloud.common.utils;

import com.yanwu.spring.cloud.common.core.common.Contents;
import com.yanwu.spring.cloud.common.pojo.AccessToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/4/3 17:58.
 * <p>
 * description:
 */
@SuppressWarnings("unused")
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
        accessToken.setExpire(System.currentTimeMillis() + Contents.TOKEN_TIME_OUT);
        return Aes128Util.encrypt(JsonUtil.toJsonString(accessToken), Contents.TOKEN);
    }

    /**
     * 校验token
     *
     * @param tokenStr token
     * @return token
     */
    public static AccessToken verifyToken(String tokenStr) {
        Assert.isTrue(!StringUtils.isBlank(tokenStr), "no token!");
        String tokenJson = Aes128Util.decrypt(tokenStr, Contents.TOKEN);
        AccessToken tokenObj = JsonUtil.toObject(tokenJson, AccessToken.class);
        Assert.notNull(tokenObj, "no token!");
        Long expire = tokenObj.getExpire();
        Assert.notNull(expire, "The token is expired!");
        Assert.isTrue((expire.compareTo(System.currentTimeMillis()) < 0), "The token is expired!");
        return tokenObj;
    }

}
