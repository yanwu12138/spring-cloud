package com.yanwu.spring.cloud.common.pojo;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Baofeng Xu
 * @date 2022-11-24 024 14:43:44.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
public class PingBO implements Serializable {
    private static final long serialVersionUID = 7179539695519326224L;
    private static final Pattern RECEIVE_PATTERN = Pattern.compile(" TTL=([\\s\\S]*?)", Pattern.CASE_INSENSITIVE);
    private static final Pattern TIMES_PATTERN = Pattern.compile("time[<|=]([\\s\\S]*?)ms", Pattern.CASE_INSENSITIVE);


    /*** 总共ping的次数 ***/
    private int max;
    /*** 收到回复的次数 ***/
    private int receive = 0;
    /*** 丢包率 ***/
    private int loss = 100;
    /*** 延迟(ms) ***/
    private int time = 1000;

    public static PingBO getInstance(int times, String commandResult) {
        PingBO result = new PingBO();
        result.setMax(times);
        if (StringUtils.isBlank(commandResult)) {
            return result;
        }
        int receive = calcReceive(commandResult);
        result.setReceive(receive);
        result.setLoss(calcLoss(times, receive));
        result.setTime(calcTime(commandResult));
        return result;
    }

    private static int calcReceive(String commandResult) {
        int receive = 0;
        if (StringUtils.isBlank(commandResult)) {
            return receive;
        }
        Matcher matcher = RECEIVE_PATTERN.matcher(commandResult);
        while (matcher.find()) {
            receive++;
        }
        return receive;
    }

    private static int calcLoss(int times, int receive) {
        if (times <= 0 || receive <= 0) {
            return 100;
        }
        if (times <= receive) {
            return 0;
        }
        BigDecimal loss = BigDecimal.valueOf(times - receive).divide(BigDecimal.valueOf(times), 2, RoundingMode.HALF_UP);
        return loss.multiply(BigDecimal.valueOf(100)).intValue();
    }

    private static int calcTime(String commandResult) {
        double sum = 0.0;
        int times = 0;
        Matcher matcher = TIMES_PATTERN.matcher(commandResult);
        while (matcher.find()) {
            String group = matcher.group();
            group = group.replace("time", "")
                    .replace("<", "")
                    .replace("=", "")
                    .replace("ms", "");
            sum = sum + Double.parseDouble(group);
            times++;
        }
        if (times == 0) {
            return -1;
        }
        return BigDecimal.valueOf(sum).divide(BigDecimal.valueOf(times), 1, RoundingMode.HALF_UP).intValue();
    }

}
