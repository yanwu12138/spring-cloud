package com.yanwu.spring.cloud.file.controller.webapp;

import com.yanwu.spring.cloud.common.core.annotation.RequestHandler;
import com.yanwu.spring.cloud.common.pojo.Result;
import com.yanwu.spring.cloud.file.data.model.Attachment;
import com.yanwu.spring.cloud.file.pojo.QrCodeReq;
import com.yanwu.spring.cloud.file.service.QrCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/9 10:41.
 * <p>
 * description: 二维码
 */
@Slf4j
@RestController
@RequestMapping("webapp/qrCode/")
public class QrCodeController {

    @Resource
    private QrCodeService codeService;

    @PostMapping("/create")
    @RequestHandler("生成二维码失败")
    public Result<Attachment> create(@RequestBody @Valid QrCodeReq param) throws Exception {
        return Result.success(codeService.create(param));
    }

    @GetMapping("/check")
    @RequestHandler("识别二维码失败")
    public Result<Void> check(@RequestParam("key") @NotBlank(message = "key不能为空") String key, HttpServletResponse response) {
        codeService.check(key, response);
        return Result.success();
    }
}
