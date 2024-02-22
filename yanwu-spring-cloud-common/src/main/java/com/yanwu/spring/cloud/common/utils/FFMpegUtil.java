package com.yanwu.spring.cloud.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author XuBaofeng.
 * @date 2024/2/22 10:21.
 * <p>
 * description:
 */
@Slf4j
@SuppressWarnings("unused")
public class FFMpegUtil {

    private FFMpegUtil() {
        throw new UnsupportedOperationException("FFMpegUtil should never be instantiated");
    }


    public static void main(String[] args) {
        String exePath = "ffmpeg";
        String input = "rtsp://admin:bxtbxtbxt4g@192.168.18.224/cam/realmonitor?channel=1&subtype=0";
        String output = "rtmp://192.168.33.151:1935/live/hj";
        FFMpegBuilder builder = FFMpegBuilder.getInstance(exePath);
        builder.input(input).output(output);
        String command = builder.build();
        System.out.println(command);
        start(builder, (p) -> {
            log.info("ffmpeg function, param: {}", p);
            return "回调成功";
        }, builder.build());
    }

    public static <P> String start(@Nonnull FFMpegBuilder ffMpegBuilder, @Nonnull Function<P, String> function, @Nonnull P funcParam) {
        String resultInfo = null;
        String command = ffMpegBuilder.build();
        try {
            ProcessBuilder builder = new ProcessBuilder();
            log.info("ffmpeg command: {}", command);
            builder.command(command);
            // ----- 正常信息和错误信息合并输出
            builder.redirectErrorStream(true);
            // ----- 开启执行子线程
            Process process = builder.start();
            String line = null;
            try (InputStreamReader streamReader = new InputStreamReader(process.getInputStream());
                 BufferedReader bufferedReader = new BufferedReader(streamReader)) {
                while ((line = bufferedReader.readLine()) != null) {
                    ThreadUtil.sleep(1_000L);
                }
                resultInfo = "推流中断";
                // ===== 等待命令子线程执行完成
                int exitValue = process.waitFor();
                // ----- 完成后执行回调
                resultInfo = function.apply(funcParam);
                // ===== 销毁子线程
                process.destroy();
            }
        } catch (Exception e) {
            log.error("ffmpeg start failed, command: {}", command, e);
        }
        return resultInfo;
    }

    public static void start(@Nonnull FFMpegBuilder ffMpegBuilder) {
        String command = ffMpegBuilder.build();
        CommandUtil.execCommand(command);
    }

    public static class FFMpegBuilder implements Serializable {
        private static final long serialVersionUID = 5856560564384658468L;
        private final List<String> command = new ArrayList<>();

        private FFMpegBuilder() {
        }

        /**
         * 构造FFMpeg命令，FFMpeg命令路径不能为空
         *
         * @param commandPath FFMpeg命令路径
         */
        public static FFMpegBuilder getInstance(String commandPath) {
            if (StringUtils.isBlank(commandPath)) {
                throw new NullPointerException("the ffmpeg command path is empty.");
            }
            FFMpegBuilder builder = new FFMpegBuilder();
            builder.command.add(commandPath);
            return builder;
        }

        /**
         * 添加输入文件的路径
         *
         * @param inputPath 可以是文件路径，也可以是输入流地址
         */
        public FFMpegBuilder input(String inputPath) {
            if (StringUtils.isBlank(inputPath)) {
                throw new NullPointerException("the ffmpeg input path is empty.");
            }
            command.add("-i");
            command.add(inputPath);
            return this;
        }

        /**
         * 添加输入文件的路径，并将输入流转换成FLV格式
         *
         * @param inputPath 可以是文件路径，也可以是输入流地址
         */
        public FFMpegBuilder flv(String inputPath) {
            if (StringUtils.isBlank(inputPath)) {
                throw new NullPointerException("the ffmpeg input path is empty.");
            }
            command.add("-rtsp_transport");
            command.add("tcp");
            command.add("-i");
            command.add(inputPath);
            command.add("-vcodec");
            command.add("copy");
            command.add("-an");
            command.add("-f");
            command.add("flv");
            return this;
        }

        /**
         * 添加输出文件的路径
         *
         * @param outputPath 可以是目标文件路径，也可以是输出流地址
         */
        public FFMpegBuilder output(String outputPath) {
            if (StringUtils.isBlank(outputPath)) {
                throw new NullPointerException("the ffmpeg output path is empty.");
            }
            command.add(outputPath);
            return this;
        }

        /**
         * 覆盖输出文件
         */
        public FFMpegBuilder override() {
            command.add("-y");
            return this;
        }

        /**
         * 设置录制/转码的时长
         *
         * @param duration 形如 0.001 表示0.001秒，hh:mm:ss[.xxx]格式的记录时间也支持
         */
        public FFMpegBuilder duration(String duration) {
            if (StringUtils.isNotBlank(duration)) {
                command.add("-t");
                command.add(duration);
            }
            return this;
        }

        /**
         * 搜索到指定的起始时间
         *
         * @param position 形如 17 表示17秒，[-]hh:mm:ss[.xxx]的格式也支持
         */
        public FFMpegBuilder position(String position) {
            if (StringUtils.isNotBlank(position)) {
                command.add("-ss");
                command.add(position);
            }
            return this;
        }

        /**
         * 设置帧大小
         *
         * @param size 形如 xxx*xxx
         */
        public FFMpegBuilder size(String size) {
            if (StringUtils.isNotBlank(size)) {
                command.add("-s");
                command.add(size);
            }
            return this;
        }

        /**
         * 构建FFMpeg命令行
         *
         * @return command
         */
        public String build() {
            if (CollectionUtils.isEmpty(command)) {
                throw new NullPointerException("the ffmpeg command is empty.");
            }
            return StringUtils.join(command, " ");
        }
    }

}
