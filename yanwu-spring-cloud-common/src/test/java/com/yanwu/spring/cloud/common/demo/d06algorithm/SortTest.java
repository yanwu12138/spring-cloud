package com.yanwu.spring.cloud.common.demo.d06algorithm;

import org.openjdk.jmh.annotations.*;

/**
 * @author Baofeng Xu
 * @date 2020/7/11 22:27.
 * <p>
 * description:
 */
public class SortTest {

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
    public void bubbleSort() {
        A000Sort.bubbleSort(A00Utils.array());
    }

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
    public void selectionSort() {
        A000Sort.selectionSort(A00Utils.array());
    }

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
    public void insertionSort() {
        A000Sort.insertionSort(A00Utils.array());
    }

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
    public void shellSort() {
        A000Sort.shellSort(A00Utils.array());
    }

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
    public void mergeSort() {
        A000Sort.mergeSort(A00Utils.array());
    }

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
    public void quickSort() {
        A000Sort.quickSort(A00Utils.array());
    }

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
    public void heapSort() {
        A000Sort.heapSort(A00Utils.array());
    }

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
    public void countingSort() {
        A000Sort.countingSort(A00Utils.array());
    }

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
    public void bucketSort() {
        A000Sort.bucketSort(A00Utils.array());
    }

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
    public void radixSort() {
        A000Sort.radixSort(A00Utils.array());
    }

}
