package com.toy.modulithdemo.order;

import com.toy.modulithdemo.order.constant.DeliveryStatus;
import com.toy.modulithdemo.order.constant.OrderErrorCode;
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

        if (this.getDeliveryStatus() == DeliveryStatus.CANCELLED) {
            throw new OrderException(OrderErrorCode.ALREADY_CANCELLED);
        }

        if (this.getDeliveryStatus() != DeliveryStatus.PAYMENT_COMPLETED) {
            throw new OrderException(OrderErrorCode.NOT_PAID);
        }

        changeDeliveryStatus();
        return this;
    }

    public Order startShipping() {
        this.deliveryStatus = DeliveryStatus.SHIPPING;
        return this;
    }


    private void changeDeliveryStatus() {
        this.deliveryStatus = DeliveryStatus.CANCELLED;
    }

}