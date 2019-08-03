package com.yanwu.spring.cloud.file.controller.webapp;

import com.yanwu.spring.cloud.common.amqp.Reader;
import com.yanwu.spring.cloud.common.core.annotation.CheckParam;
import com.yanwu.spring.cloud.common.core.annotation.YanwuLog;
import com.yanwu.spring.cloud.common.core.enums.FileType;
import com.yanwu.spring.cloud.common.utils.ExcelUtil;
import com.yanwu.spring.cloud.common.utils.FileUtil;
import com.yanwu.spring.cloud.common.utils.VoDoUtil;
import com.yanwu.spring.cloud.common.mvc.req.BaseParam;
import com.yanwu.spring.cloud.common.mvc.res.BackVO;
import com.yanwu.spring.cloud.common.mvc.vo.base.YanwuUserVO;
import com.yanwu.spring.cloud.common.mvc.vo.file.AttachmentVO;
import com.yanwu.spring.cloud.file.consumer.base.YanwuUserConsumer;
import com.yanwu.spring.cloud.file.data.model.Attachment;
import com.yanwu.spring.cloud.file.service.AttachmentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
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
    private YanwuUserConsumer yanwuUserConsumer;

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private Reader reader;

    @CheckParam
    @PostMapping(value = "findYanwuUser")
    public BackVO<YanwuUserVO> findYanwuUser(@RequestBody BaseParam<String> param) throws Exception {
        return yanwuUserConsumer.findByUserName(param);
    }

    /**
     * 上传用户头像
     *
     * @param request
     * @param userId
     * @return
     * @throws Exception
     */
    @CheckParam
    @PostMapping(value = "upPortrait")
    public BackVO<AttachmentVO> upPortrait(MultipartHttpServletRequest request, @RequestParam("userId") Long userId) throws Exception {
        Attachment attachment = attachmentService.upPortrait(request, userId);
        return new BackVO<>(voDoUtil.convertDoToVo(attachment, AttachmentVO.class));
    }

    /**
     * 上传文件
     *
     * @param request
     * @param id
     * @return
     * @throws Exception
     */
    @YanwuLog
    @PostMapping(value = "uploadFile")
    public BackVO<List<AttachmentVO>> uploadFile(MultipartHttpServletRequest request, @RequestParam("id") Long id) throws Exception {
        List<Attachment> attachments = attachmentService.uploadFile(request, id);
        return new BackVO<>(voDoUtil.mapList(attachments, AttachmentVO.class));
    }

    /**
     * 下载文件
     *
     * @param id
     * @return
     * @throws Exception
     */
    @YanwuLog
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
    @YanwuLog
    @PostMapping(value = "uploadExcel")
    public BackVO<AttachmentVO> uploadExcel(@RequestPart(name = "file") Part file, @RequestParam("id") Long id) throws Exception {
        Attachment attachment = attachmentService.uploadExcel(file, id);
        return new BackVO<>(voDoUtil.convertDoToVo(attachment, AttachmentVO.class));
    }

    /**
     * 下载Excel
     *
     * @return
     * @throws Exception
     */
    @YanwuLog
    @GetMapping(value = "downloadExcel")
    public ResponseEntity<Resource> downloadExcel() throws Exception {
        List<List<String>> contents = attachmentService.downloadExcel();
        List<String> head = ExcelUtil.assembleHead("id", "created", "update", "name", "att_name", "att_size", "att_type");
        SXSSFWorkbook workbook = ExcelUtil.assembleExcel(head, contents);
        String fileName = FileUtil.getFileNameByType("downloadExcel", FileType.EXCEL);
        return ExcelUtil.export(workbook, fileName);
    }

    /**
     * 下载Excel
     *
     * @return
     * @throws Exception
     */
    @YanwuLog
    @GetMapping(value = "reader")
    public BackVO<Object> reader() throws Exception {
        Object o = reader.receiveAndConvert("test_queue_work_1", 1000);
        return new BackVO<>(o);
    }

}
