package com.yanwu.spring.cloud.file.service.impl;

import com.yanwu.spring.cloud.common.core.common.Contents;
import com.yanwu.spring.cloud.common.core.enums.FileType;
import com.yanwu.spring.cloud.common.utils.FileUtil;
import com.yanwu.spring.cloud.file.config.FileConfig;
import com.yanwu.spring.cloud.file.data.model.Attachment;
import com.yanwu.spring.cloud.file.pojo.QrCodeReq;
import com.yanwu.spring.cloud.file.service.AttachmentService;
import com.yanwu.spring.cloud.file.service.QrCodeService;
import com.yanwu.spring.cloud.file.utils.QrCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/9 10:45.
 * <p>
 * description:
 */
@Slf4j
@Service
public class QrCodeServiceImpl implements QrCodeService {
    private static final String QR_CODE_KEY = "qr_code_key_";

    @Resource
    private FileConfig fileConfig;

    @SuppressWarnings("all")
    @Resource(name = "redisTemplate")
    private ValueOperations<String, Long> codeOperations;

    @Resource
    private AttachmentService attachmentService;

    @Override
    public Attachment create(QrCodeReq param) throws Exception {
        // ----- 生成二维码
        String checkUrl = fileConfig.getCheckQrCodeUrl() + "?key=" + param.getContent();
        BufferedImage image = QrCodeUtil.createImage(param.getLogoUrl(), checkUrl, param.getLengthOfSide(), param.getLogoLengthOfSide(), Objects.isNull(param.getNeedCompress()) ? true : param.getNeedCompress());
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, Contents.QR_CODE_EXT, baos);
            try (InputStream inputStream = new ByteArrayInputStream(baos.toByteArray())) {
                // ----- 将二维码以文件的形式写到本地
                String fileName = RandomStringUtils.randomAlphanumeric(5).toUpperCase() + FileType.JPG.getSuffix();
                String filePath = fileConfig.getCodeFilePath() + File.separator + fileName;
                FileUtil.write(inputStream, filePath);
                // ----- 入库
                Attachment attachment = new Attachment()
                        .setName(fileName)
                        .setAttachmentName(fileName)
                        .setAttachmentAddress(filePath)
                        .setAttachmentType(FileType.JPG.ordinal());
                attachmentService.save(attachment);
                // ----- 定时
                codeOperations.set(QR_CODE_KEY + param.getContent(), attachment.getId(), 10, TimeUnit.SECONDS);
                return attachment;
            }
        }
    }

    @Override
    public void check(String key, HttpServletResponse response) {
        Long content = codeOperations.get(key);
        if (content == null) {
            return;
        }
        try {
            response.sendRedirect(key);
        } catch (Exception e) {
            log.error("check qrCode error.", e);
        }
    }

}
