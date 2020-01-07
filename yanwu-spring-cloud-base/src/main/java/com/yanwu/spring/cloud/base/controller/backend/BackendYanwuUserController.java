package com.yanwu.spring.cloud.base.controller.backend;

import com.yanwu.spring.cloud.base.data.model.YanwuUser;
import com.yanwu.spring.cloud.base.service.YanwuUserService;
import com.yanwu.spring.cloud.common.core.annotation.Log;
import com.yanwu.spring.cloud.common.mvc.req.BaseParam;
import com.yanwu.spring.cloud.common.mvc.res.ResponseEnvelope;
import com.yanwu.spring.cloud.common.mvc.vo.base.YanwuUserVO;
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

    @Log
    @PostMapping(value = "findByUserName")
    public ResponseEntity<ResponseEnvelope<YanwuUserVO>> findByUserName(@RequestBody BaseParam<String> param) throws Exception {
        YanwuUser yanwuUser = userService.findByUserName(param.getData());
        YanwuUserVO vo = voDoUtil.convertDoToVo(yanwuUser, YanwuUserVO.class);
        return new ResponseEntity<>(new ResponseEnvelope<>(vo), HttpStatus.OK);
    }

    @Log
    @PostMapping(value = "updatePortrait")
    public ResponseEntity<ResponseEnvelope<Void>> updatePortrait(@RequestBody BaseParam<YanwuUserVO> param) throws Exception {
        YanwuUser yanwuUser = voDoUtil.convertVoToDo(param.getData(), YanwuUser.class);
        userService.updatePortrait(yanwuUser);
        return new ResponseEntity<>(new ResponseEnvelope<>(), HttpStatus.OK);
    }

}
