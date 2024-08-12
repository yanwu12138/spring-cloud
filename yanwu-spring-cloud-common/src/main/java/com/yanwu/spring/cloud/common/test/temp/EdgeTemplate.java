package com.yanwu.spring.cloud.common.test.temp;

import com.zaxxer.hikari.util.DriverDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Properties;

/**
 * @author XuBaofeng.
 * @date 2024/8/6 12:05.
 * <p>
 * description:
 */
public class EdgeTemplate {
    private static final JdbcTemplate EDGE_TEMPLATE;
    private static final String JDBC_URL = "jdbc:mysql://xxx.xxx.xxx.xxx:3306/centerdb?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false&useAffectedRows=true&serverTimezone=Asia/Shanghai";
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    private static final String USERNAME = "xxxxxx";
    private static final String PASSWORD = "xxxxxx";

    static {
        EDGE_TEMPLATE = new JdbcTemplate(new DriverDataSource(JDBC_URL, DRIVER_CLASS, new Properties(), USERNAME, PASSWORD));
    }

    private static String buildEdgeSql(String edgeSn) {
        return " SELECT " +
                "   tsbr.beam_id beamId, " +
                "   tsbr.modem_type AS modemType, " +
                "   td.box_code AS sn, " +
                "   tsbr.lon, " +
                "   tsbr.lat, " +
                "   tsbr.downlink_frequency_point AS freq, " +
                "   tsbr.location AS switchInfo, " +
                "   tsbr.DATETIME AS lastTime, " +
                "   tsbr.operator AS operator " +
                " FROM " +
                "   t_device AS td " +
                "   LEFT JOIN t_ship_beam_record AS tsbr ON td.ship_id = tsbr.ship_id " +
                "   AND td.type = 1 " +
                " WHERE " +
                "   td.box_code = '" + edgeSn + "' " +
                " ORDER BY " +
                "   tsbr.exchange_time DESC " +
                "   LIMIT 1; ";
    }

    public static DeviceInfo getSwitchInfo(String edgeSn) {
        List<DeviceInfo> query = EDGE_TEMPLATE.query(buildEdgeSql(edgeSn), (rs, rowNum) -> {
            DeviceInfo instance = new DeviceInfo();
            instance.setBeamId(rs.getString("beamId"));
            instance.setIndex("0");
            instance.setModemType(rs.getString("modemType"));
            instance.setDeviceType("edge");
            instance.setSn(edgeSn);
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
