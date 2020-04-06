package com.yanwu.spring.cloud.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanwu.spring.cloud.file.data.model.Attachment;
import com.yanwu.spring.cloud.file.pojo.YanwuUser;
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
public interface AttachmentService extends IService<Attachment> {

    Attachment uploadExcel(Part file, Long id) throws Exception;

    List<List<String>> downloadExcel() throws Exception;

    List<Attachment> uploadFile(MultipartHttpServletRequest request, Long id) throws Exception;

    Attachment findById(Long id) throws Exception;

    Attachment upPortrait(MultipartHttpServletRequest request, Long userId) throws Exception;

    YanwuUser updateAccountById(YanwuUser user);
}
