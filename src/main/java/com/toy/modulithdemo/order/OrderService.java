package com.toy.modulithdemo.order;


import com.toy.modulithdemo.order.exception.OrderException;
import com.toy.modulithdemo.shared.event.OrderCancelEvent;
import com.toy.modulithdemo.shared.event.ProductUsedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;


    public Order create(Long productId, int count) {
        Order order = new Order(productId, count);
        Order saved = orderRepository.save(order);

        // 주문이 생성되면 이벤트 발행
        eventPublisher.publishEvent(new ProductUsedEvent(productId, count));

        return saved;
    }

    public Order cancel(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new OrderException("주문을 찾을 수 없습니다.")
        );


        Order cancelOrder = orderRepository.save(order.cancel());

        eventPublisher.publishEvent(new OrderCancelEvent(cancelOrder.getProductId(), cancelOrder.getCount()));
        return cancelOrder;

    }
}