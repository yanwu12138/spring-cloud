package com.yanwu.spring.cloud.device.controller;

import com.yanwu.spring.cloud.common.core.annotation.LogAndCheckParam;
import com.yanwu.spring.cloud.common.pojo.ResponseEnvelope;
import com.yanwu.spring.cloud.device.data.model.DeviceLight;
import com.yanwu.spring.cloud.device.service.DeviceLightService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-11-27 9:42.
 * <p>
 * description:
 */
@Slf4j
@RestController
@RequestMapping("/device/light/")
public class DeviceLightController {

    @Resource
    private DeviceLightService lightService;

    @LogAndCheckParam
    @PostMapping("create")
    public ResponseEntity<ResponseEnvelope<Long>> create() {
        DeviceLight light = new DeviceLight();
        long millis = System.currentTimeMillis();
        light.setPoleId(millis).setCreator(millis);
        lightService.save(light);
        return new ResponseEntity<>(new ResponseEnvelope<>(light.getId()), HttpStatus.OK);
    }

}