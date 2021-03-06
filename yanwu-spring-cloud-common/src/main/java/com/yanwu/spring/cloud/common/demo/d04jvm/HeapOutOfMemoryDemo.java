package com.yanwu.spring.cloud.common.demo.d04jvm;

import java.util.ArrayList;
import java.util.List;

/***
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * 模拟堆溢出异常
 * VM Options: -Xms10m -Xmx10m -XX:+HeapDumpOnOutOfMemoryError
 */
public class HeapOutOfMemoryDemo {
    public static void main(String[] args) throws Exception {
        List<OutOfMemoryObject> temp = new ArrayList<>();
        for (; ; ) {
            temp.add(new OutOfMemoryObject());
        }
    }

    private static class OutOfMemoryObject {

    }
}