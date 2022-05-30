package com.yanwu.spring.cloud.message.service;

import com.yanwu.spring.cloud.common.pojo.ResponseEnvelope;
import com.yanwu.spring.cloud.common.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

/**
 * @author Baofeng Xu
 * @date 2022/5/30 9:37.
 * <p>
 * description: WEB SOCKET
 */
@Slf4j
@Service
@ServerEndpoint("/message/websocket/{accountId}")
public class WebSocketService {

    /*** 用户登录accountId ***/
    private String accountId;
    private Session session;
    private long lastTime;

    private static final Set<WebSocketService> WEB_SOCKET_CACHE = new CopyOnWriteArraySet<>();

    /*** 建立连接 ***/
    @OnOpen
    public void onOpen(Session session, @PathParam("accountId") String accountId) {
        if (StringUtils.isBlank(accountId)) {
            log.error("web socket on open failed, because accountId is empty.");
            sendMessage(JsonUtil.toCompactJsonString(ResponseEnvelope.failed("accountId不能为空")));
            return;
        }
        this.session = session;
        this.accountId = accountId;
        this.lastTime = System.currentTimeMillis();
        WEB_SOCKET_CACHE.add(this);
        sendMessage(JsonUtil.toCompactJsonString(ResponseEnvelope.success("conn_success")));
        log.info("web socket on open, accountId: {}, count: {}", accountId, WEB_SOCKET_CACHE.size());
    }

    /*** 断开连接 ***/
    @OnClose
    public void onClose() {
        WEB_SOCKET_CACHE.remove(this);
        log.info("web socket on close, accountId: {}, count: {}", accountId, WEB_SOCKET_CACHE.size());
    }

    /*** 接收客户端消息 ***/
    @OnMessage
    public void onMessage(String message, Session session) {
        this.lastTime = System.currentTimeMillis();
        log.info("web socket read message, accountId: {}, message: {}", accountId, message);
    }

    /*** 错误 ***/
    @OnError
    public void onError(Session session, Throwable error) {
        this.lastTime = System.currentTimeMillis();
        log.error("web socket on error.", error);
    }

    /*** 给指定的用户发送消息 ***/
    public static void sendMessageByAccountId(String message, String accountId) {
        log.info("web socket send message by accountId, accountId: {}, message: {}", accountId, message);
        if (StringUtils.isBlank(accountId) || StringUtils.isBlank(message)) {
            log.error("web socket send message by accountId failed, accountId: {}, message: {}", accountId, message);
            return;
        }
        WEB_SOCKET_CACHE.stream().filter(item -> accountId.equals(item.accountId)).collect(Collectors.toList()).forEach(item -> item.sendMessage(message));
    }

    /*** 给指定用户群发送消息 ***/
    public static void sendMessageByAccountIds(String message, String... accountIds) {
        log.info("web socket send message by accountIds, accountIds: {}, message: {}", accountIds, message);
        for (String accountId : accountIds) {
            sendMessageByAccountId(message, accountId);
        }
    }

    /*** 给所有用户发送消息 ***/
    public static void sendMessageToAll(String message) {
        log.info("web socket send message to all, message: {}", message);
        WEB_SOCKET_CACHE.forEach(item -> sendMessageByAccountId(message, item.accountId));
    }

    /*** 发送消息 ***/
    private void sendMessage(String message) {
        this.lastTime = System.currentTimeMillis();
        try {
            this.session.getBasicRemote().sendText(message);
            log.info("web socket send message success. accountId: {}, message: {}.", this.accountId, message);
        } catch (Exception e) {
            log.error("web socket send message failed. accountId: {}, message: {}.", this.accountId, message, e);
        }
    }

    /*** 连接是否超时：当连接超过3个小时未断开且连续3个小时未发送任何消息，则认为改连接超时 ***/
    public static void checkTimeout() {
        WEB_SOCKET_CACHE.stream().filter(WebSocketService::timeout).collect(Collectors.toList()).forEach(WebSocketService::onClose);
    }

    /*** 【true: 超时; false: 未超时】 ***/
    private boolean timeout() {
        return System.currentTimeMillis() - this.lastTime >= 3 * 60 * 60 * 1_000L;
    }

}