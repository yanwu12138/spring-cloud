package com.yanwu.spring.cloud.base.controller.backend;

import com.yanwu.spring.cloud.base.data.model.YanwuUser;
import com.yanwu.spring.cloud.base.service.YanwuUserService;
import com.yanwu.spring.cloud.common.core.annotation.LogAndCheckParam;
import com.yanwu.spring.cloud.common.pojo.BaseParam;
import com.yanwu.spring.cloud.common.pojo.ResponseEnvelope;
import com.yanwu.spring.cloud.common.utils.VoDoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @LogAndCheckParam
    @PostMapping(value = "findByUserName")
    public ResponseEntity<ResponseEnvelope<YanwuUser>> findByUserName(@RequestBody BaseParam<String> param) throws Exception {
        YanwuUser yanwuUser = userService.findByUserName(param.getData());
        return new ResponseEntity<>(new ResponseEnvelope<>(yanwuUser), HttpStatus.OK);
    }

    @LogAndCheckParam
    @PostMapping(value = "updatePortrait")
    public ResponseEntity<ResponseEnvelope<Void>> updatePortrait(@RequestBody BaseParam<YanwuUser> param) throws Exception {
        YanwuUser yanwuUser = voDoUtil.convertVoToDo(param.getData(), YanwuUser.class);
        userService.updatePortrait(yanwuUser);
        return new ResponseEntity<>(new ResponseEnvelope<>(), HttpStatus.OK);
    }

}
