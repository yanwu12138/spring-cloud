package com.yanwu.spring.cloud.base.controller.webapp;

import com.yanwu.spring.cloud.base.common.YanwuConstants;
import com.yanwu.spring.cloud.base.data.model.YanwuUser;
import com.yanwu.spring.cloud.base.service.YanwuUserService;
import com.yanwu.spring.cloud.common.core.annotation.LogAndCheckParam;
import com.yanwu.spring.cloud.common.pojo.ResponseEnvelope;
import com.yanwu.spring.cloud.common.utils.Aes128Util;
import com.yanwu.spring.cloud.common.utils.VoDoUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
@RequestMapping("webapp/yanwuUser/")
public class WebappYanwuUserController {

    @Autowired
    private VoDoUtil voDoUtil;

    @Autowired
    private YanwuUserService userService;

    @LogAndCheckParam
    @PostMapping(value = "create")
    public ResponseEntity<ResponseEnvelope<Long>> create(@RequestBody YanwuUser user) throws Exception {
        if (StringUtils.isBlank(user.getPassword())) {
            user.setPassword(Aes128Util.encrypt(YanwuConstants.DEFAULT_PASSWORD));
        } else {
            user.setPassword(Aes128Util.encrypt(user.getPassword()));
        }
        userService.save(user);
        return new ResponseEntity<>(new ResponseEnvelope<>(user.getId()), HttpStatus.OK);
    }

    @LogAndCheckParam
    @PutMapping(value = "update")
    public ResponseEntity<ResponseEnvelope<Boolean>> update(@RequestBody YanwuUser user) throws Exception {
        return new ResponseEntity<>(new ResponseEnvelope<>(Boolean.TRUE), HttpStatus.OK);
    }

}
