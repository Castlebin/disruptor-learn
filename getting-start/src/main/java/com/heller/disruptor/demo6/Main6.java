package com.heller.disruptor.demo6;

import java.nio.ByteBuffer;
import java.util.concurrent.Executors;

import com.heller.disruptor.demo1.LongEvent;
import com.heller.disruptor.demo1.LongEventFactory;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * Disruptor 允许你定义一个处理链，一个或多个消费者可以依赖于其他消费者的处理结果。
 * 这使得你可以构建复杂的、有向无环图（DAG）的事件处理流程。
 * <p>
 * 即任务编排
 * <p>
 * 任务编排 1 ： 串行处理 （消息 依次 被多个消费者处理，下一个消费者可以拿到上一个消费者的处理结果，实现链式处理）
 */
public class Main6 {
    public static void main(String[] args) throws Exception {
        int bufferSize = 1024;

        // 1. 创建 Disruptor 实例
        Disruptor<LongEvent> disruptor = new Disruptor<>(
                new LongEventFactory(),
                bufferSize,
                Executors.newCachedThreadPool(), // 使用线程池来运行事件处理器
                ProducerType.SINGLE,
                new BlockingWaitStrategy()
        );

        // 2. 定义处理依赖链
        // 第一个处理器
        disruptor.handleEventsWith(new FirstEventHandler())
                // 第二个处理器依赖于第一个处理器，在第一个处理器处理完后才开始
                .then(new SecondEventHandler());

        // 3. 启动 Disruptor
        disruptor.start();

        // 4. 获取 Ring Buffer
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();

        // 5. 模拟多个生产者发送数据
        // 使用一个简单的 lambda 表达式作为生产者
        Runnable producerTask = () -> {
            ByteBuffer bb = ByteBuffer.allocate(8);
            for (int i = 0; i < 5; i++) {
                bb.putLong(0, i);

                long sequence = ringBuffer.next();
                try {
                    LongEvent event = ringBuffer.get(sequence);
                    event.setValue(bb.getLong(0));
                } finally {
                    ringBuffer.publish(sequence);
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };

        // 启动生产者线程
        new Thread(producerTask).start();

        // 等待一段时间观察输出
        Thread.sleep(10000);

        // 6. 关闭 Disruptor
        disruptor.shutdown();
    }

}
