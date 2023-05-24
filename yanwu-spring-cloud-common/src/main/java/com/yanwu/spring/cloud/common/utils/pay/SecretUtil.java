package com.yanwu.spring.cloud.common.utils.pay;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Baofeng Xu
 * @date 2021/7/8 11:08.
 * <p>
 * description: Secret算法
 */
public class SecretUtil {
    private static final String ALGORITHM_NAME = "AES";
    private static final String ALGORITHM_NAME_ECB_PADDING = "AES/ECB/PKCS5Padding";

    private SecretUtil() {
        throw new UnsupportedOperationException("SecretUtil should never be instantiated");
    }

    public static void main(String[] args) {
        System.out.println(secret("bf00fa01c4a822eafd83c554e15d4d0d", System.currentTimeMillis()));
    }

    /**
     * 使用AppID&当前时间计算Secret
     *
     * @param appId     明文
     * @param timestamp 当前系统UTC毫秒时间戳
     * @return 密文
     */
    public static String secret(String appId, long timestamp) {
        if (appId == null || appId.length() != 32) {
            return null;
        }
        try {
            Cipher cipher = generateEcbCipher(appId);
            byte[] bytes = cipher.doFinal(String.valueOf(timestamp).getBytes());
            return bytes != null && bytes.length > 0 ? bytesToHexStr(bytes) : null;
        } catch (Exception e) {
            System.out.println("calc secret failed. appId: " + appId);
            e.printStackTrace();
            return null;
        }
    }

    private static Cipher generateEcbCipher(String appId) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM_NAME_ECB_PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(appId.getBytes(), ALGORITHM_NAME));
        return cipher;
    }

    public static String bytesToHexStr(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString((b & 0xFF));
            result.append(headFill0(hex));
        }
        return result.toString();
    }

    public static String headFill0(String source) {
        if (source == null || source.length() == 0) {
            source = "00";
        }
        if (source.length() >= 2) {
            return source;
        }
        return "0" + source;
    }

}
