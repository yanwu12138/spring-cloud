package com.yanwu.spring.cloud.base.controller.backend;

import com.yanwu.spring.cloud.base.data.model.YanwuUser;
import com.yanwu.spring.cloud.base.service.TransactionsService;
import com.yanwu.spring.cloud.common.core.annotation.RequestLog;
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
@RequestMapping("base/")
public class TransactionsController {
    @Resource
    private TransactionsService transactionsService;

    @RequestLog
    @GetMapping(value = "test1")
//    @GlobalTransactional(rollbackFor = Exception.class)
    public YanwuUser test1() {
        return transactionsService.test1();
    }

}
