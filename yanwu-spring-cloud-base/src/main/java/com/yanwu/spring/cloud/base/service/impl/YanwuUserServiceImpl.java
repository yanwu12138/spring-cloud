package com.yanwu.spring.cloud.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwu.spring.cloud.base.data.model.YanwuUser;
import com.yanwu.spring.cloud.base.data.mapper.YanwuUserMapper;
import com.yanwu.spring.cloud.base.service.YanwuUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author XuBaofeng.
 * @date 2018-11-12 16:37.
 * <p>
 * description:
 */
@Service
public class YanwuUserServiceImpl extends ServiceImpl<YanwuUserMapper, YanwuUser> implements YanwuUserService {

    @Resource
    private YanwuUserMapper yanwuUserMapper;

    @Override
    public YanwuUser findByAccount(String account) throws Exception {
        return yanwuUserMapper.findByAccount(account);
    }

    @Override
    public String findUserNameById(Long id) throws Exception {
        return yanwuUserMapper.findUserNameById(id);
    }

    @Override
    public YanwuUser findByUserName(String userName) throws Exception {
        return yanwuUserMapper.findByUserName(userName);
    }

    @Override
    public void updatePortrait(YanwuUser yanwuUser) {
        yanwuUserMapper.updatePortrait(yanwuUser.getId(), yanwuUser.getPortrait());
    }
}
