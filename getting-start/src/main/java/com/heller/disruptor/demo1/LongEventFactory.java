package com.heller.disruptor.demo1;

import com.lmax.disruptor.EventFactory;

/**
 * 事件工厂
 * 事件工厂用于创建事件对象的实例。Disruptor 使用事件工厂来预先分配环形缓冲区中的事件对象，从而避免在运行时频繁地创建和销毁对象，减少垃圾回收的开销。
 *
 * 通过实现 EventFactory 接口，您可以定义如何创建事件对象的实例。在这个例子中，LongEventFactory 创建 LongEvent 对象。
 */
public class LongEventFactory implements EventFactory<LongEvent> {

    @Override
    public LongEvent newInstance() {
        return new LongEvent();
    }

}
