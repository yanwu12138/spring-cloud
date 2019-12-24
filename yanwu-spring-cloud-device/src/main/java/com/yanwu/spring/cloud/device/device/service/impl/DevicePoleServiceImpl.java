package com.yanwu.spring.cloud.device.device.service.impl;

import com.yanwu.spring.cloud.device.device.data.DeviceManager;
import com.yanwu.spring.cloud.device.device.data.model.DevicePole;
import com.yanwu.spring.cloud.device.device.service.DevicePoleService;
import com.yanwu.spring.cloud.device.device.vo.SearchDeviceVO;

import java.util.List;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-12-24 11:10.
 * <p>
 * description:
 */
public class DevicePoleServiceImpl implements DevicePoleService {

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
    public DevicePole getById(Long id) throws Exception {
        return null;
    }

    @Override
    public List<DevicePole> listByIds(List<Long> ids) throws Exception {
        return null;
    }

    @Override
    public List<DevicePole> listByPageSearch(SearchDeviceVO search) throws Exception {
        return null;
    }
}
