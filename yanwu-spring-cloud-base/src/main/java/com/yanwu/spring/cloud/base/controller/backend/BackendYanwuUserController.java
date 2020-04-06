package com.yanwu.spring.cloud.base.controller.backend;

import com.yanwu.spring.cloud.base.data.model.YanwuUser;
import com.yanwu.spring.cloud.base.service.YanwuUserService;
import com.yanwu.spring.cloud.common.core.annotation.LogAndCheckParam;
import com.yanwu.spring.cloud.common.pojo.ResponseEnvelope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    private YanwuUserService userService;

    @LogAndCheckParam
    @GetMapping(value = "findByUserName")
    public ResponseEntity<ResponseEnvelope<YanwuUser>> findByUserName(@RequestParam("name") String name) throws Exception {
        YanwuUser yanwuUser = userService.findByUserName(name);
        return new ResponseEntity<>(new ResponseEnvelope<>(yanwuUser), HttpStatus.OK);
    }

    @LogAndCheckParam
    @PostMapping(value = "updatePortrait")
    public ResponseEntity<ResponseEnvelope<Void>> updatePortrait(@RequestBody YanwuUser yanwuUser) throws Exception {
        userService.updatePortrait(yanwuUser);
        return new ResponseEntity<>(new ResponseEnvelope<>(), HttpStatus.OK);
    }

    @LogAndCheckParam
    @PostMapping("updateAccountById")
    public ResponseEntity<ResponseEnvelope<YanwuUser>> updateAccountById(@RequestBody YanwuUser user) {
        user = userService.updateAccountById(user);
        return new ResponseEntity<>(new ResponseEnvelope<>(user), HttpStatus.OK);
    }

}
