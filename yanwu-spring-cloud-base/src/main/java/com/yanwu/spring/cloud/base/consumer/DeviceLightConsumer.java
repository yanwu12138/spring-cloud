package com.yanwu.spring.cloud.base.consumer;

import com.yanwu.spring.cloud.common.pojo.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020/04/06
 * <p>
 * describe:
 */
@FeignClient(value = "yanwu-device")
public interface DeviceLightConsumer {

    @PostMapping("/device/light/create")
    Result<Long> create();

    @GetMapping("/device/transactions/test1")
    void test1();
}
