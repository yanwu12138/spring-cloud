package com.yanwu.spring.cloud.base.controller.webapp;

import com.yanwu.spring.cloud.base.data.model.YanwuUser;
import com.yanwu.spring.cloud.base.service.YanwuUserService;
import com.yanwu.spring.cloud.common.core.annotation.CheckFiled;
import com.yanwu.spring.cloud.common.core.annotation.LogAndCheckParam;
import com.yanwu.spring.cloud.common.core.aspect.CheckParamRegex;
import com.yanwu.spring.cloud.common.mvc.res.ResponseEnvelope;
import com.yanwu.spring.cloud.common.mvc.vo.base.LoginVO;
import com.yanwu.spring.cloud.common.mvc.vo.base.YanwuUserVO;
import com.yanwu.spring.cloud.common.config.Contents;
import com.yanwu.spring.cloud.common.utils.Aes128Util;
import com.yanwu.spring.cloud.common.utils.VoDoUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author XuBaofeng.
 * @date 2018-11-12 16:37.
 * <p>
 * description:
 */
@Slf4j
@RestController
@RequestMapping("webapp/login/")
public class WebappLoginController {

    @Autowired
    private VoDoUtil voDoUtil;

    @SuppressWarnings("all")
    @Resource(name = "redisTemplate")
    private ValueOperations<String, YanwuUserVO> loginTokenOperations;

    @Autowired
    private YanwuUserService yanwuUserService;

    @LogAndCheckParam(check = {
            @CheckFiled(field = "account", message = "账号格式错误!", regex = CheckParamRegex.STRING_NOT_NULL),
            @CheckFiled(field = "password", message = "密码格式错误!", regex = CheckParamRegex.PASSWORD),
            @CheckFiled(field = "captcha", message = "验证码格式错误!", regex = CheckParamRegex.CAPTCHA)
    })
    @PostMapping(value = "login")
    public ResponseEntity<ResponseEnvelope<YanwuUserVO>> login(@RequestBody LoginVO vo) throws Exception {
        // ----- 根据 用户名 邮箱 手机号 检索用户
        YanwuUser user = yanwuUserService.findByAccount(vo.getAccount());
        // ----- 校验: 当结果为null时, 说明该用户不存在
        Assert.isTrue(Objects.nonNull(user), "user does not exist.");
        // ----- 校验密码: 当密码不匹配是说明密码错误
        String password = Aes128Util.encrypt(vo.getPassword());
        Assert.isTrue(StringUtils.equals(password, user.getPassword()), "password error.");
        YanwuUserVO userVO = voDoUtil.convertDoToVo(user, YanwuUserVO.class);
        // ----- TODO 得到token, 保存缓存
        loginTokenOperations.set(Contents.LOGIN_TOKEN + user.getId(), userVO, Contents.TOKEN_TIME_OUT, TimeUnit.SECONDS);
        return new ResponseEntity<>(new ResponseEnvelope<>(userVO), HttpStatus.OK);
    }

    @LogAndCheckParam
    @PostMapping(value = "logout/{id}")
    public ResponseEntity<ResponseEnvelope<Boolean>> logout(@PathVariable("id") Long id) throws Exception {
        Boolean result;
        YanwuUserVO yanwuUserVO = loginTokenOperations.get(Contents.LOGIN_TOKEN + id);
        if (yanwuUserVO == null) {
            result = true;
        } else {
            result = loginTokenOperations.getOperations().delete(Contents.LOGIN_TOKEN + id);
        }
        return new ResponseEntity<>(new ResponseEnvelope<>(result), HttpStatus.OK);
    }

}
