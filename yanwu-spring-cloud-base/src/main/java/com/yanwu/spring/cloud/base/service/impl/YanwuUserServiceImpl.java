package com.yanwu.spring.cloud.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwu.spring.cloud.base.data.mapper.YanwuUserMapper;
import com.yanwu.spring.cloud.base.data.model.YanwuUser;
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
    public YanwuUser findByUserName(String userName) throws Exception {
        return yanwuUserMapper.findByUserName(userName);
    }

    @Override
    public void updatePortrait(YanwuUser yanwuUser) {
        yanwuUserMapper.updatePortrait(yanwuUser.getId(), yanwuUser.getPortrait());
    }

    @Override
    public boolean checkAccount(String account) {
        QueryWrapper<YanwuUser> wrapper = new QueryWrapper<>();
        wrapper.eq("account", account);
        return getOne(wrapper) != null;
    }

    @Override
    public boolean checkEmail(String email) {
        QueryWrapper<YanwuUser> wrapper = new QueryWrapper<>();
        wrapper.eq("email", email);
        return getOne(wrapper) != null;
    }

    @Override
    public boolean checkPhone(String phone) {
        QueryWrapper<YanwuUser> wrapper = new QueryWrapper<>();
        wrapper.eq("phone", phone);
        return getOne(wrapper) != null;
    }
}
