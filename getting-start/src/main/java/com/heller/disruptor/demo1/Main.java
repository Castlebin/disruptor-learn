package com.heller.disruptor.demo1;

import java.nio.ByteBuffer;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;

public class Main {
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
        disruptor.handleEventsWith(new LongEventHandler());

        // 3. 启动 Disruptor
        disruptor.start();

        // 4. 获取 Ring Buffer
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();

        // 5. 创建生产者
        LongEventProducer producer = new LongEventProducer(ringBuffer);

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
