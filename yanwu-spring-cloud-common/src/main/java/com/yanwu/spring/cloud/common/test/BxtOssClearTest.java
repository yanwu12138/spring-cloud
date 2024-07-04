package com.yanwu.spring.cloud.common.test;

import com.yanwu.spring.cloud.common.pojo.OssProperties;
import com.yanwu.spring.cloud.common.utils.AliOssUtil;

import java.util.Arrays;
import java.util.List;

/**
 * @author XuBaofeng.
 * @date 2024/7/4 16:35.
 * <p>
 * description:
 */
public class BxtOssClearTest {

    private static final String ACCESS_ID = "";
    private static final String ACCESS_KEY = "";
    private static final String ENDPOINT = "";
    private static final String BUCKET = "";
    private static final List<String> PREFIX_LIST = Arrays.asList("fisherOne/hstt/zip/", "fisherOne/hstt/jpg/"
            , "fisherOne/dyjx/video/", "fisherOne/dyjx/zip/");

    public static void main(String[] args) {
        OssProperties properties = OssProperties.getInstance(ACCESS_ID, ACCESS_KEY, ENDPOINT, BUCKET);
        String month = "202403";
        PREFIX_LIST.forEach(item -> AliOssUtil.deleteDir(properties, item + month));
    }

}
