package com.yanwu.spring.cloud.message.controller;

import com.yanwu.spring.cloud.common.pojo.Result;
import com.yanwu.spring.cloud.message.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Baofeng Xu
 * @date 2022/5/30 12:57.
 * <p>
 * description:
 */

@Slf4j
@RestController
@RequestMapping("/send/websocket/message/")
public class WebSocketController {

    /**
     * 推送数据接口 [TEST]
     *
     * @see websocket-test.html
     */
    @ResponseBody
    @RequestMapping("/sender")
    public Result<Void> sender(@RequestParam(value = "message") String message,
                               @RequestParam(value = "accountId", required = false) String accountId) {
        if (StringUtils.isBlank(accountId)) {
            WebSocketService.sendMessageToAll(message);
        } else {
            accountId = accountId.replace(" ", "");
            if (!accountId.contains(",")) {
                WebSocketService.sendMessageByAccountId(message, accountId);
            } else {
                WebSocketService.sendMessageByAccountIds(message, accountId.split(","));
            }
        }
        return Result.success();
    }

}
