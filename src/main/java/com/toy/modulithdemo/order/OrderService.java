package com.toy.modulithdemo.order;


import com.toy.modulithdemo.order.constant.OrderErrorCode;
import com.toy.modulithdemo.order.exception.OrderException;
import com.toy.modulithdemo.order.port.CouponPort;
import com.toy.modulithdemo.order.port.ProductPort;
import com.toy.modulithdemo.shared.event.OrderCancelEvent;
import com.toy.modulithdemo.shared.event.ProductUsedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    private final CouponPort couponPort;
    private final ProductPort productPort;

    public Order create(Long productId, int count, Long couponId) {

        BigDecimal originalPrice = productPort.getProductPrice(productId);

        BigDecimal discountPrice = couponPort.calculateDiscountPrice(couponId, originalPrice);

        Order order = new Order(productId, count, originalPrice, discountPrice);
        Order saved = orderRepository.save(order);

        // 주문이 생성되면 이벤트 발행
        eventPublisher.publishEvent(new ProductUsedEvent(productId, count));

        return saved;
    }

    public Order cancel(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new OrderException(OrderErrorCode.NOT_FOUND)
        );


        Order cancelOrder = orderRepository.save(order.cancel());

        eventPublisher.publishEvent(new OrderCancelEvent(cancelOrder.getProductId(), cancelOrder.getCount()));
        return cancelOrder;

    }
}