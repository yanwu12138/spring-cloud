package com.yanwu.spring.cloud.file.service;

import com.yanwu.spring.cloud.file.data.model.Attachment;
import com.yanwu.spring.cloud.file.pojo.QrCodeReq;

import javax.servlet.http.HttpServletResponse;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/9 10:45.
 * <p>
 * description:
 */
public interface QrCodeService {

    /**
     * 创建二维码
     *
     * @param param 二维码参数
     * @return 文件ID
     * @throws Exception e
     */
    Attachment create(QrCodeReq param) throws Exception;

    /**
     * 识别二维码
     *
     * @param key      key
     * @param response response
     */
    void check(String key, HttpServletResponse response);
}
