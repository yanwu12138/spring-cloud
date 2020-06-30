package com.yanwu.spring.cloud.base.service.impl;

import com.yanwu.spring.cloud.base.consumer.DeviceLightConsumer;
import com.yanwu.spring.cloud.base.consumer.FileTransactionsConsumer;
import com.yanwu.spring.cloud.base.data.model.YanwuUser;
import com.yanwu.spring.cloud.base.service.TransactionsService;
import com.yanwu.spring.cloud.base.service.YanwuUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author XuBaofeng.
 * @date 2020/6/30 16:06
 * <p>
 * description:
 */
@Service
public class TransactionsServiceImpl implements TransactionsService {

    @Resource
    private YanwuUserService userService;
    @Resource
    private DeviceLightConsumer deviceTransactionsConsumer;
    @Resource
    private FileTransactionsConsumer fileTransactionsConsumer;

    @Override
    public YanwuUser test1() {
        deviceTransactionsConsumer.test1();
        fileTransactionsConsumer.test1();

        YanwuUser user = new YanwuUser().setAccount("yanwu").setEmail("yanwu0527@163.com")
                .setPhone("13750878276").setSex(true).setPassword("123456").setRoleId(1L);
        userService.save(user);
        int i = 1 / 0;
        return user;
    }
}
