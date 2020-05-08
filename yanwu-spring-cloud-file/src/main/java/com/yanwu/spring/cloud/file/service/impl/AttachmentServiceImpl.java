package com.yanwu.spring.cloud.file.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwu.spring.cloud.common.core.enums.FileType;
import com.yanwu.spring.cloud.common.utils.ExcelUtil;
import com.yanwu.spring.cloud.common.utils.FileUtil;
import com.yanwu.spring.cloud.file.consumer.base.YanwuUserConsumer;
import com.yanwu.spring.cloud.file.data.mapper.AttachmentMapper;
import com.yanwu.spring.cloud.file.data.model.Attachment;
import com.yanwu.spring.cloud.file.pojo.YanwuUser;
import com.yanwu.spring.cloud.file.service.AttachmentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.Part;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author XuBaofeng.
 * @date 2018-11-09 16:09.
 * <p>
 * description:
 */
@Slf4j
@Service
public class AttachmentServiceImpl extends ServiceImpl<AttachmentMapper, Attachment> implements AttachmentService {

    @Resource
    private YanwuUserConsumer yanwuUserConsumer;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Attachment uploadExcel(Part file, Long id) throws Exception {
        List<List<String>> result = ExcelUtil.analysisExcel(file, 0);
        log.info("uploadExcel: {}", result);
        Attachment attachment = new Attachment();
        attachment.setRelationId(id);
        attachment.setAttachmentSize(file.getSize());
        attachment.setName(file.getSubmittedFileName());
        attachment.setAttachmentName(file.getSubmittedFileName());
        attachment.setAttachmentType(FileType.EXCEL_07.ordinal());
        save(attachment);
        return attachment;
    }

    @Override
    public List<List<String>> downloadExcel() throws Exception {
        List<Attachment> attachments = list();
        List<List<String>> contents = new ArrayList<>();
        for (Attachment attachment : attachments) {
            List<String> content = new ArrayList<>();
            content.add(String.valueOf(attachment.getId()));
            content.add(String.valueOf(attachment.getCreated()));
            content.add(String.valueOf(attachment.getUpdated()));
            content.add(attachment.getName());
            content.add(attachment.getAttachmentName());
            content.add(String.valueOf(attachment.getAttachmentSize()));
            content.add(String.valueOf(attachment.getAttachmentType()));
            contents.add(content);
        }
        return contents;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Attachment> uploadFile(MultipartHttpServletRequest request, Long id) throws Exception {
        List<Attachment> attachments = new ArrayList<>();
        MultiValueMap<String, MultipartFile> multiValueMap = request.getMultiFileMap();
        List<MultipartFile> multipartFileList = multiValueMap.get("file");
        Assert.isTrue(CollectionUtils.isNotEmpty(multipartFileList), "file list is empty.");
        for (MultipartFile file : multipartFileList) {
            String fileName = file.getOriginalFilename();
            FileType fileType = FileType.getFileTypeByName(fileName);
            String filePath = "/src/file/" + fileType + File.separatorChar + LocalDate.now().toString();
            FileUtil.checkDirectoryPath(filePath);
            String dataPath = filePath + File.separatorChar + fileName;
            FileUtils.copyInputStreamToFile(file.getInputStream(), new File(dataPath));
            log.info("attachment upload attachmentAddress: {}", dataPath);
            Attachment attachment = new Attachment();
            attachment.setRelationId(id);
            attachment.setName(fileName);
            attachment.setAttachmentName(fileName);
            attachment.setAttachmentAddress(dataPath);
            attachment.setAttachmentSize(file.getSize());
            attachment.setAttachmentType(fileType.ordinal());
            save(attachment);
            attachments.add(attachment);
        }
        return attachments;
    }

    @Override
    public Attachment findById(Long id) throws Exception {
        return getById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Attachment upPortrait(MultipartHttpServletRequest request, Long userId) throws Exception {
        // ----- 上传文件
        List<Attachment> attachments = uploadFile(request, userId);
        Attachment attachment = attachments.stream().findFirst().orElse(new Attachment());
        // ----- 修改用户头像
        YanwuUser yanwuUserVO = new YanwuUser();
        yanwuUserVO.setId(userId);
        yanwuUserVO.setPortrait(attachment.getId());
        yanwuUserConsumer.updatePortrait(yanwuUserVO);
        return attachment;
    }

    @Override
//    @GlobalTransactional(timeoutMills = 30000, rollbackFor = Exception.class, name = "yanwu-seata-group-file")
    public YanwuUser updateAccountById(YanwuUser user) {
        return yanwuUserConsumer.updateAccountById(user);
    }

}
