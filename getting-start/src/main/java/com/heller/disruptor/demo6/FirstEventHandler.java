package com.heller.disruptor.demo6;

import com.heller.disruptor.demo1.LongEvent;
import com.lmax.disruptor.EventHandler;

public class FirstEventHandler implements EventHandler<LongEvent> {
    @Override
    public void onEvent(LongEvent event, long sequence, boolean endOfBatch) {
        long value = event.getValue();

        // 改变值，实现 +1
        event.setValue(value + 1);
        System.out.println("value=" + value + ", First Handler incremented value to: " + event.getValue());

        // 模拟一些处理耗时
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
