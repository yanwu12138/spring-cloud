package com.yanwu.spring.cloud.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwu.spring.cloud.base.consumer.DeviceLightConsumer;
import com.yanwu.spring.cloud.base.data.mapper.YanwuUserMapper;
import com.yanwu.spring.cloud.base.data.model.YanwuUser;
import com.yanwu.spring.cloud.base.service.YanwuUserService;
import com.yanwu.spring.cloud.common.core.annotation.RedisLock;
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
    private DeviceLightConsumer lightConsumer;
    @Resource
    private YanwuUserMapper yanwuUserMapper;

    @Override
    public YanwuUser findByAccount(String account) throws Exception {
        return yanwuUserMapper.findByAccount(account);
    }

    @Override
    public YanwuUser findByUserName(String userName) throws Exception {
        return yanwuUserMapper.findByUserName(userName);
    }

    @Override
    @RedisLock(suffix = "#yanwuUser.account")
    public void updatePortrait(YanwuUser yanwuUser) {
        yanwuUserMapper.updatePortrait(yanwuUser.getId(), yanwuUser.getPortrait());
    }

    @Override
    public YanwuUser checkAccount(String account) {
        QueryWrapper<YanwuUser> wrapper = new QueryWrapper<>();
        wrapper.eq("account", account);
        wrapper.eq("enabled", Boolean.TRUE);
        return getOne(wrapper);
    }

    @Override
    public YanwuUser checkEmail(String email) {
        QueryWrapper<YanwuUser> wrapper = new QueryWrapper<>();
        wrapper.eq("email", email);
        wrapper.eq("enabled", Boolean.TRUE);
        return getOne(wrapper);
    }

    @Override
    public YanwuUser checkPhone(String phone) {
        QueryWrapper<YanwuUser> wrapper = new QueryWrapper<>();
        wrapper.eq("phone", phone);
        wrapper.eq("enabled", Boolean.TRUE);
        return getOne(wrapper);
    }

    @Override
    public YanwuUser updateAccountById(YanwuUser user) {
        updateById(user);
        lightConsumer.create();
        return user;
    }

}
