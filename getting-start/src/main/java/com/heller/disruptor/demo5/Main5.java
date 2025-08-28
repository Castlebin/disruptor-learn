package com.heller.disruptor.demo5;

import java.nio.ByteBuffer;

import com.heller.disruptor.demo1.LongEvent;
import com.heller.disruptor.demo1.LongEventFactory;
import com.heller.disruptor.demo2.LongEventProducerWithTranslator;
import com.heller.disruptor.demo4.LongEventWorkHandler;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;

// 多生产者，多消费者，每条消息只被其中一个消费者消费
public class Main5 {
    public static void main(String[] args) throws Exception {
        // Ring Buffer 大小，必须是2的幂
        int bufferSize = 1024;

        // 1. 创建 Disruptor 实例
        Disruptor<LongEvent> disruptor = new Disruptor<>(
                new LongEventFactory(),
                bufferSize,
                DaemonThreadFactory.INSTANCE,
                // 改为多生产者模式！！
                ProducerType.MULTI,
                new YieldingWaitStrategy()
        );

        // 2. 注册事件处理器
        // 多个消费者，每条消息只被其中一个消费者消费。使用的是 handleEventsWithWorkerPool 方法
        // 消费者要实现 WorkHandler 接口，一条消息只会被一个消费者消费
        disruptor.handleEventsWithWorkerPool(new LongEventWorkHandler(), new LongEventWorkHandler());

        // 3. 启动 Disruptor
        disruptor.start();

        // 4. 获取 Ring Buffer
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();

        // 5. 创建生产者
        // 使用 EventTranslator 的生产者 代替  LongEventProducer
        LongEventProducerWithTranslator producer = new LongEventProducerWithTranslator(ringBuffer);

        // 6. 模拟生产者发送数据

        // 共用 ByteBuffer 会导致数据错乱，不能共用 （并发导致数据覆盖）
        // ByteBuffer byteBuffer = ByteBuffer.allocate(8);

        // 多个生产则会
        // 再来两个生产者
        new Thread(() -> {
            // !!! 注意这里的 ByteBuffer 不能公用
            ByteBuffer byteBuffer = ByteBuffer.allocate(8);
            // 创建生产者
            LongEventProducerWithTranslator eventProducer = new LongEventProducerWithTranslator(ringBuffer);
            // 发送消息
            for (int i = 0; i < 100; i++) {
                eventProducer.onData(byteBuffer.putLong(0, i));
                // 模拟发送间隔
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "producer1").start();

        new Thread(() -> {
            // !!! 注意这里的 ByteBuffer 不能公用
            ByteBuffer byteBuffer = ByteBuffer.allocate(8);
            // 创建生产者
            LongEventProducerWithTranslator eventProducer = new LongEventProducerWithTranslator(ringBuffer);
            // 发送消息
            for (int i = 100; i < 200; i++) {
                eventProducer.onData(byteBuffer.putLong(0, i));
                // 模拟发送间隔
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "producer2").start();
    }

}
