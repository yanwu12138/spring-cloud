package com.yanwu.spring.cloud.common.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;

/**
 * @author Baofeng Xu
 * @date 2022/7/7 17:24.
 * <p>
 * description: 该加密方式不可逆
 */
@SuppressWarnings("unused")
public class EncryptUtil {
    private static final String[] BASE_CIPHER = {"v", "3", "4", "7", "t", "5", "8", "a", "p", "c", "d", "j", "e", "f", "h", "i", "k", "m", "n", "r", "s", "u", "w", "x", "y"};
    private static final String SECRET = "-}<dacc3836fcc23a71dcbc9e835afd471a>{";

    public EncryptUtil() {
        throw new UnsupportedOperationException("EncryptUtil should never be instantiated");
    }

    /**
     * 根据content生成密码
     */
    public static String encrypt(String content) {
        return encrypt(content, SECRET);
    }

    public static String encrypt(String content, String secret) {
        if (StringUtils.isBlank(content)) {
            return null;
        }
        secret = StringUtils.isNotBlank(secret) ? secret : SECRET;
        String md5Str = DigestUtils.md5Hex((content + secret).getBytes(StandardCharsets.UTF_8));
        StringBuilder encrypt = new StringBuilder();
        do {
            String substring = md5Str.substring(0, 4);
            md5Str = md5Str.substring(4);
            int baseIndex = Integer.valueOf(substring, 16) % BASE_CIPHER.length;
            encrypt.append(BASE_CIPHER[baseIndex]);
        } while (md5Str.length() >= 4);
        return encrypt.toString();
    }

}
