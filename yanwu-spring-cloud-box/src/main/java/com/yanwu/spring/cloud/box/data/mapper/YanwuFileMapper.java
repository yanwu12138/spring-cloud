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

    @Select("select * from yanwu_file where path = #{path} and mark = #{mark} order by id limit 1")
    YanwuFile selectByMark(@Param("path") String filepath, @Param("mark") String fileMark);

    @Select("select * from yanwu_file where id > #{fileId} order by id asc limit 1")
    YanwuFile nextById(@Param("fileId") Long fileId);

    @Select("select * from yanwu_file where year = #{year} and id > #{fileId} order by id asc limit 1")
    YanwuFile nextByIdAndYear(@Param("fileId") Long fileId, @Param("year") String year);

    @Select("select * from yanwu_file where month = #{month} and id > #{fileId} order by id asc limit 1")
    YanwuFile nextByIdAndMonth(@Param("fileId") Long fileId, @Param("month") String month);

    @Select("select * from yanwu_file where year = #{year} and month = #{month} and id > #{fileId} order by id asc limit 1")
    YanwuFile nextByYearAndMonth(@Param("fileId") Long fileId, @Param("year") String year, @Param("month") String month);

    @Select("select * from yanwu_file where id < #{fileId} order by id desc limit 1")
    YanwuFile lastById(@Param("fileId") Long fileId);

    @Select("select * from yanwu_file where year = #{year} and id < #{fileId} order by id desc limit 1")
    YanwuFile lastByIdAndYear(@Param("fileId") Long fileId, @Param("year") String year);

    @Select("select * from yanwu_file where month = #{month} and id < #{fileId} order by id desc limit 1")
    YanwuFile lastByIdAndMonth(@Param("fileId") Long fileId, @Param("month") String month);

    @Select("select * from yanwu_file where year = #{year} and month = #{month} and id < #{fileId} order by id desc limit 1")
    YanwuFile lastByYearAndMonth(@Param("fileId") Long fileId, @Param("year") String year, @Param("month") String month);

}
