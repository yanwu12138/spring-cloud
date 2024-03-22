package com.yanwu.spring.cloud.box.control;

import com.yanwu.spring.cloud.common.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
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

    @RequestMapping("randomPassword/{size}")
    public Result<String> randomPassword(@PathVariable(value = "size", required = false) Integer size) {
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

}
