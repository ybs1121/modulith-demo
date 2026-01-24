package com.toy.modulithdemo.product;

import com.toy.modulithdemo.shared.event.OrderCancelEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrderCancelledEventHandler {

    private final ProductService productService;

    public OrderCancelledEventHandler(ProductService productService) {
        this.productService = productService;
    }

    // 동기 + 같은 트랜잭션
    // 이벤트 리스너에서 실패 시 발행자도 롤백

    @EventListener
    @Transactional
    public void on(OrderCancelEvent event) {
        productService.increaseStock(event.getProductId(), event.getCount());
    }

    // 비동기 + 다른 트랜잭션
    // 이벤트 리스너에서 실패 시 발행자도 롤백 X

//    @Async  //  비동기
//    @TransactionalEventListener // 새로운 트랜잭션
//    public void on(OrderCancelEvent event) {
//        productService.increaseStock(event.getProductId(), event.getCount());
//    }
}