package com.yanwu.spring.cloud.device.device.service.impl;

import com.yanwu.spring.cloud.device.device.data.DeviceManager;
import com.yanwu.spring.cloud.device.device.data.model.DeviceLight;
import com.yanwu.spring.cloud.device.device.service.DeviceLightService;
import com.yanwu.spring.cloud.device.device.vo.SearchDeviceVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-12-24 11:08.
 * <p>
 * description:
 */
@Service
public class DeviceLightServiceImpl implements DeviceLightService {

    @Override
    public Long create(DeviceManager device) throws Exception {
        return null;
    }

    @Override
    public Long update(DeviceManager device) throws Exception {
        return null;
    }

    @Override
    public Boolean remove(Long id) throws Exception {
        return null;
    }

    @Override
    public Boolean removeByIds(List<Long> ids) throws Exception {
        return null;
    }

    @Override
    public DeviceLight getById(Long id) throws Exception {
        return null;
    }

    @Override
    public List<DeviceLight> listByIds(List<Long> ids) throws Exception {
        return null;
    }

    @Override
    public List<DeviceLight> listByPageSearch(SearchDeviceVO search) throws Exception {
        return null;
    }

}
