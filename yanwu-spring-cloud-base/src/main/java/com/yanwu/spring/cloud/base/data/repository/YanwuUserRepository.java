package com.yanwu.spring.cloud.base.data.repository;

import com.yanwu.spring.cloud.base.data.model.YanwuUser;
import com.yanwu.spring.cloud.common.data.repository.BaseDoRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author XuBaofeng.
 * @date 2018-11-12 16:36.
 * <p>
 * description:
 */
@SuppressWarnings("all")
public interface YanwuUserRepository extends BaseDoRepository<YanwuUser> {

    @Query("select y from YanwuUser y where y.account=?1 or y.phone=?1 or y.email=?1")
    YanwuUser findByAccount(String account) throws Exception;

    @Query("select y.account from YanwuUser y where y.id=?1")
    String findUserNameById(Long id) throws Exception;

    @Query("select y from YanwuUser y where y.name=?1")
    YanwuUser findByUserName(String userName) throws Exception;

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update YanwuUser y set y.portrait=?2 where y.id=?1")
    void updatePortrait(Long id, Long portrait);
}
