package com.yanwu.spring.cloud.base.controller.webapp;

import com.yanwu.spring.cloud.base.data.model.YanwuUser;
import com.yanwu.spring.cloud.base.service.YanwuUserService;
import com.yanwu.spring.cloud.base.vo.LoginVO;
import com.yanwu.spring.cloud.base.vo.YanwuUserVO;
import com.yanwu.spring.cloud.common.core.annotation.RequestHandler;
import com.yanwu.spring.cloud.common.core.common.Contents;
import com.yanwu.spring.cloud.common.pojo.Result;
import com.yanwu.spring.cloud.common.utils.JsonUtil;
import com.yanwu.spring.cloud.common.utils.TokenUtil;
import com.yanwu.spring.cloud.common.utils.secret.Aes128Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
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

    @SuppressWarnings("all")
    @Resource(name = "redisTemplate")
    private ValueOperations<String, YanwuUserVO> loginTokenOperations;

    @Autowired
    private YanwuUserService yanwuUserService;

    @RequestHandler
    @PostMapping(value = "login")
    public Result<YanwuUserVO> login(@RequestBody @Valid LoginVO vo) throws Exception {
        // ----- 根据 用户名 邮箱 手机号 检索用户
        YanwuUser user = yanwuUserService.findByAccount(vo.getAccount());
        // ----- 校验: 当结果为null时, 说明该用户不存在
        Assert.isTrue(Objects.nonNull(user), "用户名或密码错误");
        // ----- 校验用户合法性: 查看密码是否匹配、用户是否被禁用
        String password = Aes128Util.encryptToStr(vo.getPassword());
        Assert.isTrue(StringUtils.equals(password, user.getPassword()), "用户名或密码错误");
        Assert.isTrue(user.getEnabled(), "用户名或密码错误");
        Assert.isTrue(user.getStatus(), "用户被禁用");
        // ----- 得到token, 保存缓存
        String token = TokenUtil.loginSuccess(user.getId());
        YanwuUserVO result = JsonUtil.convertObject(user, YanwuUserVO.class);
        result.setToken(token);
        loginTokenOperations.set(Contents.LOGIN_TOKEN + user.getId(), result, Contents.TOKEN_TIME_OUT, TimeUnit.SECONDS);
        return Result.success(result);
    }

    @RequestHandler
    @PostMapping(value = "logout/{id}")
    public Result<Boolean> logout(@PathVariable("id") Long id) throws Exception {
        Boolean result;
        YanwuUserVO user = loginTokenOperations.get(Contents.LOGIN_TOKEN + id);
        if (user == null) {
            result = true;
        } else {
            result = loginTokenOperations.getOperations().delete(Contents.LOGIN_TOKEN + id);
        }
        return Result.success(result);
    }

}
