package com.toy.modulithdemo.order.dto;

import com.toy.modulithdemo.order.constant.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusEvent {
    private Long orderId;
    private DeliveryStatus deliveryStatus;
    private String message;
    private LocalDateTime timestamp;

    public static OrderStatusEvent of(Long orderId, DeliveryStatus deliveryStatus, String message) {
        return new OrderStatusEvent(
                orderId,
                deliveryStatus,
                message,
                LocalDateTime.now()
        );
    }
}

