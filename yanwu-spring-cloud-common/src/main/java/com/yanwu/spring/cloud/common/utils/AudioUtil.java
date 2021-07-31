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
     */
    public static void play(String path) {
        play(new File(path));
    }

    public static void play(File file) {
        log.info("play audio file: {}", file.getPath());
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
        } catch (Exception e) {
            log.error("play audio error, file: {}.", file.getPath(), e);
        } finally {
            if (source != null) {
                source.drain();
                source.close();
            }
        }
    }

}
