package com.yanwu.spring.cloud.base.controller.webapp;

import com.yanwu.spring.cloud.base.cache.YanwuCacheManager;
import com.yanwu.spring.cloud.base.data.model.YanwuUser;
import com.yanwu.spring.cloud.base.service.YanwuUserService;
import com.yanwu.spring.cloud.common.core.annotation.LogAndParam;
import com.yanwu.spring.cloud.common.mvc.res.BackVO;
import com.yanwu.spring.cloud.common.mvc.vo.base.LoginVO;
import com.yanwu.spring.cloud.common.mvc.vo.base.YanwuUserVO;
import com.yanwu.spring.cloud.common.utils.AccessTokenUtil;
import com.yanwu.spring.cloud.common.utils.Aes128Util;
import com.yanwu.spring.cloud.common.utils.BackVOUtil;
import com.yanwu.spring.cloud.common.utils.VoDoUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

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

    @Autowired
    private YanwuCacheManager tokenCache;

    @Autowired
    private YanwuUserService yanwuUserService;

    @LogAndParam
    @PostMapping(value = "login")
    public BackVO<YanwuUserVO> login(@RequestBody LoginVO vo) throws Exception {
        // ----- 校验入参
        Assert.isTrue(StringUtils.isNotBlank(vo.getAccount()), "Account cannot be empty when login.");
        Assert.isTrue(StringUtils.isNotBlank(vo.getPassword()), "Password cannot be empty when login.");
        Assert.isTrue(StringUtils.isNotBlank(vo.getCaptcha()), "Captcha cannot be empty when login.");
        // ----- 根据 用户名 邮箱 手机号 检索用户
        YanwuUser user = yanwuUserService.findByAccount(vo.getAccount());
        // ----- 校验: 当结果为null时, 说明该用户不存在
        Assert.isTrue(Objects.nonNull(user), "user does not exist.");
        // ----- 校验密码: 当密码不匹配是说明密码错误
        String password = Aes128Util.encrypt(vo.getPassword());
        Assert.isTrue(StringUtils.equals(password, user.getPassword()), "password error.");
        YanwuUserVO userVO = voDoUtil.convertDoToVo(user, YanwuUserVO.class);
        // ----- 得到token, 保存缓存
        String token = AccessTokenUtil.loginSuccess(userVO.getId(), userVO.getAccount());
        userVO.setToken(token);
        tokenCache.put(user.getId(), token);
        return BackVOUtil.operateAccess(userVO);
    }

    @LogAndParam
    @PostMapping(value = "logout/{id}")
    public BackVO<Boolean> logout(@PathVariable("id") Long id) throws Exception {
        tokenCache.remove(id);
        return BackVOUtil.operateAccess(Boolean.TRUE);
    }

}
