package com.yanwu.spring.cloud.box.control;

import com.yanwu.spring.cloud.common.pojo.BaseParam;
import com.yanwu.spring.cloud.common.pojo.Result;
import com.yanwu.spring.cloud.common.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * @author XuBaofeng.
 * @date 2024/3/22 12:27.
 * <p>
 * description:
 */
@Slf4j
@RestController
@RequestMapping("/tool/")
public class ToolBoxController {
    private static final Random RANDOM = new Random();
    private static final Integer DEFAULT_LENGTH = 24;
    private static final String PASSWORD_KEY = "J3mqGev7!SlytCTLEzc1g-ZYxD=QIRWkKUjF+d9hi4a6B8pPwr5AMo2fV^nNsbuH0X";

    @PostMapping("randomPassword")
    public Result<String> randomPassword(@RequestBody BaseParam<Integer> param) {
        Integer size = param.getData();
        if (size == null || size < DEFAULT_LENGTH) {
            size = DEFAULT_LENGTH;
        }
        StringBuilder buffer = new StringBuilder();
        do {
            int index = RANDOM.nextInt(PASSWORD_KEY.length());
            buffer.append(PASSWORD_KEY.charAt(index));
            size--;
        } while (size > 0);
        return Result.success(buffer.toString());
    }

    @PostMapping("stampToTime")
    public Result<String> stampToTime(@RequestBody BaseParam<Long> param) {
        Long timestamp = param.getData();
        if (timestamp == null || timestamp <= 0) {
            timestamp = System.currentTimeMillis();
        }
        String timeStr = DateUtil.toTimeStr(timestamp, DateUtil.DateFormat.YYYY_MM_DD_HH_MM_SS);
        return Result.success(timeStr);
    }

    @PostMapping("timeToStamp")
    public Result<Long> timeToStamp(@RequestBody BaseParam<String> param) throws Exception {
        String timeStr = param.getData();
        if (StringUtils.isBlank(timeStr)) {
            BaseParam<Long> localtime = new BaseParam<>();
            timeStr = stampToTime(localtime).getData();
        }
        Long timestamp = DateUtil.toTimeLong(timeStr, DateUtil.DateFormat.YYYY_MM_DD_HH_MM_SS);
        return Result.success(timestamp);
    }

}
