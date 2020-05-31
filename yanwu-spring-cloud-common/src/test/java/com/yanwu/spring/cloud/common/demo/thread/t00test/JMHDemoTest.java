package com.yanwu.spring.cloud.common.demo.thread.t00test;

import org.openjdk.jmh.annotations.*;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-05-30 21:38:37.
 * <p>
 * describe:
 */
public class JMHDemoTest {

    // ----- 开启JMH测试
    @Benchmark
    // ----- 预热
    @Warmup(iterations = 1, time = 3)
    // ----- 起多少个线程去执行
    @Fork(5)
    // ----- 测试模式【Mode.Throughput：吞吐量】
    @BenchmarkMode(Mode.Throughput)
    // ----- 测试次数
    @Measurement(iterations = 1, time = 3)
    public void testForEach() {
        JMHDemo.forEach();
    }

    @Benchmark
    @Warmup(iterations = 1, time = 3)
    @Fork(5)
    @BenchmarkMode(Mode.Throughput)
    @Measurement(iterations = 1, time = 3)
    public void testParallel() {
        JMHDemo.parallel();
    }
}
