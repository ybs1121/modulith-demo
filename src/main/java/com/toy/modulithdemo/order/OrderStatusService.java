package com.toy.modulithdemo.order;

import com.toy.modulithdemo.order.constant.DeliveryStatus;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface OrderStatusService {


    SseEmitter subscribe(Long orderId);

    void updateOrderStatus(Long orderId, DeliveryStatus newStatus);


}
