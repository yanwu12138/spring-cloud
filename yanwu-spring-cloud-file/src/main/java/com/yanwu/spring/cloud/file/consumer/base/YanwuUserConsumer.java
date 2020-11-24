package com.yanwu.spring.cloud.file.consumer.base;

import com.yanwu.spring.cloud.common.pojo.ResponseEnvelope;
import com.yanwu.spring.cloud.file.consumer.base.fallback.YanwuUserFallbackFactory;
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
@FeignClient(name = "yanwu-base", fallbackFactory = YanwuUserFallbackFactory.class)
public interface YanwuUserConsumer {

    /**
     * 修改用户头像
     *
     * @param yanwuUser
     * @return
     */
    @PostMapping(value = "backend/yanwuUser/updatePortrait")
    ResponseEnvelope<Void> updatePortrait(@RequestBody YanwuUser yanwuUser);

    @PostMapping(value = "backend/yanwuUser/updateAccountById")
    YanwuUser updateAccountById(@RequestBody YanwuUser user);
}
