package com.yanwu.spring.cloud.common.test;

import com.yanwu.spring.cloud.common.pojo.RequestInfo;
import com.yanwu.spring.cloud.common.pojo.Result;
import com.yanwu.spring.cloud.common.test.temp.ApTemplate;
import com.yanwu.spring.cloud.common.test.temp.DeviceInfo;
import com.yanwu.spring.cloud.common.test.temp.EdgeTemplate;
import com.yanwu.spring.cloud.common.utils.JsonUtil;
import com.yanwu.spring.cloud.common.utils.RequestUtil;
import com.yanwu.spring.cloud.common.utils.ThreadUtil;
import com.zaxxer.hikari.util.DriverDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author XuBaofeng.
 * @date 2024/3/6 11:29.
 * <p>
 * description:
 */
@Slf4j
@SuppressWarnings("all")
public class JdbcTemplateTest {

    private static final JdbcTemplate JDBC_TEMPLATE;
    private static final String JDBC_URL = "jdbc:mysql://xxx.xxx.xxx.xxx:3306/centerdb?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false&useAffectedRows=true&serverTimezone=Asia/Shanghai";
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    private static final String USERNAME = "xxxxxx";
    private static final String PASSWORD = "xxxxxx";

    static {
        DataSource dataSource = new DriverDataSource(JDBC_URL, DRIVER_CLASS, new Properties(), USERNAME, PASSWORD);
        JDBC_TEMPLATE = new JdbcTemplate(dataSource);
    }

    private static final String PUSH_DEVICE_INFO_URL = "http://xxx.xxx.xxx.xxx:8001/device/setDeviceInfo";

    public static void main(String[] args) throws Exception {
        String shipNames = "浙岱渔03587";
        String[] split = shipNames.split("\n");

        for (int i = 0; i < split.length; i++) {
            String shipName = split[i];
            String deviceSn = selectDeviceSn(shipName);

            try {
                if (StringUtils.isBlank(deviceSn)) {
                    continue;
                }
                DeviceInfo deviceInfo;
                if (deviceSn.startsWith("A") || deviceSn.startsWith("B") || deviceSn.startsWith("C") || deviceSn.startsWith("D")) {
                    // ----- AP
                    deviceInfo = ApTemplate.getSwitchInfo(deviceSn);
                } else {
                    // ----- 主机盒
                    deviceInfo = EdgeTemplate.getSwitchInfo(deviceSn);
                }
                if (deviceInfo == null) {
                    continue;
                }
                if (StringUtils.isBlank(deviceInfo.getSwitchInfo())) {
                    deviceInfo.setSwitchInfo("(0-0)-->(" + deviceInfo.getBeamId() + "-" + deviceInfo.getIndex() + ")");
                }
                if (StringUtils.isBlank(deviceInfo.getOperator())) {
                    deviceInfo.setOperator("boot");
                }
                if (deviceInfo.getLastTime() <= 0) {
                    deviceInfo.setLastTime(System.currentTimeMillis());
                }
                RequestInfo<DeviceInfo, Void> requestParam = RequestInfo.getInstance(HttpMethod.POST, PUSH_DEVICE_INFO_URL, Void.class);
                Result<Void> execute = RequestUtil.execute(requestParam.buildBody(deviceInfo));
                log.info("shipName: {}, deviceSn: {}, deviceInfo: {}, result: {}", shipName, deviceSn, JsonUtil.toString(deviceInfo), JsonUtil.toString(execute));
                ThreadUtil.sleep(3000L);
            } catch (Exception e) {
                log.error("error, ship", shipName, e);
                continue;
            }
        }
    }

    private static String selectDeviceSn(String shipName) {
        String sql = " SELECT d.box_code " +
                " FROM t_ship s " +
                "  LEFT JOIN t_device d ON s.id = d.ship_id  AND d.type IN ( 1, 12, 13 ) AND ( d.type_extra IS NULL OR d.type_extra != 'AP' ) " +
                " WHERE ship_name = '" + shipName + "'; ";
        AtomicReference<String> deviceSnRef = new AtomicReference<>("");
        JDBC_TEMPLATE.query(sql, handler -> {
            deviceSnRef.set(handler.getString("box_code"));
        });
        if (StringUtils.isBlank(deviceSnRef.get())) {
            log.info("selectDeviceSn failed. shipName = {}", shipName);
            return null;
        }
        return deviceSnRef.get();
    }

}
