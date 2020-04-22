package com.yanwu.spring.cloud.base.controller.backend;

import com.yanwu.spring.cloud.base.data.model.YanwuUser;
import com.yanwu.spring.cloud.base.service.YanwuUserService;
import com.yanwu.spring.cloud.common.core.annotation.LogParam;
import com.yanwu.spring.cloud.common.pojo.ResponseEnvelope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @LogParam
    @GetMapping(value = "findByUserName")
    public ResponseEntity<ResponseEnvelope<YanwuUser>> findByUserName(@RequestParam("name") String name) throws Exception {
        YanwuUser yanwuUser = userService.findByUserName(name);
        return new ResponseEntity<>(new ResponseEnvelope<>(yanwuUser), HttpStatus.OK);
    }

    @LogParam
    @PostMapping(value = "updatePortrait")
    public ResponseEntity<ResponseEnvelope<Void>> updatePortrait(@RequestBody YanwuUser yanwuUser) throws Exception {
        userService.updatePortrait(yanwuUser);
        return new ResponseEntity<>(new ResponseEnvelope<>(), HttpStatus.OK);
    }

    @LogParam
    @PostMapping("updateAccountById")
    public ResponseEntity<ResponseEnvelope<YanwuUser>> updateAccountById(@RequestBody YanwuUser user) {
        user = userService.updateAccountById(user);
        return new ResponseEntity<>(new ResponseEnvelope<>(user), HttpStatus.OK);
    }

}
