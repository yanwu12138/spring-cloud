package com.yanwu.spring.cloud.netty.protocol.service;

import com.yanwu.spring.cloud.netty.model.AlarmLampReqBO;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-11-21 13:02.
 * <p>
 * description:
 */
@Component
public class AlarmLampService {

    private static final String ALARM_LAMP_ONLINE = "alarm_lamp_online";
    private static final String ALARM_LAMP_INFO = "alarm_lamp_info";

    @SuppressWarnings("all")
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, String> alarmLampOperations;
    
    @SuppressWarnings("all")
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, AlarmLampReqBO> alarmLampInfoOperations;

    public void saveAlarmLamp(String ctxId, AlarmLampReqBO lampReq) {
        alarmLampOperations.put(ALARM_LAMP_ONLINE, lampReq.getSn(), ctxId);
        alarmLampInfoOperations.put(ALARM_LAMP_INFO, lampReq.getSn(), lampReq);
    }

    public void devAlarmLamp(String sn) {
        alarmLampOperations.delete(ALARM_LAMP_ONLINE, sn);
        alarmLampInfoOperations.delete(ALARM_LAMP_INFO, sn);
    }

    public String getCtxId(String sn) {
        return alarmLampOperations.get(ALARM_LAMP_ONLINE, sn);
    }

    public AlarmLampReqBO getInfo(String sn) {
        return alarmLampInfoOperations.get(ALARM_LAMP_INFO, sn);
    }

}
