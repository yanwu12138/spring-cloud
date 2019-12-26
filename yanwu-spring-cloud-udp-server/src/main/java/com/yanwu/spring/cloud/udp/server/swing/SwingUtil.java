package com.yanwu.spring.cloud.udp.server.swing;

import com.yanwu.spring.cloud.udp.server.handler.Handler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.util.List;

import static com.yanwu.spring.cloud.udp.server.swing.Constant.*;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-12-24 15:56.
 * <p>
 * description:
 */
@Slf4j
@Component
public class SwingUtil {
    private static SwingUtil swingUtil;

    @Autowired
    private Handler handler;

    @PostConstruct
    public void init() {
        swingUtil = this;
    }

    public static void syncDeviceList(List sns) {
        connections.setListData(getNames(sns));
        connectionNumText.setText(String.valueOf(sns.size()));
    }

    private static String[] getNames(List sns) {
        if (CollectionUtils.isEmpty(sns)) {
            return new String[]{};
        }
        String[] result = new String[sns.size()];
        for (int i = 0; i < sns.size(); i++) {
            result[i] = (String) sns.get(i);
        }
        return result;
    }

    /**
     * 左侧列表选中事件
     */
    static class BySelectionListListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (connections.getValueIsAdjusting()) {
                // ----- 鼠标点击
                channelKey = connections.getSelectedValue();
                beSelectText.setText(channelKey);
            }
        }
    }

    /**
     * 清空日志按钮事件
     */
    static class CleanLogActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            logArea.setText("");
        }
    }

    /**
     * 发送报文
     */
    static class SendMessageActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            sendMessage();
        }
    }

    /**
     * 发送报文
     */
    private static void sendMessage() {
        String message = messageText.getText();
        if (StringUtils.isEmpty(message)) {
            printLog("发送失败, 请检查报文!", new RuntimeException("发送失败, 请检查报文!"));
            return;
        }
        try {
            swingUtil.handler.sendMessage(channelKey, message);
        } catch (Exception e) {
            printLog("发送失败, 请检查服务!", e);
        }
    }

    /**
     * 输出日志
     *
     * @param message 报文
     * @param e       异常信息
     */
    public static void printLog(String message, Throwable e) {
        logArea.append(LocalDateTime.now() + " " + message + "\r\n");
        if (e == null) {
            log.info(message);
        } else {
            log.error(message, e);
        }
    }

}
