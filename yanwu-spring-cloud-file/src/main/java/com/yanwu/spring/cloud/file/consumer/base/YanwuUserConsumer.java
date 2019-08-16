package com.yanwu.spring.cloud.file.consumer.base;

import com.yanwu.spring.cloud.common.mvc.req.BaseParam;
import com.yanwu.spring.cloud.common.mvc.res.BackVO;
import com.yanwu.spring.cloud.common.mvc.vo.base.YanwuUserVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author XuBaofeng.
 * @date 2018-11-15 18:23.
 * <p>
 * description:
 */
@Component
@FeignClient(name = "yanwu-base", url = "127.0.0.1:8881")
public interface YanwuUserConsumer {

    /**
     * 根据名称查找用户
     *
     * @param param
     * @return
     * @throws Exception
     */
    @PostMapping(value = "backend/yanwuUser/findByUserName")
    BackVO<YanwuUserVO> findByUserName(@RequestBody BaseParam<String> param) throws Exception;

    /**
     * 修改用户头像
     *
     * @param param
     * @return
     */
    @PostMapping(value = "backend/yanwuUser/updatePortrait")
    BackVO<Void> updatePortrait(@RequestBody YanwuUserVO param) throws Exception;
}
