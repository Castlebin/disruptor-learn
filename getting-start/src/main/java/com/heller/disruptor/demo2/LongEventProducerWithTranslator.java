package com.heller.disruptor.demo2;

import java.nio.ByteBuffer;

import com.heller.disruptor.demo1.LongEvent;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;

/**
 * 事件生产者也可以这么写
 * 事件生产者（使用 EventTranslator）
 * EventTranslator 用于简化事件发布的过程。它允许你定义一个翻译器，将输入数据转换为事件对象，而不需要手动处理序列号的获取和发布。
 */
public class LongEventProducerWithTranslator {

    private static final EventTranslatorOneArg<LongEvent, ByteBuffer> TRANSLATOR =
            new EventTranslatorOneArg<LongEvent, ByteBuffer>() {
                @Override
                public void translateTo(LongEvent event, long sequence, ByteBuffer buffer) {
                    event.setValue(buffer.getLong(0));
                }
            };

    private final RingBuffer<LongEvent> ringBuffer;

    public LongEventProducerWithTranslator(RingBuffer<LongEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    /**
     * onData 用来发布事件，每调用一次就发布一次事件
     * 使用 EventTranslator 来发布事件，不用手动处理序列号
     */
    public void onData(ByteBuffer buffer) {
        ringBuffer.publishEvent(TRANSLATOR, buffer);
    }

}
