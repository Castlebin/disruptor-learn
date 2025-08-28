package com.heller.disruptor.demo1;

import lombok.Getter;
import lombok.Setter;

/**
 * 事件对象
 * 一个事件对象是一个数据载体，它将通过 Disruptor 在生产者和消费者之间传递。
 *
 * 事件对象是 POJO（Plain Old Java Object），它包含了应用程序需要传递的数据。
 *
 * 这个例子里的事件对象非常简单，只包含一个 long 类型的值。
 */
@Getter
@Setter
public class LongEvent {

    private long value;

}
