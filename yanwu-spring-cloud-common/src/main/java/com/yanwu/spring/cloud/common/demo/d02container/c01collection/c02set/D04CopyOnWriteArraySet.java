package com.yanwu.spring.cloud.common.demo.d02container.c01collection.c02set;

import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/6/18 13:54.
 * <p>
 * description:
 */
public class D04CopyOnWriteArraySet {

    public static void main(String[] args) {
        D00SetUtil.test(new CopyOnWriteArraySet<>());
    }

}
