package com.yanwu.spring.cloud.postapi.service;

import org.springframework.stereotype.Service;

import javax.swing.*;
import java.awt.*;

/**
 * @author XuBaofeng.
 * @date 2024/5/17 14:47.
 * <p>
 * description:
 */
@Service
public class RootSwing extends JFrame {

    RootSwing() {
        initUI();
    }

    /**
     * 整体窗格
     */
    private void initUI() {
        setTitle("PostApi");
        setLocationRelativeTo(null);
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

}
