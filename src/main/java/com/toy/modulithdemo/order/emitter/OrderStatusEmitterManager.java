package com.toy.modulithdemo.order.emitter;

import com.toy.modulithdemo.order.dto.OrderStatusEvent;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface OrderStatusEmitterManager {
    SseEmitter subscribe(Long orderId);

    void notifyAll(OrderStatusEvent event);
}