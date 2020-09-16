package com.yanwu.spring.cloud.device.controller;

import com.yanwu.spring.cloud.common.core.annotation.LogParam;
import com.yanwu.spring.cloud.common.pojo.ResponseEnvelope;
import com.yanwu.spring.cloud.device.data.model.IpAddress;
import com.yanwu.spring.cloud.device.service.IpAddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Baofeng Xu
 * @date 2020/9/14 17:18.
 * <p>
 * description:
 */

@Slf4j
@RestController
@RequestMapping("/ip/address/")
public class IpAddressControl {

    @Resource
    private IpAddressService ipAddressService;

    @LogParam
    @PostMapping("create")
    public ResponseEntity<ResponseEnvelope<Boolean>> create() {
        int max = 256;
        log.info("=============== start ===============");
        for (int i1 = 0; i1 < max; i1++) {
            for (int i2 = 0; i2 < max; i2++) {
                for (int i3 = 0; i3 < max; i3++) {
                    for (int i4 = 0; i4 < max; i4++) {
                        String ip = i1 + "." + i2 + "." + i3 + "." + i4;
                        IpAddress ipAddress = new IpAddress();
                        ipAddress.setIpStr(ip);
                        ipAddress.setIpInt(IpUtil.ipv4ToInt(ip));
                        ipAddress.setIpLong(IpUtil.ipv4ToLong(ip));
                        ipAddressService.save(ipAddress);
                    }
                }
            }
        }
        log.info("=============== end ===============");
        return new ResponseEntity<>(new ResponseEnvelope<>(Boolean.TRUE), HttpStatus.OK);
    }

}
