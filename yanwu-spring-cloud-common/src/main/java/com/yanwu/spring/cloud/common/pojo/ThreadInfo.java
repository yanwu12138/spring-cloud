package com.yanwu.spring.cloud.common.pojo;

import com.yanwu.spring.cloud.common.utils.ThreadUtil;
import lombok.Getter;
import lombok.ToString;
import org.apache.poi.ss.formula.functions.T;

import java.io.Serializable;

/**
 * @author XuBaofeng.
 * @date 2023/6/30 10:59.
 * <p>
 * description:
 */
@Getter
@ToString(exclude = {"thread"})
public class ThreadInfo implements Serializable {
    private static final long serialVersionUID = 7223163407992445294L;

    /*** key：THREAD_ID, 程序中线程的唯一标识 ***/
    private String key;

    /*** 等待线程 ***/
    private Thread thread;

    /*** 先成执行完之后的响应 ***/
    private Result<Object> result;

    /*** 超时时间: 毫秒 ***/
    private Long timeout;

    /*** 判断是否被唤醒 ***/
    private Boolean isNotify;

    public static ThreadInfo getInstance(Thread thread, long timeout) {
        String key = String.valueOf(ThreadUtil.getUniId());
        return getInstance(key, thread, timeout);
    }

    public static ThreadInfo getInstance(String key, Thread thread, long timeout) {
        ThreadInfo instance = new ThreadInfo();
        instance.setKey(String.join("_", key, thread.getName(), String.valueOf(thread.getId())));
        instance.setThread(thread);
        instance.setTimeout(timeout);
        instance.setIsNotify(false);
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
        this.result = Result.success(result);
        return this;
    }

    public ThreadInfo setIsNotify(boolean isNotify) {
        this.isNotify = isNotify;
        return this;
    }

}
