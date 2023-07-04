package com.yanwu.spring.cloud.common.pojo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author XuBaofeng.
 * @date 2023/6/30 10:59.
 * <p>
 * description:
 */
@Getter
@EqualsAndHashCode
@ToString(exclude = {"thread", "result", "isNotify"})
public class ThreadInfo implements Serializable {
    private static final long serialVersionUID = 7223163407992445294L;

    /*** key：THREAD_ID, 程序中线程的唯一标识 ***/
    private String key;

    /*** 等待线程 ***/
    private Thread thread;

    /*** 先成执行完之后的响应 ***/
    private Object result;

    /*** 超时时间: 毫秒 ***/
    private Long timeout;

    /*** 判断是否被唤醒 ***/
    private Boolean isNotify;

    public static ThreadInfo getInstance(String key, long timeout) {
        Thread thread = Thread.currentThread();
        ThreadInfo instance = new ThreadInfo();
        instance.setKey(String.join(":", key, thread.getName(), String.valueOf(thread.getId())));
        instance.setThread(thread).setTimeout(timeout).setIsNotify(false);
        return instance;
    }

    private ThreadInfo() {
    }

    private ThreadInfo setKey(String key) {
        this.key = key;
        return this;
    }

    private ThreadInfo setThread(Thread thread) {
        this.thread = thread;
        return this;
    }

    private ThreadInfo setTimeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    public ThreadInfo setResult(Object result) {
        this.result = result;
        return this;
    }

    public ThreadInfo setIsNotify(boolean isNotify) {
        this.isNotify = isNotify;
        return this;
    }

}
