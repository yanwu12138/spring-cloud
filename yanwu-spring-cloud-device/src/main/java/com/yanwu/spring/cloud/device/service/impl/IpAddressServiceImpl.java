package com.yanwu.spring.cloud.device.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwu.spring.cloud.device.data.mapper.IpAddressMapper;
import com.yanwu.spring.cloud.device.data.model.IpAddress;
import com.yanwu.spring.cloud.device.service.IpAddressService;
import org.springframework.stereotype.Service;

/**
 * @author Baofeng Xu
 * @date 2020/9/14 17:22.
 * <p>
 * description:
 */
@Service
public class IpAddressServiceImpl extends ServiceImpl<IpAddressMapper, IpAddress> implements IpAddressService {
}
