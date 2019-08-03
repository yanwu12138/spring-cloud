package com.yanwu.spring.cloud.common.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-08-03 17:08.
 * <p>
 * description:
 */
@Slf4j
@Component
public class RedisServer {

    @Autowired
    private RedisTemplate redisTemplate;

}
