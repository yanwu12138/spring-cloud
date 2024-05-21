package com.yanwu.spring.cloud.postapi.service.impl;

import com.yanwu.spring.cloud.postapi.service.RibbonService;
import com.yanwu.spring.cloud.postapi.service.RootSwing;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.awt.*;

/**
 * @author XuBaofeng.
 * @date 2024/5/17 18:37.
 * <p>
 * description:
 */

@Slf4j
@Service("requestSendService")
public class RequestSendService implements RibbonService {

    private static final JPanel PANEL = new JPanel();

    @Override
    public void createRibbon(RootSwing window) {
        PANEL.setLayout(new BorderLayout());
        window.add(PANEL, BorderLayout.CENTER);
        log.info("init requestSendService success.");
    }

}
