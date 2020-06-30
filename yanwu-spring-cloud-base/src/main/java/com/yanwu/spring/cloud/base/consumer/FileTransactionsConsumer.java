package com.yanwu.spring.cloud.base.consumer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author XuBaofeng.
 * @date 2020/6/30 10:40
 * <p>
 * description:
 */
@FeignClient(value = "yanwu-file")
public interface FileTransactionsConsumer {
    @GetMapping("/file/transactions/test1")
    void test1();
}
