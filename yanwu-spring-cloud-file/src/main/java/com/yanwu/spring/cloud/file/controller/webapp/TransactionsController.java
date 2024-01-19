package com.yanwu.spring.cloud.file.controller.webapp;

import com.yanwu.spring.cloud.common.core.annotation.RequestHandler;
import com.yanwu.spring.cloud.file.data.model.Attachment;
import com.yanwu.spring.cloud.file.service.AttachmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author XuBaofeng.
 * @date 2018-11-12 16:37.
 * <p>
 * description: 测试分布式事务
 */
@Slf4j
@RestController
@RequestMapping("file/transactions/")
public class TransactionsController {
    @Resource
    private AttachmentService attachmentService;

    @RequestHandler
    @GetMapping(value = "test1")
    public void test1() {
        attachmentService.save(new Attachment());
    }

}
