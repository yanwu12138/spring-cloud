package com.yanwu.spring.cloud.netty.controller;

import com.yanwu.spring.cloud.common.core.annotation.LogAndCheckParam;
import com.yanwu.spring.cloud.netty.handler.Handler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-08-09 14:45.
 * <p>
 * description:
 */
@Slf4j
@RestController
@RequestMapping("webapp/netty/")
public class NettyController {

    @Resource
    private Handler handler;

    @LogAndCheckParam
    @PostMapping("send")
    public void send() {//@RequestBody CommandVO command
//        handler.send(command.getCtxId(), String.valueOf(command.getData()));
    }

}
