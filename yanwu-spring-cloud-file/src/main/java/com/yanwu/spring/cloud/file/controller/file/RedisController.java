package com.yanwu.spring.cloud.file.controller.file;

import com.yanwu.spring.cloud.common.core.annotation.RequestLog;
import com.yanwu.spring.cloud.common.pojo.ResponseEnvelope;
import com.yanwu.spring.cloud.common.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Baofeng Xu
 * @date 2023-02-15 015 10:26:30.
 * <p>
 * description:
 */
@Slf4j
@RestController
@RequestMapping("file/redis/")
public class RedisController {

    @Resource
    private RedisUtil redisUtil;


    @RequestLog
    @PostMapping("increment/{key}")
    public ResponseEnvelope<Long> increment(@PathVariable(value = "key") String param) {
        return ResponseEnvelope.success(redisUtil.increment(param));
    }

}
