package com.yanwu.spring.cloud.file.controller.webapp;

import com.yanwu.spring.cloud.common.core.annotation.LogAndCheckParam;
import com.yanwu.spring.cloud.common.core.enums.FileType;
import com.yanwu.spring.cloud.common.pojo.ResponseEnvelope;
import com.yanwu.spring.cloud.common.utils.ExcelUtil;
import com.yanwu.spring.cloud.common.utils.FileUtil;
import com.yanwu.spring.cloud.common.utils.VoDoUtil;
import com.yanwu.spring.cloud.file.data.model.Attachment;
import com.yanwu.spring.cloud.file.pojo.YanwuUser;
import com.yanwu.spring.cloud.file.service.AttachmentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.Part;
import java.util.List;

/**
 * @author XuBaofeng.
 * @date 2018-11-09 15:20.
 * <p>
 * description:
 */
@Slf4j
@RestController
@RequestMapping("webapp/attachment/")
public class AttachmentController {

    @Autowired
    private VoDoUtil voDoUtil;

    @Autowired
    private AttachmentService attachmentService;

    /**
     * 上传用户头像
     *
     * @param request
     * @param userId
     * @return
     * @throws Exception
     */
    @LogAndCheckParam
    @PostMapping(value = "upPortrait")
    public ResponseEntity<ResponseEnvelope<Attachment>> upPortrait(MultipartHttpServletRequest request, @RequestParam("userId") Long userId) throws Exception {
        Attachment attachment = attachmentService.upPortrait(request, userId);
        Attachment result = voDoUtil.convertDoToVo(attachment, Attachment.class);
        return new ResponseEntity<>(new ResponseEnvelope<>(result), HttpStatus.OK);
    }

    /**
     * 上传文件
     *
     * @param request
     * @param id
     * @return
     * @throws Exception
     */
    @LogAndCheckParam
    @PostMapping(value = "uploadFile")
    public ResponseEntity<ResponseEnvelope<List<Attachment>>> uploadFile(MultipartHttpServletRequest request, @RequestParam("id") Long id) throws Exception {
        List<Attachment> attachments = attachmentService.uploadFile(request, id);
        return new ResponseEntity<>(new ResponseEnvelope<>(attachments), HttpStatus.OK);
    }

    /**
     * 下载文件
     *
     * @param id
     * @return
     * @throws Exception
     */
    @LogAndCheckParam
    @GetMapping(value = "downloadFile/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("id") Long id) throws Exception {
        Attachment attachment = attachmentService.findById(id);
        return FileUtil.exportFile(attachment.getAttachmentAddress(), attachment.getAttachmentName());
    }

    /**
     * 上传Excel
     *
     * @param file
     * @param id
     * @return
     * @throws Exception
     */
    @LogAndCheckParam
    @PostMapping(value = "uploadExcel")
    public ResponseEntity<ResponseEnvelope<Attachment>> uploadExcel(@RequestPart(name = "file") Part file, @RequestParam("id") Long id) throws Exception {
        Attachment attachment = attachmentService.uploadExcel(file, id);
        return new ResponseEntity<>(new ResponseEnvelope<>(attachment), HttpStatus.OK);
    }

    /**
     * 下载Excel
     *
     * @return
     * @throws Exception
     */
    @LogAndCheckParam
    @GetMapping(value = "downloadExcel")
    public ResponseEntity<Resource> downloadExcel() throws Exception {
        List<List<String>> contents = attachmentService.downloadExcel();
        List<String> head = ExcelUtil.assembleHead("id", "created", "update", "name", "att_name", "att_size", "att_type");
        SXSSFWorkbook workbook = ExcelUtil.assembleExcel(head, contents);
        String fileName = FileUtil.getFileNameByType("downloadExcel", FileType.EXCEL);
        return ExcelUtil.exportExcel(workbook, fileName);
    }

    @LogAndCheckParam
    @PostMapping("updateUser")
    public ResponseEntity<ResponseEnvelope<YanwuUser>> updateUser(@RequestBody YanwuUser user) {
        user = attachmentService.updateAccountById(user);
        return new ResponseEntity<>(new ResponseEnvelope<>(user), HttpStatus.OK);
    }

}
