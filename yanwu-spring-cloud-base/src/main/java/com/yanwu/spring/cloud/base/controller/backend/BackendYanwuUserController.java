package com.yanwu.spring.cloud.base.controller.backend;

import com.yanwu.spring.cloud.base.data.model.YanwuUser;
import com.yanwu.spring.cloud.base.service.YanwuUserService;
import com.yanwu.spring.cloud.common.core.annotation.CheckParam;
import com.yanwu.spring.cloud.common.utils.VoDoUtil;
import com.yanwu.spring.cloud.common.mvc.req.BaseParam;
import com.yanwu.spring.cloud.common.mvc.res.BackVO;
import com.yanwu.spring.cloud.common.mvc.vo.base.YanwuUserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author XuBaofeng.
 * @date 2018-11-12 16:37.
 * <p>
 * description:
 */
@Slf4j
@RestController
@RequestMapping("backend/yanwuUser/")
public class BackendYanwuUserController {

    @Autowired
    private VoDoUtil voDoUtil;

    @Autowired
    private YanwuUserService userService;

    @CheckParam
    @PostMapping(value = "findByUserName")
    public BackVO<YanwuUserVO> findByUserName(@RequestBody BaseParam<String> param) throws Exception {
        YanwuUser yanwuUser = userService.findByUserName(param.getData());
        YanwuUserVO vo = voDoUtil.convertDoToVo(yanwuUser, YanwuUserVO.class);
        return new BackVO<>(vo);
    }

    @CheckParam
    @PostMapping(value = "updatePortrait")
    public BackVO<Void> updatePortrait(@RequestBody BaseParam<YanwuUserVO> param) throws Exception {
        YanwuUser yanwuUser = voDoUtil.convertVoToDo(param.getData(), YanwuUser.class);
        int i = 1 / 0;
        userService.updatePortrait(yanwuUser);
        return new BackVO<>();
    }

}
