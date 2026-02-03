package com.toy.modulithdemo.order.publisher;

import com.toy.modulithdemo.order.dto.OrderStatusEvent;

public interface OrderStatusPublisher {
    void publish(OrderStatusEvent event);
}