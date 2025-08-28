package com.heller.disruptor.demo1;

import java.nio.ByteBuffer;

import com.lmax.disruptor.RingBuffer;

/**
 * 事件生产者
 * 事件生产者负责将数据发布到 Disruptor 的环形缓冲区中。它通常与一个或多个消费者协同工作，消费者从环形缓冲区中消费事件并进行处理。
 *
 * 在这个例子中，LongEventProducer 将 long 类型的值发布到 Disruptor 中。
 */
public class LongEventProducer {

    /**
     * RingBuffer 环形缓冲区
     */
    private final RingBuffer<LongEvent> ringBuffer;

    public LongEventProducer(RingBuffer<LongEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    /**
     * onData 用来发布事件，每调用一次就发布一次事件
     * 它的参数会用过事件传递给消费者
     */
    public void onData(ByteBuffer byteBuffer) {
        // 获取下一个可用的序列号
        long sequence = ringBuffer.next();
        try {
            // 通过序列号获取事件对象
            LongEvent event = ringBuffer.get(sequence);
            // 设置事件对象的值
            long value = byteBuffer.getLong(0);
            event.setValue(value);
        } finally {
            // 发布事件，发布后才能消费发布事件，发布后才能消费
            // 注意，最后的 ringBuffer.publish 方法必须包含在  finally 中以确保必须得到调用；
            // 如果某个请求的 sequence 未被提交，将会堵塞后续的发布操作或者其它的 producer。
            ringBuffer.publish(sequence);
        }
    }

}
