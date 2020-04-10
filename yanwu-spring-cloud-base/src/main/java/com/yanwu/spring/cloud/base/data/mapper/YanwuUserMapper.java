package com.yanwu.spring.cloud.base.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanwu.spring.cloud.base.data.model.YanwuUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author XuBaofeng.
 * @date 2018-11-12 16:36.
 * <p>
 * description:
 */
@SuppressWarnings("all")
public interface YanwuUserMapper extends BaseMapper<YanwuUser> {

    @Select("select y.* from yanwu_user as y where y.account=#{account} or y.phone=#{account} or y.email=#{account}")
    YanwuUser findByAccount(@Param("account") String account) throws Exception;

    @Select("select y.account from yanwu_user as where y.id=${id}")
    String findUserNameById(@Param("id") Long id) throws Exception;

    @Select("select y.* from yanwu_user as where y.name=${userName}")
    YanwuUser findByUserName(@Param("userName") String userName) throws Exception;

    @Transactional
    @Update("update yanwu_user set y.portrait=${portrait} where y.id=${id}")
    void updatePortrait(@Param("id") Long id, @Param("portrait") Long portrait);
}
