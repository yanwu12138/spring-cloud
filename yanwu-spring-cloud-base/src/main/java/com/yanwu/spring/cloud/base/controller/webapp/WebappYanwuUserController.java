package com.yanwu.spring.cloud.base.controller.webapp;

import com.yanwu.spring.cloud.base.common.YanwuConstants;
import com.yanwu.spring.cloud.base.data.model.YanwuUser;
import com.yanwu.spring.cloud.base.service.YanwuUserService;
import com.yanwu.spring.cloud.common.core.annotation.CheckFiled;
import com.yanwu.spring.cloud.common.core.annotation.LogAndCheckParam;
import com.yanwu.spring.cloud.common.core.aspect.CheckParamRegex;
import com.yanwu.spring.cloud.common.pojo.ResponseEnvelope;
import com.yanwu.spring.cloud.common.utils.Aes128Util;
import com.yanwu.spring.cloud.common.utils.VoDoUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private VoDoUtil voDoUtil;

    @Autowired
    private YanwuUserService userService;

    @PostMapping(value = "create")
    @LogAndCheckParam(check = {
            @CheckFiled(field = "account", message = "账号格式错误", regex = CheckParamRegex.STRING_NOT_NULL),
            @CheckFiled(field = "email", message = "邮箱格式错误", regex = CheckParamRegex.EMAIL),
            @CheckFiled(field = "sex", message = "性别不能为空", regex = CheckParamRegex.STRING_NOT_NULL),
            @CheckFiled(field = "phone", message = "手机号格式错误", regex = CheckParamRegex.PHONE_NO),
            @CheckFiled(field = "roleId", message = "所属角色不能为空", regex = CheckParamRegex.STRING_NOT_NULL)
    })
    public ResponseEntity<ResponseEnvelope<Long>> create(@RequestBody YanwuUser user) {
        // ===== 校验账号、邮箱、手机号是否存在
        Assert.isTrue(!userService.checkAccount(user.getAccount()), "账号已存在");
        // ===== 校验邮箱是否存在
        Assert.isTrue(!userService.checkEmail(user.getEmail()), "邮箱已存在");
        // ===== 校验手机号是否存在
        Assert.isTrue(!userService.checkPhone(user.getPhone()), "手机号有已存在");
        if (StringUtils.isBlank(user.getPassword())) {
            user.setPassword(Aes128Util.encrypt(YanwuConstants.DEFAULT_PASSWORD));
        } else {
            user.setPassword(Aes128Util.encrypt(user.getPassword()));
        }
        userService.save(user);
        return new ResponseEntity<>(new ResponseEnvelope<>(user.getId()), HttpStatus.OK);
    }

    @LogAndCheckParam
    @PutMapping(value = "update")
    public ResponseEntity<ResponseEnvelope<Boolean>> update(@RequestBody YanwuUser user) throws Exception {
        return new ResponseEntity<>(new ResponseEnvelope<>(Boolean.TRUE), HttpStatus.OK);
    }

}
