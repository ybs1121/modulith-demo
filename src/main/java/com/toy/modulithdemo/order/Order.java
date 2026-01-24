package com.toy.modulithdemo.order;

import com.toy.modulithdemo.order.constant.DeliveryStatus;
import com.toy.modulithdemo.order.exception.OrderException;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "orders") // order가 예약어일 수 있어 테이블명은 orders로
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private int count;

    private DeliveryStatus deliveryStatus;


    protected Order() {
    }

    public Order(Long productId, int count) {
        this.productId = productId;
        this.count = count;
        this.deliveryStatus = DeliveryStatus.PAYMENT_COMPLETED;
    }


    public Order cancel() {

        if (this.getDeliveryStatus() != DeliveryStatus.PAYMENT_COMPLETED) {
            throw new OrderException("주문상태가 결제 완료일 때만 취소할 수 있습니다.");
        }

        changeDeliveryStatus();
        return this;
    }


    private void changeDeliveryStatus() {
        this.deliveryStatus = DeliveryStatus.CANCELLED;
    }

}