package com.yanwu.spring.cloud.postapi;

import com.yanwu.spring.cloud.postapi.service.RibbonService;
import com.yanwu.spring.cloud.postapi.service.RootSwing;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * @author XuBaofeng.
 * @date 2024/3/19 11:05.
 * <p>
 * description:
 */
@Slf4j
@SpringBootApplication
public class PostApiApplication extends JFrame {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(PostApiApplication.class).headless(false).run(args);
        EventQueue.invokeLater(() -> {
            RootSwing window = ctx.getBean(RootSwing.class);
            Map<String, RibbonService> ribbons = ctx.getBeansOfType(RibbonService.class);
            if (MapUtils.isNotEmpty(ribbons)) {
                ribbons.values().forEach(service -> service.createRibbon(window));
            }
            window.setVisible(true);
        });
    }

}
