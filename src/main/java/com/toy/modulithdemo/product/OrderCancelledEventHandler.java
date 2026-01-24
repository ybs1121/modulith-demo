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

    // 1. 동기 + 같은 트랜잭션
//    이벤트 리스너에서 실패 시 발행자도 롤백
    @EventListener
    @Transactional
    public void on(OrderCancelEvent event) {
        productService.increaseStock(event.getProductId(), event.getCount());
    }

//    // 2. 동기 + 트랜잭션 없음
////    이벤트 리스너에서 실패 시 발행자는 영향 없음, 원자성 없음
//    @EventListener
//    public void on(OrderCancelEvent event) {
//        productService.increaseStock(event.getProductId(), event.getCount());
//    }
//
//    // 3. 동기 + 다른 트랜잭션 (AFTER_COMMIT)
////    이벤트 리스너에서 실패 시 발행자는 롤백 X, 원자성 깨짐
//    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
//    public void on(OrderCancelEvent event) {
//        productService.increaseStock(event.getProductId(), event.getCount());
//    }
//
//    // 4. 비동기 + 다른 트랜잭션
////    이벤트 리스너에서 실패 시 발행자는 롤백 X,
//    @Async
//    @TransactionalEventListener
//    public void on(OrderCancelEvent event) {
//        productService.increaseStock(event.getProductId(), event.getCount());
//    }

}