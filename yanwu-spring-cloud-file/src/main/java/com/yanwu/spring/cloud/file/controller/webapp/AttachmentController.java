package com.yanwu.spring.cloud.file.controller.webapp;

import com.yanwu.spring.cloud.common.core.annotation.LogParam;
import com.yanwu.spring.cloud.common.core.enums.FileType;
import com.yanwu.spring.cloud.common.pojo.ResponseEnvelope;
import com.yanwu.spring.cloud.common.utils.ExcelUtil;
import com.yanwu.spring.cloud.common.utils.FileUtil;
import com.yanwu.spring.cloud.common.utils.JsonUtil;
import com.yanwu.spring.cloud.file.data.model.Attachment;
import com.yanwu.spring.cloud.file.pojo.YanwuUser;
import com.yanwu.spring.cloud.file.service.AttachmentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.Part;
import java.time.LocalDate;
import java.time.LocalTime;
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

    @javax.annotation.Resource
    private AttachmentService attachmentService;

    /**
     * 上传用户头像
     *
     * @param request
     * @param userId
     * @return
     * @throws Exception
     */
    @LogParam
    @PostMapping(value = "upPortrait")
    public ResponseEnvelope<Attachment> upPortrait(MultipartHttpServletRequest request, @RequestParam("userId") Long userId) throws Exception {
        Attachment attachment = attachmentService.upPortrait(request, userId);
        Attachment result = JsonUtil.convertObject(attachment, Attachment.class);
        return ResponseEnvelope.success(result);
    }

    /**
     * 上传文件
     *
     * @param request
     * @param id
     * @return
     * @throws Exception
     */
    @LogParam
    @PostMapping(value = "uploadFile")
    public ResponseEnvelope<List<Attachment>> uploadFile(MultipartHttpServletRequest request, @RequestParam("id") Long id) throws Exception {
        List<Attachment> attachments = attachmentService.uploadFile(request, id);
        return ResponseEnvelope.success(attachments);
    }

    /**
     * 下载文件
     *
     * @param id
     * @return
     * @throws Exception
     */
    @LogParam
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
    @LogParam
    @PostMapping(value = "uploadExcel")
    public ResponseEnvelope<Attachment> uploadExcel(@RequestPart(name = "file") Part file, @RequestParam("id") Long id) throws Exception {
        Attachment attachment = attachmentService.uploadExcel(file, id);
        return ResponseEnvelope.success(attachment);
    }

    /**
     * 下载Excel
     *
     * @return
     * @throws Exception
     */
    @LogParam
    @GetMapping(value = "downloadExcel")
    public ResponseEntity<Resource> downloadExcel() throws Exception {
        List<List<String>> contents = attachmentService.downloadExcel();
        List<String> head = ExcelUtil.assembleHead("id", "created", "update", "name", "att_name", "att_size", "att_type");
        SXSSFWorkbook workbook = ExcelUtil.assembleExcel(head, contents);
        String fileName = "downloadExcel" + LocalDate.now().toString() + LocalTime.now().toString() + FileType.EXCEL_07.getSuffix();
        return ExcelUtil.exportExcel(workbook, fileName);
    }

    @LogParam
    @PostMapping("updateUser")
    public ResponseEnvelope<YanwuUser> updateUser(@RequestBody YanwuUser user) {
        user = attachmentService.updateAccountById(user);
        return ResponseEnvelope.success(user);
    }

}
