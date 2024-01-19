package com.yanwu.spring.cloud.base.controller.backend;

import com.yanwu.spring.cloud.base.data.model.YanwuUser;
import com.yanwu.spring.cloud.base.service.YanwuUserService;
import com.yanwu.spring.cloud.common.core.annotation.RequestHandler;
import com.yanwu.spring.cloud.common.core.annotation.UserAccesses;
import com.yanwu.spring.cloud.common.pojo.Result;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("backend/yanwuUser/")
public class BackendYanwuUserController {

    @Resource
    private YanwuUserService userService;

    @RequestHandler(dataScope = @UserAccesses(agent = false))
    @GetMapping(value = "findByUserName")
    public Result<YanwuUser> findByUserName(@RequestParam("name") String name) throws Exception {
        YanwuUser yanwuUser = userService.findByUserName(name);
        return Result.success(yanwuUser);
    }

    @RequestHandler
    @PostMapping(value = "updatePortrait")
    public Result<Void> updatePortrait(@RequestBody YanwuUser yanwuUser) throws Exception {
        userService.updatePortrait(yanwuUser);
        return Result.success();
    }

    @RequestHandler
    @PostMapping("updateAccountById")
    public Result<YanwuUser> updateAccountById(@RequestBody YanwuUser user) {
        user = userService.updateAccountById(user);
        return Result.success(user);
    }

}
