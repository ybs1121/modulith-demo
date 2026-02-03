package com.toy.modulithdemo.order;

import com.toy.modulithdemo.order.constant.DeliveryStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderStatusService orderStatusService;


    @PostMapping
    public Order create(@RequestParam Long productId,
                        @RequestParam int count,
                        @RequestParam Long couponId) {
        return orderService.create(productId, count, couponId);
    }


    // SSE 구독
    @GetMapping("/subscribe/{orderId}")
    public SseEmitter subscribe(@PathVariable Long orderId) {
        return orderStatusService.subscribe(orderId);
    }

    //배송 상태 변경
    @PostMapping("/{orderId}/status")
    public void updateStatus(@PathVariable Long orderId, @RequestParam DeliveryStatus deliveryStatus) {
        orderStatusService.updateOrderStatus(orderId, deliveryStatus);
    }


}