package com.yanwu.spring.cloud.common.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;

/**
 * @author Baofeng Xu
 * @date 2022/7/7 17:24.
 * <p>
 * description:
 */
@SuppressWarnings("unused")
public class PasswordUtil {
    private static final String[] BASE_CIPHER = {"v", "3", "4", "7", "t", "5", "8", "a", "p", "c", "d", "j", "e", "f", "h", "i", "k", "m", "n", "r", "s", "u", "w", "x", "y"};
    private static final String SECRET = "-}<dacc3836fcc23a71dcbc9e835afd471a>{";

    public PasswordUtil() {
        throw new UnsupportedOperationException("PasswordUtil should never be instantiated");
    }

    /**
     * 根据SN生成密码
     */
    public static String passwordBySN(String sn) {
        return passwordBySN(sn, SECRET);
    }

    public static String passwordBySN(String sn, String secret) {
        if (StringUtils.isBlank(sn)) {
            return null;
        }
        secret = StringUtils.isNotBlank(secret) ? secret : SECRET;
        String md5Str = DigestUtils.md5Hex((sn + secret).getBytes(StandardCharsets.UTF_8));
        int index = 0;
        StringBuilder password = new StringBuilder();
        do {
            String substring = md5Str.substring(index, index + 4);
            md5Str = md5Str.substring(index + 4);
            int baseIndex = Integer.valueOf(substring, 16) % BASE_CIPHER.length;
            password.append(BASE_CIPHER[baseIndex]);
        } while (md5Str.length() >= 4);
        return password.toString();
    }

}
