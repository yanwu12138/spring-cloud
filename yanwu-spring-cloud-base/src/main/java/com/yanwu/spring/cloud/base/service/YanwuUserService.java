package com.yanwu.spring.cloud.base.service;

import com.yanwu.spring.cloud.base.data.model.YanwuUser;

/**
 * @author XuBaofeng.
 * @date 2018-11-12 16:36.
 * <p>
 * description:
 */
public interface YanwuUserService {

    /**
     * 根据用户 账号\邮箱\手机号 查找用户
     *
     * @param account
     * @return
     * @throws Exception
     */
    YanwuUser findByAccount(String account) throws Exception;

    /**
     * 根据用户ID查找用户
     *
     * @param id
     * @return
     * @throws Exception
     */
    String findUserNameById(Long id) throws Exception;

    /**
     * 保存用户
     *
     * @param yanwuUser
     * @return
     * @throws Exception
     */
    YanwuUser save(YanwuUser yanwuUser) throws Exception;

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
    void updatePortrait(YanwuUser yanwuUser) throws Exception;
}
