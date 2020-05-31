package com.yanwu.spring.cloud.common.demo.mq.disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-05-30 22:32:56.
 * <p>
 * describe: 在初始化环形队列时，通过eventFactory，对ringBuffer进行内存提前分配。
 * 在环形队列的每个位置提前通过nuwInstance()函数创建好对象，使用的时候直接使用，不需要做任何其它初始化的动作
 */
public class StringEventFactory implements EventFactory<StringEvent> {

    @Override
    public StringEvent newInstance() {
        return new StringEvent();
    }

}
