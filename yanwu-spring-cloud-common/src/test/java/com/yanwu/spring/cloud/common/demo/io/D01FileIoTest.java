package com.yanwu.spring.cloud.common.demo.io;

import com.yanwu.spring.cloud.common.demo.d05io.d01socket.D01FileIO;
import org.openjdk.jmh.annotations.*;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/6/15 9:29.
 * <p>
 * description:
 */
public class D01FileIoTest {

    // ----- 开启JMH测试
    @Benchmark
    // ----- 预热
    @Warmup(iterations = 1, time = 3)
    // ----- 起多少个线程去执行
    @Fork(1)
    // ----- 测试模式【Mode.Throughput：吞吐量】
    @BenchmarkMode(Mode.Throughput)
    // ----- 测试次数
    @Measurement(iterations = 1, time = 3)
    public void testBasicFileIo() throws Exception {
        D01FileIO.basicFileIo();
    }
}
