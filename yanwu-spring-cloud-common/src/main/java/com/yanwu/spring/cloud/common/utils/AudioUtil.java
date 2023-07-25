package com.yanwu.spring.cloud.common.utils;

import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.sound.sampled.*;
import java.io.File;
import java.io.InputStream;
import java.util.Objects;

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
     * Java Music 播放 flac
     *
     * @param filepath 文件绝对路径
     * @return 播放结果[true: 成功; false: 失败]
     */
    public synchronized static boolean playFlac(String filepath) {
        return playFlac(new File(filepath));
    }

    /**
     * Java Music 播放 flac
     *
     * @param file 音频文件
     * @return 播放结果[true: 成功; false: 失败]
     */
    public synchronized static boolean playFlac(File file) {
        if (!FileUtil.fileExists(file) || !file.getName().toLowerCase().endsWith(AudioEnum.FLAC.getSuffix())) {
            log.error("play flac audio failed, because is not a file or the file does not exist. file: {}", file.getPath());
            return false;
        }
        AudioInputStream inputStream = null;
        SourceDataLine sourceDataLine = null;
        try {
            inputStream = AudioSystem.getAudioInputStream(file);
            AudioFormat format = inputStream.getFormat();
            if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
                format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, format.getSampleRate(), 16, format.getChannels(), format.getChannels() * 2, format.getSampleRate(), false);
                inputStream = AudioSystem.getAudioInputStream(format, inputStream);
            }
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format, AudioSystem.NOT_SPECIFIED);
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
            play(inputStream, sourceDataLine, format);
            return true;
        } catch (Exception e) {
            log.error("play audio failed, file: {}.", file.getPath(), e);
            return false;
        } finally {
            close(inputStream, sourceDataLine);
        }
    }

    /**
     * Java Music 播放 wav
     *
     * @param filepath 文件绝对路径
     * @return 播放结果[true: 成功; false: 失败]
     */
    public synchronized static boolean playWav(String filepath) {
        return playWav(new File(filepath));
    }

    /**
     * Java Music 播放 wav
     *
     * @param file 音频文件
     * @return 播放结果[true: 成功; false: 失败]
     */
    public synchronized static boolean playWav(File file) {
        if (!FileUtil.fileExists(file) || !file.getName().toLowerCase().endsWith(AudioEnum.WAV.getSuffix())) {
            log.error("play wav audio failed, because is not a file or the file does not exist. file: {}", file.getPath());
            return false;
        }
        AudioInputStream inputStream = null;
        SourceDataLine sourceDataLine = null;
        try {
            inputStream = AudioSystem.getAudioInputStream(file);
            AudioFormat format = inputStream.getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
            play(inputStream, sourceDataLine, format);
            return true;
        } catch (Exception e) {
            log.error("play audio failed, file: {}.", file.getPath(), e);
            return false;
        } finally {
            close(inputStream, sourceDataLine);
        }
    }

    /**
     * Java Music 播放 wav
     *
     * @param filepath 文件绝对路径
     * @return 播放结果[true: 成功; false: 失败]
     */
    public synchronized static boolean playPcm(String filepath) {
        return playPcm(new File(filepath));
    }

    /**
     * Java Music 播放 pcm
     *
     * @param file 音频文件
     * @return 播放结果[true: 成功; false: 失败]
     */
    public synchronized static boolean playPcm(File file) {
        if (!FileUtil.fileExists(file) || !file.getName().toLowerCase().endsWith(AudioEnum.PCM.getSuffix())) {
            log.error("play pcm audio failed, because is not a file or the file does not exist. file: {}", file.getPath());
            return false;
        }
        AudioInputStream inputStream = null;
        SourceDataLine sourceDataLine = null;
        try {
            inputStream = AudioSystem.getAudioInputStream(file);
            AudioFormat format = inputStream.getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format, AudioSystem.NOT_SPECIFIED);
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
            play(inputStream, sourceDataLine, format);
            return true;
        } catch (Exception e) {
            log.error("play audio failed, file: {}.", file.getPath(), e);
            return false;
        } finally {
            close(inputStream, sourceDataLine);
        }
    }

    /**
     * Java Music 播放 mp3
     *
     * @param filepath 文件绝对路径
     * @return 播放结果[true: 成功; false: 失败]
     */
    public synchronized static boolean playMp3(String filepath) {
        return playMp3(new File(filepath));
    }

    /**
     * Java Music 播放 mp3
     *
     * @param file 音频文件
     * @return 播放结果[true: 成功; false: 失败]
     */
    public synchronized static boolean playMp3(File file) {
        if (!FileUtil.fileExists(file) || !file.getName().toLowerCase().endsWith(AudioEnum.MP3.getSuffix())) {
            log.error("play mp3 audio failed, because is not a file or the file does not exist. file: {}", file.getPath());
            return false;
        }
        AudioInputStream inputStream = null;
        SourceDataLine sourceDataLine = null;
        try {
            MpegAudioFileReader reader = new MpegAudioFileReader();
            inputStream = reader.getAudioInputStream(file);
            AudioFormat baseFormat = inputStream.getFormat();
            AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(), 16, baseFormat.getChannels(), baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);
            inputStream = AudioSystem.getAudioInputStream(format, inputStream);
            AudioFormat target = inputStream.getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, target, AudioSystem.NOT_SPECIFIED);
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
            play(inputStream, sourceDataLine, target);
            return true;
        } catch (Exception e) {
            log.error("play audio failed, file: {}.", file.getPath(), e);
            return false;
        } finally {
            close(inputStream, sourceDataLine);
        }
    }

    private static void play(AudioInputStream stream, SourceDataLine source, AudioFormat format) throws Exception {
        source.open(format);
        source.start();
        int length;
        byte[] buffer = new byte[1024];
        while ((length = stream.read(buffer)) > 0) {
            source.write(buffer, 0, length);
        }
    }


    /*** 释放资源 ***/
    private static void close(InputStream stream, SourceDataLine source) {
        IOUtil.close(stream);
        if (Objects.nonNull(source)) {
            try {
                source.drain();
                source.stop();
                source.close();
            } catch (Exception e) {
                log.error("close SourceDataLine error.", e);
            }
        }
    }

    @Getter
    private enum AudioEnum {
        // ----- 音频文件后缀名
        FLAC(".flac"),
        WAV(".wav"),
        PCM(".pcm"),
        MP3(".mp3"),
        ;
        private final String suffix;

        AudioEnum(String suffix) {
            this.suffix = suffix;
        }

    }

}
