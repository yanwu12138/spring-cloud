package com.yanwu.spring.cloud.file.service;

import com.yanwu.spring.cloud.file.data.model.Attachment;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.Part;
import java.util.List;

/**
 * @author XuBaofeng.
 * @date 2018-11-09 16:08.
 * <p>
 * description:
 */
@SuppressWarnings("all")
public interface AttachmentService {

    Attachment uploadExcel(Part file, Long id) throws Exception;

    List<List<String>> downloadExcel() throws Exception;

    Attachment save(Attachment attachment) throws Exception;

    List<Attachment> uploadFile(MultipartHttpServletRequest request, Long id) throws Exception;

    Attachment findById(Long id) throws Exception;

    Attachment upPortrait(MultipartHttpServletRequest request, Long userId) throws Exception;
}
