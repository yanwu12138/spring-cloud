package com.yanwu.spring.cloud.box.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanwu.spring.cloud.box.data.model.YanwuFile;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * @author XuBaofeng.
 * @date 2024/4/18 17:00.
 * <p>
 * description:
 */
@Repository
public interface YanwuFileMapper extends BaseMapper<YanwuFile> {

    @Select("select * from yanwu_file where mark = #{mark} order by id limit 1")
    YanwuFile selectByMark(@Param("mark") String fileMark);

}
