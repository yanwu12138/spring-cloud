package com.yanwu.spring.cloud.device.controller;

import com.yanwu.spring.cloud.common.core.annotation.LogParam;
import com.yanwu.spring.cloud.device.data.model.DeviceLight;
import com.yanwu.spring.cloud.device.service.DeviceLightService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("device/transactions/")
public class TransactionsController {

    @Resource
    private DeviceLightService lightService;

    @LogParam
    @GetMapping(value = "test1")
    public void test1() {
        lightService.save(new DeviceLight().setPoleId(1L));
    }

}