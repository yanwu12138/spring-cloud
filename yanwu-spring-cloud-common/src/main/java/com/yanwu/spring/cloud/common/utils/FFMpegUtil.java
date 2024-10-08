package com.yanwu.spring.cloud.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * @author XuBaofeng.
 * @date 2024/2/22 10:21.
 * <p>
 * description:
 */
@Slf4j
@SuppressWarnings("unused")
public class FFMpegUtil {
    private static final List<String> VIDEO_EXTENSIONS = Arrays.asList(".mp4", ".avi", ".mov", ".mkv", ".mpg", ".wmv", ".flv", ".webm");

    private FFMpegUtil() {
        throw new UnsupportedOperationException("FFMpegUtil should never be instantiated");
    }

    private static synchronized void videoCompression(String source, String target) {
        if (StringUtils.isBlank(source) || !isVideoFile(source) || StringUtils.isBlank(target)) {
            return;
        }
        File sourceFile = new File(source);
        if (!sourceFile.exists() || sourceFile.isDirectory()) {
            return;
        }
        File targetFile = new File(target);
        if (targetFile.exists()) {
            FileUtil.deleteFile(targetFile);
        } else {
            FileUtil.checkDirectoryPath(targetFile.getParentFile());
        }
        String command = "ffmpeg -y -i " + source + " -c:a copy -c:v libx264 -profile:v high -crf 30 -movflags +faststart " + target;
        CommandUtil.execCommand(command);
    }

    public static boolean isVideoFile(String filepath) {
        for (String extension : VIDEO_EXTENSIONS) {
            if (filepath.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }


}
