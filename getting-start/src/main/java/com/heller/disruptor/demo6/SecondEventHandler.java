package com.heller.disruptor.demo6;

import com.heller.disruptor.demo1.LongEvent;
import com.lmax.disruptor.EventHandler;

public class SecondEventHandler implements EventHandler<LongEvent> {
    @Override
    public void onEvent(LongEvent event, long sequence, boolean endOfBatch) {
        long value = event.getValue();

        // 改变值，实现 *2
        event.setValue(value * 2);
        System.out.println("value=" + value + ", Second Handler incremented value to: " + event.getValue());
    }
}
