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
        A01Sort.bubbleSort(A00Utils.array());
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
        A01Sort.selectionSort(A00Utils.array());
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
        A01Sort.insertionSort(A00Utils.array());
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
        A01Sort.shellSort(A00Utils.array());
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
        A01Sort.mergeSort(A00Utils.array());
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
        A01Sort.quickSort(A00Utils.array());
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
        A01Sort.heapSort(A00Utils.array());
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
        A01Sort.countingSort(A00Utils.array());
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
        A01Sort.bucketSort(A00Utils.array());
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
        A01Sort.radixSort(A00Utils.array());
    }

}
