package com.toy.modulithdemo.order;

import com.toy.modulithdemo.order.constant.DeliveryStatus;
import com.toy.modulithdemo.order.dto.OrderStatusEvent;
import com.toy.modulithdemo.order.publisher.OrderStatusPublisher;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderStatusService {

    private final OrderRepository orderRepository;


    private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    // SSE 구독
    public SseEmitter subscribe(Long orderId) {
        SseEmitter emitter = new SseEmitter(300000L);

        emitters.computeIfAbsent(orderId, k -> new CopyOnWriteArrayList<>())
                .add(emitter);

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

    // 배송 상태 변경
    public void updateOrderStatus(Long orderId, DeliveryStatus newStatus) {
        // DB 저장
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        order.changeDeliveryStatus(newStatus);
        orderRepository.save(order);

        // 구독 중인 클라이언트에 알림
        notifyStatusChange(orderId, newStatus);
    }

    // 구독자에게 알림
    private void notifyStatusChange(Long orderId, DeliveryStatus status) {
        List<SseEmitter> emitterList = emitters.get(orderId);
        if (emitterList == null || emitterList.isEmpty()) {
            return;
        }

        OrderStatusEvent event = new OrderStatusEvent(
                orderId,
                status,
                "배송 상태: " + status,
                LocalDateTime.now()
        );

        emitterList.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .id(String.valueOf(System.currentTimeMillis()))
                        .name("orderStatus")
                        .data(event)
                        .build());
            } catch (IOException e) {
                emitterList.remove(emitter);
            }
        });
    }
}