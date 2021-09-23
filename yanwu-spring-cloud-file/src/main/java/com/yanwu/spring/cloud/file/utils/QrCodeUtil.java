package com.yanwu.spring.cloud.file.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.yanwu.spring.cloud.common.utils.DownLoadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Hashtable;
import java.util.Objects;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/9 10:58.
 * <p>
 * description:
 */
@Slf4j
public class QrCodeUtil {

    private QrCodeUtil() {
        throw new UnsupportedOperationException("QrCodeUtil should never be instantiated");
    }

    /**
     * 编码格式,采用utf-8
     */
    private static final String UNICODE = "utf-8";

    /**
     * @param content      内容
     * @param lengthOfSide 边长
     * @param needCompress
     * @return
     * @throws Exception
     */
    public static BufferedImage createImage(String imageUrl, String content, int lengthOfSide, Integer logoLengthOfSide, boolean needCompress) throws Exception {
        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, UNICODE);
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, lengthOfSide, lengthOfSide, hints);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        if (Objects.nonNull(logoLengthOfSide) && StringUtils.isNotEmpty(imageUrl)) {
            insertLogoToImage(imageUrl, image, lengthOfSide, logoLengthOfSide, needCompress);
        }
        return image;
    }


    private static void insertLogoToImage(String imageUrl, BufferedImage source, int lengthOfSide, int logoLengthOfSide, boolean needCompress) throws Exception {
        Image src = ImageIO.read(new URL(imageUrl));
        int width = src.getWidth(null);
        int height = src.getHeight(null);
        // 压缩LOGO
        if (needCompress) {
            if (width > logoLengthOfSide) {
                width = logoLengthOfSide;
            }
            if (height > logoLengthOfSide) {
                height = logoLengthOfSide;
            }
            Image image = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics g = tag.getGraphics();
            // 绘制缩小后的图
            g.drawImage(image, 0, 0, null);
            g.dispose();
            src = image;
        }
        // 插入LOGO
        Graphics2D graph = source.createGraphics();
        int x = (lengthOfSide - width) / 2;
        int y = (lengthOfSide - height) / 2;
        graph.drawImage(src, x, y, width, height, null);
        Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
        graph.setStroke(new BasicStroke(3f));
        graph.draw(shape);
        graph.dispose();
    }

    public static void main(String[] args) throws Exception {
        DownLoadUtil.download("https://bird-ops.oss-cn-shanghai.aliyuncs.com/upgrade/package/2.0/1002/1002-2.2.1.4.bin", "E:\\download\\111.zip", "330ea82fdc0a27370ee59652f4fb8f24");
    }
}
