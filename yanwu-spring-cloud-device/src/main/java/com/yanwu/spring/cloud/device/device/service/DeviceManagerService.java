package com.yanwu.spring.cloud.device.device.service;

import com.yanwu.spring.cloud.device.device.data.DeviceManager;
import com.yanwu.spring.cloud.device.device.vo.SearchDeviceVO;

import java.util.List;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-11-27 9:41.
 * <p>
 * description:
 */
public interface DeviceManagerService {

    /**
     * 创建设备
     *
     * @param device
     * @return
     * @throws Exception
     */
    Long create(DeviceManager device) throws Exception;

    /**
     * 更新设备
     *
     * @param device
     * @return
     * @throws Exception
     */
    Long update(DeviceManager device) throws Exception;

    /**
     * 根据ID删除设备
     *
     * @param id
     * @return
     * @throws Exception
     */
    Boolean remove(Long id) throws Exception;

    /**
     * 根据ID集合删除设备
     *
     * @param ids
     * @return
     * @throws Exception
     */
    Boolean removeByIds(List<Long> ids) throws Exception;

    /**
     * 根据ID获取设备
     *
     * @param id
     * @return
     * @throws Exception
     */
    DeviceManager getById(Long id) throws Exception;

    /**
     * 根据ID集合获取设备集合
     *
     * @param ids
     * @return
     * @throws Exception
     */
    List<? extends DeviceManager> listByIds(List<Long> ids) throws Exception;

    /**
     * 根据名称和编号模糊搜索设备集合
     *
     * @param search
     * @return
     * @throws Exception
     */
    List<? extends DeviceManager> listByPageSearch(SearchDeviceVO search) throws Exception;
}
