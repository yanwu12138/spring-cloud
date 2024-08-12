package com.yanwu.spring.cloud.common.test.temp;

import com.zaxxer.hikari.util.DriverDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Properties;

/**
 * @author XuBaofeng.
 * @date 2024/8/6 12:05.
 * <p>
 * description:
 */
public class ApTemplate {
    private static final JdbcTemplate AP_TEMPLATE;
    private static final String JDBC_URL = "jdbc:mysql://xxx.xxx.xxx.xxx:3306/ap_server?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8&connectTimeout=6000&socketTimeout=6000";
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    private static final String USERNAME = "xxxxxx";
    private static final String PASSWORD = "xxxxxx";

    static {
        DataSource dataSource = new DriverDataSource(JDBC_URL, DRIVER_CLASS, new Properties(), USERNAME, PASSWORD);
        AP_TEMPLATE = new JdbcTemplate(dataSource);
    }

    private static String buildApSql(String apSn) {
        return " SELECT " +
                "  dbl.beam_id AS beamId, " +
                "  dbl.modem_type AS modemType, " +
                "  dbl.sn AS sn, " +
                "  dbl.lon, " +
                "  dbl.lat, " +
                "  dbl.freq AS freq, " +
                "  dbl.location AS switchInfo, " +
                "  dbl.date_time AS lastTime, " +
                "  dbl.operator AS operator  " +
                " FROM " +
                "  device_beam_log AS dbl " +
                " WHERE " +
                "  dbl.sn = '" + apSn + "' " +
                " ORDER BY " +
                "  change_time DESC  " +
                "  LIMIT 1; ";
    }

    public static DeviceInfo getSwitchInfo(String apSn) {
        List<DeviceInfo> query = AP_TEMPLATE.query(buildApSql(apSn), (rs, rowNum) -> {
            DeviceInfo instance = new DeviceInfo();
            instance.setBeamId(rs.getString("beamId"));
            instance.setIndex("0");
            instance.setModemType(rs.getString("modemType"));
            instance.setDeviceType("ap");
            instance.setSn(apSn);
            instance.setLon(rs.getDouble("lon"));
            instance.setLat(rs.getDouble("lat"));
            instance.setFreq(rs.getDouble("freq"));
            instance.setSwitchInfo(rs.getString("switchInfo"));
            instance.setLastTime(rs.getLong("lastTime"));
            instance.setOperator(rs.getString("operator"));
            return instance;
        });
        return query.stream().findFirst().orElse(null);
    }
}
