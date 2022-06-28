package com.yanwu.spring.cloud.common.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Baofeng Xu
 * @date 2022/6/26 18:23.
 * <p>
 * description:
 */
@Slf4j
public class FFMPEGUtil {

    private FFMPEGUtil() {
        throw new UnsupportedOperationException("FFMPEGUtil should never be instantiated");
    }

    /**
     * 根据M3U8地址下载文件
     *
     * @param url      资源路径
     * @param filepath 文件路径
     * @throws Exception
     */
    public static void downloadM3u8(String url, String filepath) throws Exception {
        String cmd = "ffmpeg -i " + url + " -vcodec h264 -b:v 0 -c copy " + filepath;
        CommandUtil.execCommand(cmd);
    }

}
