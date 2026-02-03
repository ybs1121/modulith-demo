package com.toy.modulithdemo.order.emitter;

import com.toy.modulithdemo.order.dto.OrderStatusEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Slf4j
public class MemoryOrderStatusEmitterManager implements OrderStatusEmitterManager {

    private final Map<Long, CopyOnWriteArrayList<SseEmitter>> emitters =
            new ConcurrentHashMap<>();

    @Override
    public SseEmitter subscribe(Long orderId) {
        SseEmitter emitter = new SseEmitter(300000L); // 5분 타임아웃

        emitters.computeIfAbsent(orderId, k -> new CopyOnWriteArrayList<>())
                .add(emitter);

        log.info("[메모리] 주문 {} 구독 시작", orderId);

        try {
            emitter.send(SseEmitter.event()
                    .id(String.valueOf(System.currentTimeMillis()))
                    .name("connect")
                    .data("배송 상태 모니터링 시작")
                    .build());
        } catch (IOException e) {
            emitter.completeWithError(e);
        }

        emitter.onCompletion(() -> emitters.get(orderId).remove(emitter));
        emitter.onTimeout(() -> emitters.get(orderId).remove(emitter));
        emitter.onError(throwable -> emitters.get(orderId).remove(emitter));

        return emitter;
    }

    @Override
    public void notifyAll(OrderStatusEvent event) {
        List<SseEmitter> emitterList = emitters.get(event.getOrderId());
        if (emitterList == null || emitterList.isEmpty()) {
            return;
        }

        log.info("[메모리] 주문 {} 상태 변경 알림: {}", event.getOrderId(), event.getDeliveryStatus());

        emitterList.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .id(String.valueOf(System.currentTimeMillis()))
                        .name("orderStatus")
                        .data(event)
                        .build());
            } catch (IOException e) {
                log.error("Emitter 전송 실패", e);
                emitterList.remove(emitter);
            }
        });
    }
}
