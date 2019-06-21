package com.yanwu.spring.cloud.base.service.impl;

import com.yanwu.spring.cloud.base.data.model.YanwuUser;
import com.yanwu.spring.cloud.base.data.repository.YanwuUserRepository;
import com.yanwu.spring.cloud.base.service.YanwuUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author XuBaofeng.
 * @date 2018-11-12 16:37.
 * <p>
 * description:
 */
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
    public void updatePortrait(YanwuUser yanwuUser) {
        yanwuUserRepository.updatePortrait(yanwuUser.getId(), yanwuUser.getPortrait());
    }
}
