package com.yanwu.spring.cloud.base.controller.webapp;

import com.yanwu.spring.cloud.base.common.YanwuConstants;
import com.yanwu.spring.cloud.base.data.model.YanwuUser;
import com.yanwu.spring.cloud.base.service.YanwuUserService;
import com.yanwu.spring.cloud.common.core.annotation.LogParam;
import com.yanwu.spring.cloud.common.pojo.ResponseEnvelope;
import com.yanwu.spring.cloud.common.utils.Aes128Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author XuBaofeng.
 * @date 2018-11-12 16:37.
 * <p>
 * description:
 */
@Slf4j
@RestController
@RequestMapping("webapp/yanwuUser/")
public class WebappYanwuUserController {

    @Resource
    private YanwuUserService userService;

    @LogParam
    @PostMapping(value = "create")
    public ResponseEntity<ResponseEnvelope<Long>> create(@RequestBody YanwuUser user) {
        // ===== 校验账号、邮箱、手机号是否存在
        Assert.isNull(userService.checkAccount(user.getAccount()), "账号已存在");
        // ===== 校验邮箱是否存在
        Assert.isNull(userService.checkEmail(user.getEmail()), "邮箱已存在");
        // ===== 校验手机号是否存在
        Assert.isNull(userService.checkPhone(user.getPhone()), "手机号有已存在");
        if (StringUtils.isBlank(user.getPassword())) {
            user.setPassword(Aes128Util.encrypt(YanwuConstants.DEFAULT_PASSWORD));
        } else {
            user.setPassword(Aes128Util.encrypt(user.getPassword()));
        }
        userService.save(user);
        return new ResponseEntity<>(new ResponseEnvelope<>(user.getId()), HttpStatus.OK);
    }

    @LogParam
    @PutMapping(value = "update")
    public ResponseEntity<ResponseEnvelope<Boolean>> update(@RequestBody YanwuUser user) {
        return new ResponseEntity<>(new ResponseEnvelope<>(Boolean.TRUE), HttpStatus.OK);
    }

    @LogParam
    @GetMapping("getById")
    public ResponseEntity<ResponseEnvelope<YanwuUser>> getById(@RequestParam("id") Long id) {
        return new ResponseEntity<>(new ResponseEnvelope<>(userService.getById(id)), HttpStatus.OK);
    }

}
