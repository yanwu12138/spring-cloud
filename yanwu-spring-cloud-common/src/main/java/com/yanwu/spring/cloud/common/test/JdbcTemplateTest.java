package com.yanwu.spring.cloud.common.test;

import com.yanwu.spring.cloud.common.utils.DateUtil;
import com.yanwu.spring.cloud.common.utils.FileUtil;
import com.yanwu.spring.cloud.common.utils.JsonUtil;
import com.zaxxer.hikari.util.DriverDataSource;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static org.apache.poi.util.StringUtil.UTF8;

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
    private static final String JDBC_URL = "";
    private static final String DRIVER_CLASS = "com.mysql.jdbc.Driver";
    private static final String USERNAME = "";
    private static final String PASSWORD = "";

    static {
        DataSource dataSource = new DriverDataSource(JDBC_URL, DRIVER_CLASS, new Properties(), USERNAME, PASSWORD);
        JDBC_TEMPLATE = new JdbcTemplate(dataSource);
    }

    public static void main(String[] args) throws Exception {
        String filepath = "/Users/xubaofeng/Downloads/奉化船只_1709693784040.json";
        byte[] read = FileUtil.read(filepath);
        if (read.length == 0) {
            log.error("read file failed. filepath: {}", filepath);
            return;
        }
        List<ShipInfo> shipInfos = JsonUtil.toObjectList(new String(read, UTF8), ShipInfo.class);
        if (CollectionUtils.isEmpty(shipInfos)) {
            log.error("file to shipInfos failed. filepath: {}", filepath);
            return;
        }
        for (ShipInfo shipInfo : shipInfos) {
            try {
                if (shipInfo == null || shipInfo.getShipId() == null) {
                    continue;
                }
                if (CollectionUtils.isEmpty(shipInfo.getDevices())) {
                    shipInfo.setDevices(new ArrayList<>());
                }
                buildEdge(shipInfo);
                buildAntenna(shipInfo);
                buildCamera(shipInfo);
                buildShipName(shipInfo);
            } catch (Exception e) {
                log.error("build ship device failed, shipId: {}, shipName: {}", shipInfo.getShipId(), shipInfo.getShipName(), e);
            }
        }
    }

    private static void buildShipName(ShipInfo shipInfo) {
        if (!shipInfo.getShipName().contains("-")) {
            return;
        }
        shipInfo.setShipName(shipInfo.getShipName().substring(0, shipInfo.getShipName().indexOf("-")));
    }

    private static void buildEdge(ShipInfo shipInfo) {
        String sql = " select " +
                " td.box_code as box_code, " +
                " td.mac_addr as mac_addr, " +
                " td.create_time as create_time, " +
                " tbm.type_name as model, " +
                " tbp.type_name as location " +
                " from t_device as td " +
                "   left join t_base_type as tbm on td.device_model = tbm.id " +
                "   left join t_base_type tbp on td.location  = tbp.id " +
                " where " +
                "   td.ship_id = " + shipInfo.getShipId() +
                "   and td.type = 1 " +
                "   order by td.id desc " +
                "   limit 1;";
        JDBC_TEMPLATE.query(sql, handler -> {
            DeviceInfo instance = new DeviceInfo();
            instance.setType(2);
            instance.setBrand("波星通");
            instance.setModel(handler.getString("model"));
            instance.setSn(handler.getString("box_code"));
            instance.setMac(handler.getString("mac_addr"));
            instance.setTime(handler.getDate("create_time"));
            instance.setPosition(handler.getString("location"));
            instance.setPositionMark(buildPosition(instance.getPosition()));
            shipInfo.getDevices().add(instance);
        });
    }

    private static void buildCamera(ShipInfo shipInfo) {
        String sql = " select " +
                " td.box_code as box_code,  " +
                " td.mac_addr as mac_addr,  " +
                " td.create_time as create_time,  " +
                " tbd.type_name as brand, " +
                " tbm.type_name as model, " +
                " tbp.type_name as location " +
                " from t_device as td " +
                "   left join t_base_type as tbm on td.device_model = tbm.id " +
                "   left join t_base_type tbp on td.location = tbp.id " +
                "   left join t_base_type as tbd on td.model = tbd.id " +
                " where " +
                "   td.ship_id = " + shipInfo.getShipId() +
                "   and td.type = 6 " +
                "   order by td.id desc; ";
        JDBC_TEMPLATE.query(sql, handler -> {
            DeviceInfo instance = new DeviceInfo();
            instance.setType(6);
            instance.setBrand(handler.getString("brand"));
            instance.setModel(handler.getString("model"));
            instance.setMac(handler.getString("mac_addr"));
            instance.setSn(buildSn(handler.getString("box_code"), instance.getMac()));
            instance.setTime(handler.getDate("create_time"));
            instance.setPosition(handler.getString("location"));
            instance.setPositionMark(buildPosition(instance.getPosition()));
            shipInfo.getDevices().add(instance);
        });
    }

    private static void buildAntenna(ShipInfo shipInfo) {
        String sql = " select " +
                " td.box_code as box_code, " +
                " td.mac_addr as mac_addr, " +
                " td.create_time as create_time, " +
                " tbm.type_name as model, " +
                " tbp.type_name as location " +
                " from t_device as td " +
                "   left join t_base_type as tbm on td.device_model = tbm.id " +
                "   left join t_base_type tbp on td.location  = tbp.id " +
                " where " +
                "   td.ship_id = " + shipInfo.getShipId() +
                "   and td.type = 2 " +
                "   order by td.id desc " +
                "   limit 1;";
        JDBC_TEMPLATE.query(sql, handler -> {
            DeviceInfo instance = new DeviceInfo();
            instance.setType(1);
            instance.setBrand("波星通");
            instance.setModel(handler.getString("model"));
            instance.setSn(handler.getString("box_code"));
            instance.setMac(handler.getString("mac_addr"));
            instance.setTime(handler.getDate("create_time"));
            instance.setPosition(handler.getString("location"));
            instance.setPositionMark(buildPosition(instance.getPosition()));
            shipInfo.getDevices().add(instance);
        });
    }

    private static String buildSn(String sn, String mac) {
        if (StringUtils.isNotBlank(sn)) {
            return sn;
        }
        return mac.replaceAll(":", "");
    }

    private static String buildPosition(String position) {
        switch (position) {
            case "前甲板":
            case "右前方-2":
            case "左前方-1":
                return "船头";
            case "右后方-3":
            case "左后方-4":
            case "后甲板":
            case "右侧":
            case "右舷":
            case "左侧":
            case "左舷":
                return "船尾";
            case "车头":
            case "车尾":
            case "机舱":
                return "发动机舱";
            case "墙面":
            case "天花板":
            case "机柜中":
            case "桌面":
            case "船长室":
            case "船长室墙面":
            case "船长室桌面":
            case "驾驶台":
            case "驾驶台桌面":
            case "驾驶室":
            case "驾驶舱":
            default:
                return "驾驶室";
        }
    }

    @Data
    @Accessors(chain = true)
    public static class DeviceInfo implements Serializable {
        private static final long serialVersionUID = -2173510009765550861L;
        /*** 设备类型：【1: 主机盒; 2: 天线; 6: 摄像头】 ***/
        private Integer type;
        /*** 设备SN序列号 ***/
        private String sn;
        /*** 设备MAC地址 ***/
        private String mac;
        /*** 安装时间 ***/
        private String time;
        /*** 型号 ***/
        private String model;
        /*** 品牌 ***/
        private String brand;
        /*** 安装位置 ***/
        private String position;
        /*** 安装位置标记 ***/
        private String positionMark;

        public DeviceInfo setTime(Date date) {
            this.time = DateUtil.toTimeStr(date, DateUtil.DateFormat.YYYY_MM_DD_HH_MM_SS);
            return this;
        }
    }

    @Data
    @Accessors(chain = true)
    public static class ShipInfo implements Serializable {
        private static final long serialVersionUID = 1832293969718658077L;
        /*** 船只名称 ***/
        private Integer shipId;
        /*** 船只ID ***/
        private String shipName;
        /*** 船只设备列表 ***/
        private List<DeviceInfo> devices;
    }

}
