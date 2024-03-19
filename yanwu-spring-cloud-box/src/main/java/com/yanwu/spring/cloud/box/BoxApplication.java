package com.yanwu.spring.cloud.box;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author XuBaofeng.
 * @date 2024/3/19 11:05.
 * <p>
 * description:
 */
@SpringBootApplication
public class BoxApplication extends JFrame {

    public BoxApplication() {
        initSwing();
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(BoxApplication.class).headless(false).run(args);
        EventQueue.invokeLater(() -> {
            BoxApplication application = context.getBean(BoxApplication.class);
            application.setVisible(true);
        });
    }

    private void initSwing() {
        setTitle("Tool BOX");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initJson();
    }

    private void initJson() {
        JButton quitButton = new JButton("JSON");
        quitButton.addActionListener((ActionEvent event) -> {

        });
        createLayout(quitButton);
    }

    private void createLayout(JComponent... arg) {
        Container pane = getContentPane();
        GroupLayout gl = new GroupLayout(pane);
        pane.setLayout(gl);
        gl.setAutoCreateContainerGaps(true);
        gl.setHorizontalGroup(gl.createSequentialGroup().addComponent(arg[0]));
        gl.setVerticalGroup(gl.createSequentialGroup().addComponent(arg[0]));
    }
}
