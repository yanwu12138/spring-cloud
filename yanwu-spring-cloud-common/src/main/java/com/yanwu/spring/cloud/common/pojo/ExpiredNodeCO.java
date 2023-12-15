package com.yanwu.spring.cloud.common.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author XuBaofeng.
 * @date 2023/12/15 10:15.
 * <p>
 * description:
 */
@ToString
@Accessors(chain = true)
public class ExpiredNodeCO<V> implements Serializable {
    private static final long serialVersionUID = 1197572171199448469L;

    @Getter
    @Setter
    private V value;

    private Long lastTime = System.currentTimeMillis();

    private ExpiredNodeCO() {
    }

    public static <V> ExpiredNodeCO<V> getInstance(V value) {
        ExpiredNodeCO<V> result = new ExpiredNodeCO<>();
        return result.setValue(value);
    }

    /*** 重置最后访问时间 ***/
    public void resetTime() {
        this.lastTime = System.currentTimeMillis();
    }

    /*** 检查是否超时：【true: 超时; false: 未超时】 ***/
    public boolean timeout(long localTime, long expired) {
        return localTime - lastTime >= expired;
    }
}