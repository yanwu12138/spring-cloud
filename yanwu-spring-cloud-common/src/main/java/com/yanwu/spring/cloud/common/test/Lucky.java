package com.yanwu.spring.cloud.common.test;

import com.yanwu.spring.cloud.common.utils.ArrayUtil;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;

/**
 * @author XuBaofeng.
 * @date 2024/7/15 17:57.
 * <p>
 * description:
 */
public class Lucky {
    private static final List<String> RED_BALL = ArrayUtil.toList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33");

    private static final List<String> BLUE_BALL = ArrayUtil.toList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16");


    public static void main(String[] args) {
        int lucky = RandomUtils.nextInt(BLUE_BALL.size(), RED_BALL.size());
        int nextInt;
        do {
            nextInt = RandomUtils.nextInt(0, BLUE_BALL.size() * RED_BALL.size());
        } while (nextInt % lucky != 0);
        System.out.println("lucky: " + lucky + ", nextInt: " + nextInt);
        String[] numbers = randomRed();
        String blue = BLUE_BALL.get(RandomUtils.nextInt(0, BLUE_BALL.size()));
        System.out.println("red: [" + String.join(",", numbers) + "]; blue: [" + blue + "].");
    }

    private static String[] randomRed() {
        String[] numbers = new String[6];
        int redIndex = 0;
        while (redIndex < 6) {
            int number = RandomUtils.nextInt(0, RED_BALL.size());
            numbers[redIndex] = RED_BALL.get(number);
            RED_BALL.remove(number);
            redIndex++;
        }
        return numbers;
    }

}
