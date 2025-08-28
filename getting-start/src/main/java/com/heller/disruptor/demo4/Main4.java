package com.heller.disruptor.demo4;

import java.nio.ByteBuffer;

import com.heller.disruptor.demo1.LongEvent;
import com.heller.disruptor.demo1.LongEventFactory;
import com.heller.disruptor.demo1.LongEventHandler;
import com.heller.disruptor.demo2.LongEventProducerWithTranslator;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;

// 多个消费者，每条消息只被其中一个消费者消费
public class Main4 {
    public static void main(String[] args) throws Exception {
        // Ring Buffer 大小，必须是2的幂
        int bufferSize = 1024;

        // 1. 创建 Disruptor 实例
        Disruptor<LongEvent> disruptor = new Disruptor<>(
                new LongEventFactory(),
                bufferSize,
                DaemonThreadFactory.INSTANCE,
                // 单生产者模式
                ProducerType.SINGLE,
                new BlockingWaitStrategy()
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
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        for (long value = 0; true; value++) {
            // 生产者生产数据
            producer.onData(byteBuffer.putLong(0, value));

            // 模拟发送间隔
            Thread.sleep(1000);
        }

        // 7. 关闭 Disruptor (如果循环结束的话)
        //disruptor.shutdown();
    }

}
