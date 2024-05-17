package com.yanwu.spring.cloud.postapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import java.awt.*;

/**
 * @author XuBaofeng.
 * @date 2024/3/19 11:05.
 * <p>
 * description:
 */
@Slf4j
@SpringBootApplication
public class PostApiApplication extends JFrame {

    PostApiApplication() {
        initUI();
    }

    private void initUI() {
        setTitle("PostApi");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void createLayout(JComponent... arg) {
        Container pane = getContentPane();
        GroupLayout gl = new GroupLayout(pane);
        pane.setLayout(gl);
        gl.setAutoCreateContainerGaps(true);
        gl.setHorizontalGroup(gl.createSequentialGroup().addComponent(arg[0]));
        gl.setVerticalGroup(gl.createSequentialGroup().addComponent(arg[0]));
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(PostApiApplication.class).headless(false).run(args);
        EventQueue.invokeLater(() -> {
            PostApiApplication ex = ctx.getBean(PostApiApplication.class);
            ex.setVisible(true);
        });
    }

}
