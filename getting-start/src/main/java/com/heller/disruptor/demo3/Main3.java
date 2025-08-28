package com.heller.disruptor.demo3;

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

// 多个消费者，每个消费者都消费所有的事件
public class Main3 {
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
        // 多个消费者，每个消费者都消费所有的事件。使用的是 handleEventsWith 方法
        disruptor.handleEventsWith(new LongEventHandler(), new LongEventHandler());

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
