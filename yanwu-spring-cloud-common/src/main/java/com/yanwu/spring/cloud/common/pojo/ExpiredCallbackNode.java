package com.yanwu.spring.cloud.common.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.function.Function;

/**
 * @author XuBaofeng.
 * @date 2023/12/15 10:55.
 * <p>
 * description:
 */
@Accessors(chain = true)
public class ExpiredCallbackNode<V> extends ExpiredNode<V> implements Serializable {
    private static final long serialVersionUID = -5793743609750000076L;

    /*** Key过期时执行的过期方法 ***/
    @Setter
    @JsonIgnore
    private Function<V, Boolean> function;

    public static <V> ExpiredCallbackNode<V> getInstance(@NotNull V value, @NotNull long expired, @NotNull Function<V, Boolean> function) {
        ExpiredCallbackNode<V> instance = new ExpiredCallbackNode<>();
        instance.setValue(value);
        instance.setExpired(expired);
        instance.setFunction(function);
        return instance;
    }

    @Override
    public Boolean callback() {
        return function.apply(getValue());
    }

}
