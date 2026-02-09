package com.toy.modulithdemo.order;

import com.toy.modulithdemo.order.constant.DeliveryStatus;
import com.toy.modulithdemo.shared.sse.constant.NotificationType;
import com.toy.modulithdemo.shared.sse.dto.NotificationEvent;
import com.toy.modulithdemo.shared.sse.emitter.SseEmitterManager;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;


@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderStatusService orderStatusService;

    private final SseEmitterManager sseEmitterManager;


    @PostMapping
    public Order create(@RequestParam Long productId,
                        @RequestParam int count,
                        @RequestParam Long couponId,
                        @RequestParam Long userKey) {
        return orderService.create(productId, count, couponId, userKey);
    }


    // SSE 구독
    @GetMapping("/subscribe/{orderId}")
    public SseEmitter subscribe(@PathVariable Long orderId) {
        return orderStatusService.subscribe(orderId);
    }

    //배송 상태 변경
    @PostMapping("/{orderId}/status")
    public void updateStatus(@PathVariable Long orderId,
                             @RequestParam DeliveryStatus deliveryStatus,
                             @RequestParam Long userKey) {
        orderStatusService.updateOrderStatus(orderId, deliveryStatus);
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("orderId", orderId);
        payload.put("deliveryStatus", deliveryStatus);
        sseEmitterManager.notifyAll(NotificationEvent.create(userKey, NotificationType.ORDER, payload));
    }


}