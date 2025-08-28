package com.heller.disruptor.demo1;

import com.lmax.disruptor.EventHandler;

/**
 * 事件处理器
 * 事件处理器用于处理从环形缓冲区中消费的事件。它实现了 EventHandler 接口，并定义了如何处理每个事件。
 *
 * 在这个例子中，LongEventHandler 简单地打印出事件的值。
 */
public class LongEventHandler implements EventHandler<LongEvent> {

    @Override
    public void onEvent(LongEvent event, long sequence, boolean endOfBatch) throws Exception {
        System.out.println("Event: " + event.getValue());
    }

}
