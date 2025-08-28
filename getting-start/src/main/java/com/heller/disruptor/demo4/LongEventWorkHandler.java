package com.heller.disruptor.demo4;

import com.heller.disruptor.demo1.LongEvent;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

/**
 * 事件处理器
 * 实现了 WorkHandler 接口，适用于工作池模式。即多个消费者实例可以并行处理事件，每个事件只会被其中一个消费者消费处理。
 */
public class LongEventWorkHandler implements WorkHandler<LongEvent> {


    /**
     * Callback to indicate a unit of work needs to be processed.
     *
     * @param event published to the {@link RingBuffer}
     * @throws Exception if the {@link WorkHandler} would like the exception handled further up the chain.
     */
    @Override
    public void onEvent(LongEvent event) throws Exception {
        System.out.println("Event: " + event.getValue());
    }

}
