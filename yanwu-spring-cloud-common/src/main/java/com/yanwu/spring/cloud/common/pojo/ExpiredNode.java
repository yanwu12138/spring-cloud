package com.yanwu.spring.cloud.common.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author XuBaofeng.
 * @date 2023/12/15 10:15.
 * <p>
 * description:
 */
@ToString
@Accessors(chain = true)
public class ExpiredNode<V> implements Serializable {
    private static final long serialVersionUID = -2862488510245016394L;

    @Getter
    @Setter
    private V value;

    @Getter
    protected long expired;

    protected long lastTime = System.currentTimeMillis();

    protected ExpiredNode() {
    }

    public static <V> ExpiredNode<V> getInstance(@NotNull V value, @NotNull long expired) {
        ExpiredNode<V> instance = new ExpiredNode<>();
        return instance.setValue(value).setExpired(expired);
    }

    protected ExpiredNode<V> setExpired(long expired) {
        this.expired = expired;
        return this;
    }

    /*** 重置最后访问时间 ***/
    public void resetTime() {
        this.lastTime = System.currentTimeMillis();
    }

    /*** 检查是否超时：【true: 超时; false: 未超时】 ***/
    public boolean timeout(long localTime) {
        return localTime - lastTime >= expired;
    }

    public Boolean callback() {
        return Boolean.TRUE;
    }

}