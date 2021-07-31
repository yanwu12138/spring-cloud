package com.yanwu.spring.cloud.common.utils;

import lombok.extern.slf4j.Slf4j;

import javax.sound.sampled.*;
import java.io.File;

/**
 * @author Baofeng Xu
 * @date 2021/7/31 11:56.
 * <p>
 * description: 音频处理文件
 */
@Slf4j
@SuppressWarnings("unused")
public class AudioUtil {

    public AudioUtil() {
        throw new UnsupportedOperationException("AudioUtil should never be instantiated");
    }

    /**
     * 播放音频文件
     *
     * @param path 文件绝对全路径
     * @return 播放结果[true: 成功; false: 失败]
     */
    public static boolean play(String path) {
        return play(new File(path));
    }

    /**
     * 播放音频文件
     *
     * @param file 音频文件
     * @return 播放结果[true: 成功; false: 失败]
     */
    public static boolean play(File file) {
        if (!file.isFile() || !file.exists()) {
            log.error("play audio failed, because is not a file or the file does not exist. file: {}", file.getPath());
            return false;
        }
        SourceDataLine source = null;
        try (AudioInputStream ais = AudioSystem.getAudioInputStream(file)) {
            AudioFormat audioFormat = ais.getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
            source = (SourceDataLine) AudioSystem.getLine(info);
            byte[] bytes = new byte[512];
            int length;
            source.open(audioFormat);
            source.start();
            while ((length = ais.read(bytes)) > 0) {
                source.write(bytes, 0, length);
            }
            log.info("play audio success, file: {}", file.getPath());
            return true;
        } catch (Exception e) {
            log.error("play audio failed, file: {}.", file.getPath(), e);
            return false;
        } finally {
            if (source != null) {
                source.drain();
                source.close();
            }
        }
    }

}
