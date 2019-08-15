package com.yanwu.spring.cloud.file.service.impl;

import com.yanwu.spring.cloud.common.core.common.TimeStringFormat;
import com.yanwu.spring.cloud.common.core.enums.FileType;
import com.yanwu.spring.cloud.common.mvc.res.BackVO;
import com.yanwu.spring.cloud.common.mvc.vo.base.YanwuUserVO;
import com.yanwu.spring.cloud.common.utils.CheckParamUtil;
import com.yanwu.spring.cloud.common.utils.DataUtil;
import com.yanwu.spring.cloud.common.utils.FileUtil;
import com.yanwu.spring.cloud.file.consumer.base.YanwuUserConsumer;
import com.yanwu.spring.cloud.file.data.model.Attachment;
import com.yanwu.spring.cloud.file.data.repository.AttachmentRepository;
import com.yanwu.spring.cloud.file.service.AttachmentService;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.Part;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author XuBaofeng.
 * @date 2018-11-09 16:09.
 * <p>
 * description:
 */
@Slf4j
@Service
public class AttachmentServiceImpl implements AttachmentService {

    @Autowired
    private YanwuUserConsumer yanwuUserConsumer;
    @Autowired
    private AttachmentRepository attachmentRepository;
    @Autowired
    private RestTemplate restTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Attachment uploadExcel(Part file, Long id) throws Exception {
        InputStream inputStream = file.getInputStream();
        Workbook workbook;
        try {
            workbook = new HSSFWorkbook(inputStream);
        } catch (Exception ex) {
            inputStream = file.getInputStream();
            workbook = new XSSFWorkbook(inputStream);
        }
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.rowIterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                cell.setCellType(CellType.STRING);
                String value = cell.getStringCellValue();
                log.info("===== value: {}", value);
            }
        }
        Attachment attachment = new Attachment();
        attachment.setAttachmentName(file.getName());
        attachment.setAttachmentSize(file.getSize());
        attachment.setAttachmentType(FileType.EXCEL);
        return attachmentRepository.save(attachment);
    }

    @Override
    public List<List<String>> downloadExcel() throws Exception {
        List<Attachment> attachments = attachmentRepository.findAll();
        List<List<String>> contents = new ArrayList<>();
        for (Attachment attachment : attachments) {
            List<String> content = new ArrayList<>();
            content.add(String.valueOf(attachment.getId()));
            content.add(String.valueOf(attachment.getCreatedAt()));
            content.add(String.valueOf(attachment.getUpdatedAt()));
            content.add(attachment.getAttachmentName());
            content.add(String.valueOf(attachment.getAttachmentSize()));
            content.add(String.valueOf(attachment.getAttachmentType()));
            contents.add(content);
        }
        return contents;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Attachment save(Attachment attachment) throws Exception {
        return attachmentRepository.save(attachment);
    }

    @Override
    public List<Attachment> uploadFile(MultipartHttpServletRequest request, Long id) throws Exception {
        List<Attachment> attachments = new ArrayList<>();
        MultiValueMap<String, MultipartFile> multiValueMap = request.getMultiFileMap();
        List<MultipartFile> multipartFileList = multiValueMap.get("file");
        CheckParamUtil.checkListNotNullAndSizeGreaterZero(multipartFileList);
        for (MultipartFile multipartFile : multipartFileList) {
            String fileName = multipartFile.getOriginalFilename();
            FileType fileType = FileUtil.getFileTypeByName(fileName);
            String name = FileUtil.getNameByFileName(fileName);
            String basePath = "/src/file/" + fileType + File.separatorChar + DataUtil.getTimeString(System.currentTimeMillis(), TimeStringFormat.YYYY_MM_DD4);
            File myFilePath = new File(basePath);
            if (!myFilePath.exists()) {
                myFilePath.mkdirs();
            }
            String dataPath = basePath + File.separatorChar + fileName;
            FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), new File(dataPath));
            log.info("attachment upload attachmentAddress: {}", dataPath);
            Attachment attachment = new Attachment();
            attachment.setRelationId(id);
            attachment.setName(name);
            attachment.setAttachmentName(fileName);
            attachment.setAttachmentType(fileType);
            attachment.setAttachmentAddress(dataPath);
            attachment.setAttachmentSize(multipartFile.getSize());
            Attachment save = attachmentRepository.save(attachment);
            attachments.add(save);
        }
        return attachments;
    }

    @Override
    public Attachment findById(Long id) throws Exception {
        return attachmentRepository.findById(id).get();
    }

    @Override
    @GlobalTransactional
    public Attachment upPortrait(MultipartHttpServletRequest request, Long userId) throws Exception {
        // ----- 上传文件
        log.info("当前 XID: {}", RootContext.getXID());
        MultiValueMap<String, MultipartFile> multiValueMap = request.getMultiFileMap();
        List<MultipartFile> multipartFileList = multiValueMap.get("file");
        CheckParamUtil.checkListNotNullAndSizeGreaterZero(multipartFileList);
        MultipartFile multipartFile = multipartFileList.get(0);
        String fileName = multipartFile.getOriginalFilename();
        FileType fileType = FileUtil.getFileTypeByName(fileName);
        String name = FileUtil.getNameByFileName(fileName);
        String basePath = "/src/file/" + fileType + File.separatorChar + DataUtil.getTimeString(System.currentTimeMillis(), TimeStringFormat.YYYY_MM_DD4);
        File myFilePath = new File(basePath);
        if (!myFilePath.exists()) {
            myFilePath.mkdirs();
        }
        String dataPath = basePath + File.separatorChar + fileName;
        FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), new File(dataPath));
        log.info("attachment upload attachmentAddress: {}", dataPath);
        Attachment attachment = new Attachment();
        attachment.setRelationId(userId);
        attachment.setName(name);
        attachment.setAttachmentName(fileName);
        attachment.setAttachmentType(fileType);
        attachment.setAttachmentAddress(dataPath);
        attachment.setAttachmentSize(multipartFile.getSize());
        Attachment save = attachmentRepository.save(attachment);
        // ----- 修改用户头像
        YanwuUserVO yanwuUserVO = new YanwuUserVO();
        yanwuUserVO.setId(userId);
        yanwuUserVO.setPortrait(save.getId());
        restTemplate.postForObject("http://yanwu-base/backend/yanwuUser/updatePortrait", yanwuUserVO, BackVO.class);
        // yanwuUserConsumer.updatePortrait(yanwuUserVO);
        return attachment;
    }
}
