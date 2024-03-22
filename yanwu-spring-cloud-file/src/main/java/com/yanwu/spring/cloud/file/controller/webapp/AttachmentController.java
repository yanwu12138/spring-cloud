package com.yanwu.spring.cloud.file.controller.webapp;

import com.yanwu.spring.cloud.common.core.annotation.RequestHandler;
import com.yanwu.spring.cloud.common.core.enums.FileType;
import com.yanwu.spring.cloud.common.pojo.Result;
import com.yanwu.spring.cloud.common.utils.ExcelUtil;
import com.yanwu.spring.cloud.common.utils.FileUtil;
import com.yanwu.spring.cloud.common.utils.JsonUtil;
import com.yanwu.spring.cloud.file.data.model.Attachment;
import com.yanwu.spring.cloud.file.pojo.YanwuUser;
import com.yanwu.spring.cloud.file.service.AttachmentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
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
     * @param request 头像文件
     * @param userId  userId
     */
    @RequestHandler
    @PostMapping(value = "upPortrait")
    public Result<Attachment> upPortrait(MultipartHttpServletRequest request, @RequestParam("userId") Long userId) throws Exception {
        Attachment attachment = attachmentService.upPortrait(request, userId);
        Attachment result = JsonUtil.convertObject(attachment, Attachment.class);
        return Result.success(result);
    }

    /**
     * 上传文件
     *
     * @param request 头像文件
     * @param id      操作者
     */
    @RequestHandler
    @PostMapping(value = "uploadFile")
    public Result<List<Attachment>> uploadFile(MultipartHttpServletRequest request, @RequestParam("id") Long id) throws Exception {
        List<Attachment> attachments = attachmentService.uploadFile(request, id);
        return Result.success(attachments);
    }

    /**
     * 下载文件
     *
     * @param id 操作者
     */
    @RequestHandler
    @GetMapping(value = "downloadFile/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("id") Long id, HttpServletResponse response) throws Exception {
        Attachment attachment = attachmentService.findById(id);
        if (attachment == null || StringUtils.isBlank(attachment.getAttachmentAddress())) {
            return FileUtil.exportFailed("文件不存在");
        }
        return FileUtil.exportFile(attachment.getAttachmentAddress(), response);
    }

    /**
     * 下载文件
     */
    @RequestHandler
    @GetMapping(value = "downloadFile")
    public ResponseEntity<Resource> downloadFile(@RequestParam("path") String path, HttpServletResponse response) throws Exception {
        return FileUtil.exportFile(path, response);
    }

    /**
     * 上传Excel
     *
     * @param file 文件
     * @param id   操作者
     */
    @RequestHandler
    @PostMapping(value = "uploadExcel")
    public Result<Attachment> uploadExcel(@RequestPart(name = "file") Part file, @RequestParam("id") Long id) throws Exception {
        Attachment attachment = attachmentService.uploadExcel(file, id);
        return Result.success(attachment);
    }

    /**
     * 下载Excel
     */
    @RequestHandler
    @GetMapping(value = "downloadExcel")
    public ResponseEntity<Resource> downloadExcel() throws Exception {
        List<Attachment> contents = attachmentService.downloadExcel();
        List<String> head = ExcelUtil.assembleHead("id", "created", "update", "name", "att_name", "att_size", "att_type");
        SXSSFWorkbook workbook = ExcelUtil.assembleExcelByList(head, contents);
        String fileName = "downloadExcel" + LocalDate.now() + LocalTime.now().toString() + FileType.EXCEL_07.getSuffix();
        return ExcelUtil.exportExcel(workbook, fileName);
    }

    @RequestHandler
    @PostMapping("updateUser")
    public Result<YanwuUser> updateUser(@RequestBody YanwuUser user) {
        user = attachmentService.updateAccountById(user);
        return Result.success(user);
    }

}
