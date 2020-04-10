package com.yanwu.spring.cloud.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanwu.spring.cloud.base.data.model.YanwuUser;

/**
 * @author XuBaofeng.
 * @date 2018-11-12 16:36.
 * <p>
 * description:
 */
public interface YanwuUserService extends IService<YanwuUser> {

    /**
     * 根据用户 账号\邮箱\手机号 查找用户
     *
     * @param account
     * @return
     * @throws Exception
     */
    YanwuUser findByAccount(String account) throws Exception;

    /**
     * 根据用户名查找用户
     *
     * @param userName
     * @return
     * @throws Exception
     */
    YanwuUser findByUserName(String userName) throws Exception;

    /**
     * 修改用户头像
     *
     * @param yanwuUser
     */
    void updatePortrait(YanwuUser yanwuUser);

    /**
     * 检查账号是否重复
     *
     * @param account
     * @return
     */
    YanwuUser checkAccount(String account);

    /**
     * 检查邮箱是否存在
     *
     * @param email
     * @return
     */
    YanwuUser checkEmail(String email);

    /**
     * 校验手机号是否存在
     *
     * @param phone
     * @return
     */
    YanwuUser checkPhone(String phone);

    /**
     * 根据用户ID修改用户名
     *
     * @param user
     * @return
     */
    YanwuUser updateAccountById(YanwuUser user);
}
