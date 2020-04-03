package com.yanwu.spring.cloud.device.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwu.spring.cloud.device.data.mapper.DeviceLightMapper;
import com.yanwu.spring.cloud.device.data.model.DeviceLight;
import com.yanwu.spring.cloud.device.service.DeviceLightService;
import org.springframework.stereotype.Service;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/4/3 15:35.
 * <p>
 * description:
 */
@Service
public class DeviceLightServiceImpl extends ServiceImpl<DeviceLightMapper, DeviceLight> implements DeviceLightService {
}
