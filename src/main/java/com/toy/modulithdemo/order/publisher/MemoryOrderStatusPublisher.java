package com.toy.modulithdemo.order.publisher;

import com.toy.modulithdemo.order.dto.OrderStatusEvent;
import com.toy.modulithdemo.order.emitter.OrderStatusEmitterManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class MemoryOrderStatusPublisher implements OrderStatusPublisher {

    @Qualifier("memoryOrderStatusEmitterManager")
    private final OrderStatusEmitterManager emitterManager;

    @Override
    public void publish(OrderStatusEvent event) {
        log.info("[메모리] 배송 상태 발행: {}", event);
        emitterManager.notifyAll(event);
    }
}