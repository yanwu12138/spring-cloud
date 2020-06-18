package com.yanwu.spring.cloud.common.demo.d02container.c01collection.c01list;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/6/18 13:51.
 * <p>
 * description:
 */
public class D01CopyOnWriteArrayList {

    public static void main(String[] args) {
        D00ListUtil.test(new CopyOnWriteArrayList<>());
    }

}
