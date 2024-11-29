package com.yanwu.spring.cloud.common.utils.buc;

import com.yanwu.spring.cloud.common.pojo.SortedList;
import com.yanwu.spring.cloud.common.utils.DateUtil;
import com.yanwu.spring.cloud.common.utils.ExcelUtil;
import com.yanwu.spring.cloud.common.utils.FileUtil;
import com.yanwu.spring.cloud.common.utils.JsonUtil;
import com.yanwu.spring.cloud.common.utils.buc.ap.ApOnlineLog;
import com.yanwu.spring.cloud.common.utils.buc.ap.ApSwitchLog;
import com.yanwu.spring.cloud.common.utils.buc.edge.EdgeOnlineLog;
import com.yanwu.spring.cloud.common.utils.buc.edge.EdgeSwitchLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author XuBaofeng.
 * @date 2024/11/22 15:36.
 * <p>
 * description:
 */
@Slf4j
public class BucTest {

    private static final String BUC_BASE_PATH = "/Users/xubaofeng/Downloads/BUC";
    private static final String BUC_SHIP_INFO_PATH = BUC_BASE_PATH + "/BUG故障船只信息.json";

    public static void main(String[] args) throws Exception {
        byte[] bytes = FileUtil.read(BUC_SHIP_INFO_PATH);
        String shipInfoJson = new String(bytes, StandardCharsets.UTF_8);
        List<ShipInfo> shipInfos = JsonUtil.toObjectList(shipInfoJson, ShipInfo.class);
        List<ShipOnlineLog> shipOnlineLogs = new ArrayList<>();
        List<ShipSwitchLog> shipSwitchLogs = new ArrayList<>();
        for (ShipInfo shipInfo : shipInfos) {
            String deviceSn = shipInfo.getDeviceSn();
            Long bucTime = DateUtil.toTimeLong(shipInfo.getBucTime(), DateUtil.DateFormat.YYYY_MM_DD);
            Integer deviceType = shipInfo.getDeviceType();
            if (deviceType.compareTo(1) == 0) {
                // ----- 盒子
                List<EdgeOnlineLog> onlineLogs = edgeOnlineLog(deviceSn);
                edgeOnlineLogToShip(deviceSn, onlineLogs, shipOnlineLogs);
                List<EdgeSwitchLog> switchLogs = edgeSwitchLog(deviceSn);
                edgeSwitchLogToShip(deviceSn, switchLogs, shipSwitchLogs);
                log.info("edge ship buc, deviceSn: {}, excTime: {}, bucTime: {}, onlineLogs: {}, switchLogs: {}",
                        deviceSn, shipInfo.getBucTime(), bucTime, onlineLogs.size(), switchLogs.size());
            } else {
                // ----- AP
                List<ApOnlineLog> onlineLogs = apOnlineLog(deviceSn);
                apOnlineLogToShip(deviceSn, onlineLogs, shipOnlineLogs);
                List<ApSwitchLog> switchLogs = apSwitchLog(deviceSn);
                apSwitchLogToShip(deviceSn, switchLogs, shipSwitchLogs);
                log.info("AP ship buc, deviceSn: {}, excTime: {}, bucTime: {}, onlineLogs: {}, switchLogs: {}",
                        deviceSn, shipInfo.getBucTime(), bucTime, onlineLogs.size(), switchLogs.size());
            }
        }
        log.info("processing completed, total number of ships: {}", shipInfos.size());
        calcShipOnlineDurationDistribution(shipInfos, shipOnlineLogs, shipSwitchLogs);
    }

    private static void calcShipOnlineDurationDistribution(List<ShipInfo> shipInfos, List<ShipOnlineLog> shipOnlineLogs, List<ShipSwitchLog> shipSwitchLogs) throws Exception {
        List<ShipOnlineDuration> durations = new ArrayList<>();
        System.out.println("==================== 计算船只在线持续时间分布 ====================");

        System.out.println("|船名|设备|BUC故障时间|在线总时长|6D在线时长|138在线时长|");
        System.out.println("|---|---|-----------|--------|--------|----------|");

        for (ShipInfo shipInfo : shipInfos) {
            ShipOnlineDuration instance = ShipOnlineDuration.getInstance(shipInfo.getShipName(), shipInfo.getDeviceSn(), shipInfo.getBucTime());
            // ***** 计算小站的在线时长
            SortedList<ShipOnlineLog> itemOnlineLogs = new SortedList<>();
            shipOnlineLogs.forEach(item -> {
                if (shipInfo.getDeviceSn().equals(item.getDeviceSn())) {
                    itemOnlineLogs.add(item);
                }
            });
            long countOnlineTime = getCountOnlineTime(itemOnlineLogs);

            // ***** 计算小站在各个波束的在线时长
            SortedList<ShipSwitchLog> itemSwitchLogs = new SortedList<>();
            shipSwitchLogs.forEach(item -> {
                if (shipInfo.getDeviceSn().equals(item.getDeviceSn())) {
                    itemSwitchLogs.add(item);
                }
            });
            long lock6DTime = getLock6DTime(itemOnlineLogs, itemSwitchLogs);
            if (lock6DTime >= countOnlineTime) {
                countOnlineTime = lock6DTime;
            }
            long lock138Time = countOnlineTime - lock6DTime;

            System.out.println("|" + shipInfo.getShipName() + "|" + shipInfo.getDeviceSn() + "|" + shipInfo.getBucTime()
                    + "|" + (countOnlineTime / 1000 / 3600) + "|" + (lock6DTime / 1000 / 3600) + "|" + (lock138Time / 1000 / 3600) + "|");
            instance.setCountOnline(countOnlineTime / 1000 / 3600).setLock6DTime(lock6DTime / 1000 / 3600).setLock138Time(lock138Time / 1000 / 3600);
            durations.add(instance);
        }
        String durationJsonPath = BUC_BASE_PATH + "/durations.json";
        FileUtil.deleteFile(durationJsonPath);
        FileUtil.checkFilePath(durationJsonPath, Boolean.TRUE);
        FileUtil.write(durationJsonPath, JsonUtil.toString(durations).getBytes(StandardCharsets.UTF_8), 0);

        String durationXlsxPath = BUC_BASE_PATH + "/durations.xlsx";
        List<String> head = ExcelUtil.assembleHead("shipName", "deviceSn", "bucTime", "countOnline", "lock6DTime", "lock138Time");
        SXSSFWorkbook sheets = ExcelUtil.assembleExcelByList(head, durations);
        FileUtil.deleteFile(durationXlsxPath);
        FileUtil.checkFilePath(durationXlsxPath, Boolean.TRUE);
        ExcelUtil.writeExcel(sheets, durationXlsxPath);
    }

    private static long getLock6DTime(SortedList<ShipOnlineLog> itemOnlineLogs, SortedList<ShipSwitchLog> itemSwitchLogs) {
        if (CollectionUtils.isEmpty(itemOnlineLogs) || CollectionUtils.isEmpty(itemSwitchLogs)) {
            return 0L;
        }
        List<ShipOnlineEvent> onlineEvents = ShipOnlineEvent.buildShipOnlineEvents(itemOnlineLogs);
        ShipOnlineLog lastOfflineEvent = itemOnlineLogs.stream().filter(item -> !item.isOnline()).findFirst().get();
        List<ShipSwitchEvent> switchEvents = ShipSwitchEvent.buildShipSwitchEvents(lastOfflineEvent, itemSwitchLogs);
        if (CollectionUtils.isEmpty(onlineEvents) || CollectionUtils.isEmpty(switchEvents)) {
            return 0L;
        }
        long lock6DTime = 0L;
        for (ShipSwitchEvent itemSwitch : switchEvents) {
            if (itemSwitch.getBeamId().equals("138")) {
                continue;
            }
            ShipOnlineEvent checkInOnlineEvent = null, checkOutOnlineEvent = null;
            for (ShipOnlineEvent itemOnline : onlineEvents) {
                if (itemOnline.getOnlineTime() <= itemSwitch.getCheckInTime() && itemOnline.getOfflineTime() >= itemSwitch.getCheckInTime()) {
                    checkInOnlineEvent = itemOnline;
                }
                if (itemOnline.getOnlineTime() <= itemSwitch.getCheckOutTime() && itemOnline.getOfflineTime() >= itemSwitch.getCheckOutTime()) {
                    checkOutOnlineEvent = itemOnline;
                }
            }
            if (checkInOnlineEvent == null || checkOutOnlineEvent == null) {
                continue;
            }
            if (checkInOnlineEvent.equals(checkOutOnlineEvent)) {
                long tempTime = itemSwitch.getCheckOutTime() - itemSwitch.getCheckInTime();
                lock6DTime += tempTime;
            } else {
                long tempIn = checkInOnlineEvent.getOfflineTime() - itemSwitch.getCheckInTime();
                lock6DTime += tempIn;
                long tempOut = itemSwitch.getCheckOutTime() - checkOutOnlineEvent.getOnlineTime();
                lock6DTime += tempOut;
            }
        }
        return lock6DTime;
    }

    private static long getCountOnlineTime(SortedList<ShipOnlineLog> itemOnlineLogs) {
        List<ShipOnlineEvent> onlineEvents = ShipOnlineEvent.buildShipOnlineEvents(itemOnlineLogs);
        if (CollectionUtils.isEmpty(onlineEvents)) {
            return 0L;
        }
        long countOnlineTime = 0L;
        for (ShipOnlineEvent itemEvent : onlineEvents) {
            long tempTime = itemEvent.getOfflineTime() - itemEvent.getOnlineTime();
            countOnlineTime += tempTime;
        }
        return countOnlineTime;
    }

    private static List<EdgeOnlineLog> edgeOnlineLog(String deviceSn) throws Exception {
        String filePath = String.join("/", BUC_BASE_PATH, "源数据", "EDGE", "onlineLogs", (deviceSn + ".json"));
        String onlineLogJson = new String(FileUtil.read(filePath), StandardCharsets.UTF_8);
        return JsonUtil.toObjectList(onlineLogJson, EdgeOnlineLog.class);
    }

    private static void edgeOnlineLogToShip(String deviceSn, List<EdgeOnlineLog> edgeOnlineLogs, List<ShipOnlineLog> shipOnlineLogs) throws Exception {
        if (StringUtils.isBlank(deviceSn) || CollectionUtils.isEmpty(edgeOnlineLogs)) {
            return;
        }
        for (EdgeOnlineLog item : edgeOnlineLogs) {
            if (item.getOnline() == null || StringUtil.isBlank(item.getDate_created())) {
                continue;
            }
            Long timeLong = DateUtil.toTimeLong(item.getDate_created(), DateUtil.DateFormat.DD_MM_YYYY_HH_MM_SS);
            shipOnlineLogs.add(ShipOnlineLog.getInstance(deviceSn, timeLong, item.getOnline().compareTo(1) == 0));
        }
    }

    private static List<EdgeSwitchLog> edgeSwitchLog(String deviceSn) throws Exception {
        String filePath = String.join("/", BUC_BASE_PATH, "源数据", "EDGE", "switchLogs", (deviceSn + ".json"));
        String onlineLogJson = new String(FileUtil.read(filePath), StandardCharsets.UTF_8);
        return JsonUtil.toObjectList(onlineLogJson, EdgeSwitchLog.class);
    }

    private static void edgeSwitchLogToShip(String deviceSn, List<EdgeSwitchLog> edgeSwitchLogs, List<ShipSwitchLog> shipOnlineLogs) {
        if (StringUtils.isBlank(deviceSn) || CollectionUtils.isEmpty(edgeSwitchLogs)) {
            return;
        }
        for (EdgeSwitchLog item : edgeSwitchLogs) {
            if (StringUtil.isBlank(item.getIndex()) || !item.getIndex().equals("0")) {
                continue;
            }
            ShipSwitchLog shipSwitchLog = JsonUtil.convertObject(item, ShipSwitchLog.class);
            shipOnlineLogs.add(shipSwitchLog.setDeviceSn(deviceSn));
        }
    }

    private static List<ApOnlineLog> apOnlineLog(String deviceSn) throws Exception {
        String filePath = String.join("/", BUC_BASE_PATH, "源数据", "AP", "onlineLogs", (deviceSn + ".json"));
        String onlineLogJson = new String(FileUtil.read(filePath), StandardCharsets.UTF_8);
        return JsonUtil.toObjectList(onlineLogJson, ApOnlineLog.class);
    }

    private static void apOnlineLogToShip(String deviceSn, List<ApOnlineLog> apOnlineLogs, List<ShipOnlineLog> shipOnlineLogs) {
        if (StringUtils.isBlank(deviceSn) || CollectionUtils.isEmpty(apOnlineLogs)) {
            return;
        }
        for (ApOnlineLog item : apOnlineLogs) {
            shipOnlineLogs.add(ShipOnlineLog.getInstance(deviceSn, item.getDatetime(), item.getEvent().equals("online")));
        }
    }

    private static List<ApSwitchLog> apSwitchLog(String deviceSn) throws Exception {
        String filePath = String.join("/", BUC_BASE_PATH, "源数据", "AP", "switchLogs", (deviceSn + ".json"));
        String onlineLogJson = new String(FileUtil.read(filePath), StandardCharsets.UTF_8);
        return JsonUtil.toObjectList(onlineLogJson, ApSwitchLog.class);
    }

    private static void apSwitchLogToShip(String deviceSn, List<ApSwitchLog> apSwitchLogs, List<ShipSwitchLog> shipOnlineLogs) {
        if (StringUtils.isBlank(deviceSn) || CollectionUtils.isEmpty(apSwitchLogs)) {
            return;
        }
        for (ApSwitchLog item : apSwitchLogs) {
            if (StringUtil.isBlank(item.getIndex()) || !item.getIndex().equals("0")) {
                ShipSwitchLog shipSwitchLog = JsonUtil.convertObject(item, ShipSwitchLog.class);
                shipOnlineLogs.add(shipSwitchLog.setDeviceSn(deviceSn));
            }
        }
    }

    private static void writeFile(String deviceSn, String deviceType, String fileType, Object file) throws Exception {
        String filePath = String.join("/", BUC_BASE_PATH, deviceType, fileType, (deviceSn + ".json"));
        FileUtil.deleteFile(filePath);
        FileUtil.checkFilePath(filePath, Boolean.TRUE);
        FileUtil.write(filePath, JsonUtil.toString(file).getBytes(StandardCharsets.UTF_8), 0);
    }

}
