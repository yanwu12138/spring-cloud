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
 * @date 2024/11/28 16:47.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
public class ShipOnlineEvent implements Serializable {
    private static final long serialVersionUID = 2593818618445472083L;

    private Long offlineTime;

    private Long onlineTime;

    public static ShipOnlineEvent getInstance(long offlineTime, long onlineTime) {
        return new ShipOnlineEvent().setOfflineTime(offlineTime).setOnlineTime(onlineTime);
    }

    public static List<ShipOnlineEvent> buildShipOnlineEvents(SortedList<ShipOnlineLog> onlineLogs) {
        if (CollectionUtils.isEmpty(onlineLogs)) {
            return Collections.emptyList();
        }
        List<ShipOnlineEvent> shipOnlineEvent = new ArrayList<>();
        boolean offlineFlag = false;
        long lastOfflineTime = 0L;
        for (ShipOnlineLog itemOnline : onlineLogs) {
            if (offlineFlag && itemOnline.isOnline()) {
                offlineFlag = false;
                shipOnlineEvent.add(ShipOnlineEvent.getInstance(lastOfflineTime, itemOnline.getTime()));
            } else {
                offlineFlag = true;
                lastOfflineTime = itemOnline.getTime();
            }
        }
        return shipOnlineEvent;
    }

}
