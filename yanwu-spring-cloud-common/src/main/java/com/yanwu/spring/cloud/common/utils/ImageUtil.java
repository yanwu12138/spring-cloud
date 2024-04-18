package com.yanwu.spring.cloud.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.core.JsonPointer.SEPARATOR;
import static com.yanwu.spring.cloud.common.utils.DateUtil.filling;

/**
 * @author XuBaofeng.
 * @date 2024/4/18 17:38.
 * <p>
 * description:
 */
@Slf4j
public class ImageUtil {
    private static final String ROOT_DIR = "/home/thumbnail/";
    private static final int width = 480, height = 720;

    private ImageUtil() {
        throw new UnsupportedOperationException("ImageUtil should never be instantiated");
    }

    public static String readThumbnail(String filepath) {
        if (StringUtils.isBlank(filepath)) {
            return null;
        }
        return readThumbnail(new File(filepath));
    }

    public static String readThumbnail(File file) {
        if (file == null || !file.exists()) {
            return null;
        }
        try {
            String thumbnailPath = buildThumbnailPath(file);
            BufferedImage originalImage = ImageIO.read(file);
            Image image = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage thumbnail = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics graphics = thumbnail.getGraphics();
            graphics.setColor(Color.RED);
            graphics.drawImage(image, 0, 0, null);
            graphics.dispose();
            File thumbnailFile = new File(thumbnailPath);
            FileUtil.checkFilePath(thumbnailPath, Boolean.TRUE);
            ImageIO.write(thumbnail, "jpg", thumbnailFile);
            return thumbnailPath;
        } catch (Exception e) {
            log.error("read file thumbnail failed. file: {}", file.getAbsolutePath(), e);
            return null;
        }
    }

    private static String buildThumbnailPath(File file) {
        Long lastTime = FileUtil.readFileCreateTime(file);
        LocalDateTime datetime = DateUtil.datetime(lastTime);
        String filepath = datetime.getYear() + SEPARATOR + filling(datetime.getMonthValue()) + SEPARATOR + DateUtil.dateStr(datetime.toLocalDate()) + "_" + file.getName();
        return ROOT_DIR + filepath;
    }
}
