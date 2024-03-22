package com.yanwu.spring.cloud.box.control;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author XuBaofeng.
 * @date 2024/3/22 09:59.
 * <p>
 * description:
 */
@Slf4j
@Controller
@RequestMapping("/page/")
public class PageController {

    @RequestMapping("index")
    public String start() {
        return "start.html";
    }

}
