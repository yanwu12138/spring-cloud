package com.yanwu.spring.cloud.udp.server.swing;

import com.yanwu.spring.cloud.udp.server.cache.ClientSessionMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static com.yanwu.spring.cloud.udp.server.swing.Constant.*;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-12-24 15:42.
 * <p>
 * description:
 */
@Slf4j
@Component
public class MyServer implements ApplicationListener<ContextRefreshedEvent>, Runnable {
    @Value("${netty.port}")
    private int nettyPort;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        SwingUtilities.invokeLater(new MyServer());
    }

    @Override
    public void run() {
        createAndShowGui();
    }

    private void createAndShowGui() {
        log.info("打开UDP-服务端窗口");
        JFrame frame = new JFrame("UDP-服务端");
        frame.setVisible(Boolean.TRUE);
        // ----- 设置窗体固定大小
        frame.setResizable(Boolean.FALSE);
        frame.setSize(800, 800);
        // ----- 设置窗体关闭方式
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // ===== 添加 IP地址 & 端口 输入框
        panel = new JPanel();
        createPanel();
        frame.add(panel);
    }

    private void createPanel() {
        createPortInput();
        createConnectionSize();
        createCleanLog();
        createConnectionArea();
        createLogScrollPane();
        createMessageButton();
    }

    /**
     * 端口
     */
    private void createPortInput() {
        panel.setLayout(null);
        JLabel portLabel = new JLabel("PORT:");
        portLabel.setBounds(50, 20, 80, 25);
        panel.add(portLabel);
        portText = new JTextField(20);
        portText.setBounds(100, 20, 50, 25);
        portText.setText("8888");
        portText.setEnabled(Boolean.FALSE);
        portText.setDisabledTextColor(Color.black);
        panel.add(portText);
    }

    /**
     * 当前连接 && 连接总数
     */
    private void createConnectionSize() {
        // ----- 当前连接
        JLabel beSelect = new JLabel("当前选中:");
        beSelect.setBounds(170, 20, 80, 25);
        panel.add(beSelect);
        beSelectText = new JTextField(20);
        beSelectText.setBounds(240, 20, 125, 25);
        beSelectText.setDisabledTextColor(Color.black);
        beSelectText.setEnabled(Boolean.FALSE);
        panel.add(beSelectText);
        // ----- 连接总数
        JLabel connectionNum = new JLabel("总连接数:");
        connectionNum.setBounds(400, 20, 80, 25);
        panel.add(connectionNum);
        connectionNumText = new JTextField(20);
        connectionNumText.setBounds(480, 20, 50, 25);
        connectionNumText.setDisabledTextColor(Color.black);
        connectionNumText.setEnabled(Boolean.FALSE);
        connectionNumText.setText("0");
        panel.add(connectionNumText);
    }

    /**
     * 清空日志按钮
     */
    private void createCleanLog() {
        JButton cleanLogButton = new JButton("清空日志");
        cleanLogButton.setBounds(670, 20, 90, 25);
        cleanLogButton.addActionListener(new SwingUtil.CleanLogActionListener());
        panel.add(cleanLogButton);
    }

    /**
     * 设备连接区
     */
    private void createConnectionArea() {
        connections = new JList<>();
        connections.setBounds(20, 60, 125, 680);
        JScrollPane connectionsPane = new JScrollPane(connections);
        connectionsPane.setBounds(20, 60, 125, 680);
        panel.add(connectionsPane);
        connections.setListData(new String[]{});
        connections.addListSelectionListener(new SwingUtil.BySelectionListListener());
    }

    /**
     * 日志输出区
     */
    private void createLogScrollPane() {
        JScrollPane logScrollPane = new JScrollPane();
        logScrollPane.setBounds(160, 60, 600, 630);
        panel.add(logScrollPane);
        logArea = new JTextArea();
        logArea.setBounds(160, 60, 600, 630);
        logScrollPane.setViewportView(logArea);
    }

    /**
     * 创建报文输入框和发送报文按钮
     */
    private void createMessageButton() {
        // ----- 报文输入框
        messageText = new JTextField(20);
        messageText.setBounds(160, 711, 500, 25);
        panel.add(messageText);
        // ----- 发送报文按钮
        JButton sendButton = new JButton("发送");
        sendButton.setBounds(670, 711, 90, 25);
        sendButton.addActionListener(new SwingUtil.SendMessageActionListener());
        panel.add(sendButton);
    }

}
