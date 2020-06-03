package com.yanwu.spring.cloud.common.demo.mq.disruptor;

import org.openjdk.jmh.annotations.*;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-05-30 22:48:36.
 * <p>
 * describe:
 */
public class D01DisruptorTest {

    @Benchmark
    @Warmup(iterations = 1, time = 3)
    @Fork(3)
    @BenchmarkMode(Mode.Throughput)
    @Measurement(iterations = 1)
    public void D01DisruptorTest1() {
        D01Disruptor.testDisruptor01();
    }

    @Benchmark
    @Warmup(iterations = 1, time = 3)
    @Fork(3)
    @BenchmarkMode(Mode.Throughput)
    @Measurement(iterations = 1)
    public void D01DisruptorTest3() {
        D01Disruptor.testDisruptor03();
    }

    @Benchmark
    @Warmup(iterations = 1, time = 3)
    @Fork(3)
    @BenchmarkMode(Mode.Throughput)
    @Measurement(iterations = 1)
    public void D01DisruptorTest4() {
        D01Disruptor.testDisruptor04();
    }

    @Benchmark
    @Warmup(iterations = 1, time = 3)
    @Fork(3)
    @BenchmarkMode(Mode.Throughput)
    @Measurement(iterations = 1)
    public void D01DisruptorTest() {
        D01Disruptor.testDisruptor();
    }
}
