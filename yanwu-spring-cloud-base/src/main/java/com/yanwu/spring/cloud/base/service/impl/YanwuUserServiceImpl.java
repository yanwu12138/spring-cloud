package com.yanwu.spring.cloud.base.service.impl;

import com.yanwu.spring.cloud.base.data.model.YanwuUser;
import com.yanwu.spring.cloud.base.data.repository.YanwuUserRepository;
import com.yanwu.spring.cloud.base.service.YanwuUserService;
import io.seata.core.context.RootContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author XuBaofeng.
 * @date 2018-11-12 16:37.
 * <p>
 * description:
 */
@Slf4j
@Service
public class YanwuUserServiceImpl implements YanwuUserService {

    @Autowired
    private YanwuUserRepository yanwuUserRepository;

    @Override
    public YanwuUser findByAccount(String account) throws Exception {
        return yanwuUserRepository.findByAccount(account);
    }

    @Override
    public String findUserNameById(Long id) throws Exception {
        return yanwuUserRepository.findUserNameById(id);
    }

    @Override
    public YanwuUser save(YanwuUser yanwuUser) throws Exception {
        return yanwuUserRepository.save(yanwuUser);
    }

    @Override
    public YanwuUser findByUserName(String userName) throws Exception {
        return yanwuUserRepository.findByUserName(userName);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePortrait(YanwuUser yanwuUser) throws Exception {
        log.info("当前 XID: {}", RootContext.getXID());
        if (StringUtils.isBlank(yanwuUser.getEmail())) {
            throw new Exception("邮箱为空");
        }
        yanwuUserRepository.updatePortrait(yanwuUser.getId(), yanwuUser.getPortrait());
    }

}
