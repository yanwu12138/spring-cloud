package com.yanwu.spring.cloud.base.controller.webapp;

import com.yanwu.spring.cloud.base.common.YanwuConstants;
import com.yanwu.spring.cloud.base.data.model.YanwuUser;
import com.yanwu.spring.cloud.base.service.YanwuUserService;
import com.yanwu.spring.cloud.common.core.annotation.LogAndCheckParam;
import com.yanwu.spring.cloud.common.mvc.res.ResponseEnvelope;
import com.yanwu.spring.cloud.common.mvc.vo.base.YanwuUserVO;
import com.yanwu.spring.cloud.common.utils.Aes128Util;
import com.yanwu.spring.cloud.common.utils.VoDoUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
@RequestMapping("webapp/yanwuUser/")
public class WebappYanwuUserController {

    @Autowired
    private VoDoUtil voDoUtil;

    @Autowired
    private YanwuUserService userService;

    @LogAndCheckParam
    @PostMapping(value = "create")
    public ResponseEntity<ResponseEnvelope<YanwuUserVO>> create(@RequestBody YanwuUserVO yanwuUserVO) throws Exception {
        YanwuUser userDO = voDoUtil.convertVoToDo(yanwuUserVO, YanwuUser.class);
        if (StringUtils.isBlank(userDO.getPassword())) {
            userDO.setPassword(Aes128Util.encrypt(YanwuConstants.DEFAULT_PASSWORD));
        } else {
            userDO.setPassword(Aes128Util.encrypt(userDO.getPassword()));
        }
        YanwuUser yanwuUser = userService.save(userDO);
        YanwuUserVO vo = voDoUtil.convertDoToVo(yanwuUser, YanwuUserVO.class);
        return new ResponseEntity<>(new ResponseEnvelope<>(vo), HttpStatus.OK);
    }

    @LogAndCheckParam
    @PostMapping(value = "update")
    public ResponseEntity<ResponseEnvelope<Boolean>> update(@RequestBody YanwuUserVO yanwuUserVO) throws Exception {
        return new ResponseEntity<>(new ResponseEnvelope<>(Boolean.TRUE), HttpStatus.OK);
    }

}
