package com.yanwu.spring.cloud.common.utils;

import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.yanwu.spring.cloud.common.pojo.RequestInfo;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpMethod;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import static com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant.*;

/**
 * @author XuBaofeng.
 * @date 2023/9/5 17:02.
 * <p>
 * description:
 */
@Slf4j
public class BxtApiTestUtil {


    public static void main(String[] args) {
        String appId = "73039084026629282443";
        String accessToken = "Y6gD6ziZQz63MoFlnapRO6RyfezALSRht1cU1ffCMypoUCB2BpBs2IwyVa32u8dROeVhEEv8RO93AH1XCwk6NXxubyx2VxLYL8hGYsnUtXmCF2CqfI2bNbv1ZXVaUv3I";
//        testLogin(appId, System.currentTimeMillis());
//        testGetBalance(appId, System.currentTimeMillis(), accessToken);
        testAntennaStatus();
    }

    private static void testAntennaStatus() {
        TAntennaStatus status = new TAntennaStatus();
        status.setGpsSpeedKnots(10.0D).setGpsHeadingDegs(12.312D);
        String url = "http://192.168.18.254:8001/api/amu/antennaStatus";
        RequestInfo<Object> instance = RequestInfo.getInstance(HttpMethod.POST, url, Object.class);
        instance.buildHeaders("Content-Type", "application/json");

        // ----- 正常情况，信号量大与11
        for (int i = 0; i < 120; i++) {
            instance.buildBody(status.setSatLongitudeDegs(134.1D).setGpsLongitudeDegs(122.001D).setGpsLatitudeDegs(31.002D).setDemodConDbAvg(15.3235D));
            log.info("request: {}, response: {}", instance, RestUtil.execute(instance));
            ThreadUtil.sleep(5_000L);
        }

        // ----- 信号量6.5～11
        for (int i = 0; i < 750; i++) {
            instance.buildBody(status.setSatLongitudeDegs(134.1D).setGpsLongitudeDegs(122.001D).setGpsLatitudeDegs(31.002D).setDemodConDbAvg(9.3235D));
            log.info("request: {}, response: {}", instance, RestUtil.execute(instance));
            ThreadUtil.sleep(5_000L);
        }

        // ----- 信号量<6.5
        for (int i = 0; i < 360; i++) {
            instance.buildBody(status.setSatLongitudeDegs(134.1D).setGpsLongitudeDegs(120.001D).setGpsLatitudeDegs(30.002D).setDemodConDbAvg(4.3235D));
            log.info("request: {}, response: {}", instance, RestUtil.execute(instance));
            ThreadUtil.sleep(5_000L);
        }
    }

    @Data
    @Accessors(chain = true)
    public static class TAntennaStatus {
        @Column(name = "id", type = INT, isNull = false, comment = "主键 ID", isKey = true, isAutoIncrement = true)
        private Integer id;
        @Column(name = "format_version", type = INT, comment = "协议版本")
        private Integer formatVersion;
        @Column(name = "utc", type = BIGINT, length = 13, comment = "上报时间戳")
        private Long utc;
        @Column(name = "antenna_serial", type = VARCHAR, length = 32, comment = "天线序列号")
        private String antennaSerial;
        @Column(name = "gps_locked", type = TINYINT, length = 1, comment = "gps锁定状态")
        private Integer gpsLocked;
        @Column(name = "mute_state", type = TINYINT, length = 1, comment = "mute的状态")
        private Integer muteState;
        @Column(name = "gps_longitude_degs", type = DOUBLE, length = 20, decimalLength = 5, isNull = false, comment = "经度")
        private Double gpsLongitudeDegs;
        @Column(name = "gps_latitude_degs", type = DOUBLE, length = 20, decimalLength = 5, isNull = false, comment = "纬度")
        private Double gpsLatitudeDegs;
        @Column(name = "gps_speed_knots", type = DOUBLE, length = 20, decimalLength = 5, isNull = false, comment = "速度")
        private Double gpsSpeedKnots;
        @Column(name = "gps_heading_degs", type = DOUBLE, length = 20, decimalLength = 5, isNull = false, comment = "航向")
        private Double gpsHeadingDegs;
        @Column(name = "gps_utc_s", type = BIGINT, length = 20, comment = "gps时间")
        private Long gpsUtcS;
        @Column(name = "fault_list_json", type = TEXT, comment = "天线故障表")
        private String faultListJson;
        @Column(name = "mf_active_carrier_id", type = INT, comment = "手动载波的id")
        private Integer mfActiveCarrierId;
        @Column(name = "rx_polarisation", type = VARCHAR, length = 32, comment = "接收的极化方式")
        private String rxPolarisation;
        @Column(name = "rx_rf_freq_khz", type = DOUBLE, length = 20, decimalLength = 5, comment = "接收载波频率")
        private Double rxRfFreqKhz;
        @Column(name = "rx_lo_freq_khz", type = DOUBLE, length = 20, decimalLength = 5, comment = "lnb本振频率")
        private Double rxLoFreqKhz;
        @Column(name = "sat_longitude_degs", type = DOUBLE, length = 20, decimalLength = 5, comment = "卫星的经度角度")
        private Double satLongitudeDegs;
        @Column(name = "rx_symbol_rate_ksps", type = DOUBLE, length = 20, decimalLength = 5, comment = "接收的符号率")
        private Double rxSymbolRateKsps;
        @Column(name = "openamip_state_id", type = INT, comment = "openamip的状态id")
        private Integer openamipStateId;
        @Column(name = "demod_con_db_avg", type = DOUBLE, length = 10, decimalLength = 5, comment = "信号值")
        private Double demodConDbAvg;
        @Column(name = "tracking_stable", type = TINYINT, length = 1, comment = "锁星稳定")
        private Integer trackingStable;
        @Column(name = "temperature_degc", type = DOUBLE, length = 20, decimalLength = 5, comment = "环境温度")
        private Double temperatureDegc;
        @Column(name = "pr_version", type = VARCHAR, length = 32, comment = "当前pr的版本")
        private String prVersion;
        @Column(name = "acu_state", type = VARCHAR, length = 64, comment = "acu的状态")
        private String acuState;
        @Column(name = "acu_substate", type = VARCHAR, length = 64, comment = "acu的子状态")
        private String acuSubstate;
        @Column(name = "ant_state", type = VARCHAR, length = 64, comment = "ant的状态")
        private String antState;
        @Column(name = "ant_substate", type = VARCHAR, length = 64, comment = "ant的子状态")
        private String antSubstate;
        @Column(name = "az_nominal_degs", type = DOUBLE, length = 20, decimalLength = 5, comment = "理论方位角度数")
        private Double azNominalDegs;
        @Column(name = "el_nominal_degs", type = DOUBLE, length = 20, decimalLength = 5, comment = "理论俯仰角度数")
        private Double elNominalDegs;
        @Column(name = "sk_nominal_degs", type = DOUBLE, length = 20, decimalLength = 5, comment = "理论极化角度数")
        private Double skNominalDegs;
        @Column(name = "az_position_degs", type = DOUBLE, length = 20, decimalLength = 5, comment = "方位角度数")
        private Double azPositionDegs;
        @Column(name = "el_position_degs", type = DOUBLE, length = 20, decimalLength = 5, comment = "俯仰角度数")
        private Double elPositionDegs;
        @Column(name = "rl_position_degs", type = DOUBLE, length = 20, decimalLength = 5, comment = "横滚角度数")
        private Double rlPositionDegs;
        @Column(name = "sk_position_degs", type = DOUBLE, length = 20, decimalLength = 5, comment = "极化角度数")
        private Double skPositionDegs;
        @Column(name = "openamip_ip", type = VARCHAR, length = 64, comment = "天线openamip ip地址")
        private String openamipIp;
        @Column(name = "openamip_mask", type = VARCHAR, length = 64, comment = "天线openamip子网掩码地址")
        private String openamipMask;
        @Column(name = "antenna_model", type = VARCHAR, length = 64, comment = "天线型号")
        private String antennaModel;
        @Column(name = "openamip_rx_rolloff", type = DOUBLE, length = 20, decimalLength = 5, comment = "滚降系数")
        private Double openamipRxRolloff;
        @Column(name = "amu_dc_v", type = DOUBLE, length = 20, decimalLength = 5, comment = "AMU电压")
        private Double amuDcV;
        @Column(name = "amu_dc_a", type = DOUBLE, length = 20, decimalLength = 5, comment = "AMU电流")
        private Double amuDcA;
        @Column(name = "lnb_dc_v", type = DOUBLE, length = 20, decimalLength = 5, comment = "LNB电压")
        private Double lnbDcV;
        @Column(name = "lnb_dc_a", type = DOUBLE, length = 20, decimalLength = 5, comment = "LNB电流")
        private Double lnbDcA;
        @Column(name = "buc_dc_v", type = DOUBLE, length = 20, decimalLength = 5, comment = "BUC电压")
        private Double bucDcV;
        @Column(name = "buc_dc_a", type = DOUBLE, length = 20, decimalLength = 5, comment = "BUC电流")
        private Double bucDcA;
        @Column(name = "event_list_json", type = TEXT, comment = "天线事件表")
        private String eventListJson;
    }

    private static void testLogin(String appId, long timestamp) {
        String url = "http://bxtapi.boxingtong.net:6241/bxt-api/user/login";
        HashMap<String, String> param = new HashMap<>();
        param.put("phone", "13121909171");
        param.put("password", "123456");
        RequestInfo<Object> instance = RequestInfo.getInstance(HttpMethod.POST, url, Object.class);
        instance.buildHeaders("Content-Type", "application/json")
                .buildHeaders("AppId", appId)
                .buildHeaders("Timestamp", String.valueOf(timestamp))
                .buildHeaders("BXT_Token", getBxtToken(appId, timestamp))
                .buildBody(param);
        log.info("test rest, url: {}, param: {}, result: {}", url, param, RestUtil.execute(instance));
    }

    private static void testGetBalance(String appId, long timestamp, String accessToken) {
        String url = "http://bxtapi.boxingtong.net:6241/bxt-api/user/getBalance";
        RequestInfo<Object> instance = RequestInfo.getInstance(url, Object.class);
        instance.buildHeaders("Content-Type", "application/json")
                .buildHeaders("AppId", appId)
                .buildHeaders("Timestamp", String.valueOf(timestamp))
                .buildHeaders("BXT_Token", getBxtToken(appId, timestamp))
                .buildHeaders("AccessToken", accessToken)
                .buildParams("userId", "C10000336");
        log.info("test rest, url: {}, param: {}, result: {}", url, "C10000336", RestUtil.execute(instance));
    }

    private static void testBobyPay(String appId, long timestamp, String accessToken) {
        String url = "http://bxtapi.boxingtong.net:6241/bxt-api/user/bobyPay";
        HashMap<String, String> param = new HashMap<>();
        param.put("userId", "C10000336");
        param.put("boby", "6");
        param.put("orderNum", "111111111114");
        param.put("describe", "订单备注信息");
        RequestInfo<Object> instance = RequestInfo.getInstance(HttpMethod.POST, url, Object.class);
        instance.buildHeaders("Content-Type", "application/json")
                .buildHeaders("AppId", appId)
                .buildHeaders("Timestamp", String.valueOf(timestamp))
                .buildHeaders("BXT_Token", getBxtToken(appId, timestamp))
                .buildHeaders("AccessToken", accessToken)
                .buildBody(param);
        log.info("test rest, url: {}, param: {}, result: {}", url, "C10000336", RestUtil.execute(instance));
    }

    private static String getBxtToken(String appId, long timestamp) {
        String secret = "B05DF532E466FE1C783213AFA8F6DAB9";
        String md5Str = DigestUtils.md5Hex((appId + secret).getBytes(StandardCharsets.UTF_8));
        md5Str = DigestUtils.md5Hex((md5Str + timestamp).getBytes(StandardCharsets.UTF_8));
        return md5Str;
    }

}
