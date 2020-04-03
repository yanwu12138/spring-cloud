package com.yanwu.spring.cloud.file.consumer.base;

import com.yanwu.spring.cloud.common.pojo.BaseParam;
import com.yanwu.spring.cloud.common.pojo.ResponseEnvelope;
import com.yanwu.spring.cloud.file.pojo.YanwuUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
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
     * 修改用户头像
     *
     * @param param
     * @return
     */
    @PostMapping(value = "backend/yanwuUser/updatePortrait")
    ResponseEntity<ResponseEnvelope<Void>> updatePortrait(@RequestBody BaseParam<YanwuUser> param);
}
