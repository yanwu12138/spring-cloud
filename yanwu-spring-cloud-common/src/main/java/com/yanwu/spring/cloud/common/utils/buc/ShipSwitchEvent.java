package com.yanwu.spring.cloud.common.utils.buc;

import com.yanwu.spring.cloud.common.pojo.SortedList;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author XuBaofeng.
 * @date 2024/11/28 16:59.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
public class ShipSwitchEvent implements Serializable {
    private static final long serialVersionUID = 5458386613094072776L;

    private String beamId;

    private Long checkInTime;

    private Long checkOutTime;

    public static ShipSwitchEvent getInstance(String beamId, Long checkInTime, Long checkOutTime) {
        return new ShipSwitchEvent().setBeamId(beamId).setCheckInTime(checkInTime).setCheckOutTime(checkOutTime);
    }

    public static List<ShipSwitchEvent> buildShipSwitchEvents(ShipOnlineLog lastOfflineEvent, SortedList<ShipSwitchLog> switchLogs) {
        if (CollectionUtils.isEmpty(switchLogs)) {
            return Collections.emptyList();
        }
        ArrayList<ShipSwitchEvent> switchEvents = new ArrayList<>();
        ShipSwitchLog lastLog = null;
        for (ShipSwitchLog switchLog : switchLogs) {
            if (lastLog == null) {
                lastLog = switchLog;
                switchEvents.add(ShipSwitchEvent.getInstance(switchLog.getBeamId(), switchLog.getDatetime(), lastOfflineEvent.getTime()));
            } else {
                switchEvents.add(ShipSwitchEvent.getInstance(switchLog.getBeamId(), switchLog.getDatetime(), lastLog.getDatetime()));
            }
        }
        return switchEvents;
    }

}
