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
import java.nio.charset.StandardCharsets;
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
    private static final String DRIVER_CLASS = "";
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
        List<DeviceInfo> devices = new ArrayList<>();
        shipInfos.forEach(shipInfo -> {
            try {
                if (shipInfo == null || shipInfo.getShipId() == null) {
                    return;
                }
                buildShipName(shipInfo);
                buildDevices(shipInfo, devices);
            } catch (Exception e) {
                log.error("build ship device failed, shipId: {}, shipName: {}", shipInfo.getShipId(), shipInfo.getShipName(), e);
            }
        });
        FileUtil.appendWrite("/Users/xubaofeng/Downloads/奉化船只列表.json", JsonUtil.toString(devices).getBytes(StandardCharsets.UTF_8));
    }

    private static void buildShipName(ShipInfo shipInfo) {
        if (!shipInfo.getShipName().contains("-")) {
            return;
        }
        shipInfo.setShipName(shipInfo.getShipName().substring(0, shipInfo.getShipName().indexOf("-")));
    }

    private static void buildDevices(ShipInfo shipInfo, List<DeviceInfo> devices) {
        String sql = " select " +
                " td.box_code as box_code,  " +
                " td.mac_addr as mac_addr,  " +
                " td.create_time as create_time,  " +
                " td.type as device_type, " +
                " tbd.type_name as brand, " +
                " tbm.type_name as model, " +
                " tbp.type_name as location " +
                " from t_device as td " +
                "   left join t_base_type as tbm on td.device_model = tbm.id " +
                "   left join t_base_type tbp on td.location = tbp.id " +
                "   left join t_base_type as tbd on td.model = tbd.id " +
                " where " +
                "   td.ship_id = " + shipInfo.getShipId() +
                "   and td.type in (1, 2, 6) " +
                "   order by td.id desc; ";
        JDBC_TEMPLATE.query(sql, handler -> {
            int deviceType = handler.getInt("device_type");
            DeviceInfo instance = new DeviceInfo();
            instance.setShipName(shipInfo.getShipName());
            instance.setModel(handler.getString("model"));
            instance.setMac(handler.getString("mac_addr"));
            instance.setTime(handler.getDate("create_time"));
            instance.setPosition(handler.getString("location"));
            instance.setPositionMark(buildPosition(instance.getPosition()));
            switch (deviceType) {
                case 1:
                    instance.setType(1);
                    instance.setBrand("波星通");
                    instance.setSn(handler.getString("box_code"));
                    break;
                case 2:
                    instance.setType(2);
                    instance.setBrand("波星通");
                    instance.setSn(handler.getString("box_code"));
                    break;
                case 6:
                    instance.setType(6);
                    instance.setBrand(handler.getString("brand"));
                    instance.setSn(buildSn(handler.getString("box_code"), instance.getMac()));
                    break;
                default:
                    break;
            }
            devices.add(instance);
        });
    }

    private static String buildSn(String sn, String mac) {
        if (StringUtils.isNotBlank(sn)) {
            return sn;
        }
        return mac.replaceAll(":", "");
    }

    private static String buildPosition(String position) {
        if (StringUtils.isBlank(position)) {
            return "驾驶室";
        }
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
        /*** 船只ID ***/
        private String shipName;
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

        public DeviceInfo setPosition(String position) {
            if (StringUtils.isNotBlank(position)) {
                this.position = position;
            } else {
                this.position = "驾驶室";
            }
            return this;
        }

        public DeviceInfo setModel(String model) {
            if (StringUtils.isNotBlank(model)) {
                this.model = model;
            } else {
                this.model = "球形";
            }
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
    }

}
