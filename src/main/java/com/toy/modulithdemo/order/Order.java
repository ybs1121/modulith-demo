package com.toy.modulithdemo.order;

import com.toy.modulithdemo.order.constant.DeliveryStatus;
import com.toy.modulithdemo.order.constant.OrderErrorCode;
import com.toy.modulithdemo.order.exception.OrderException;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;

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

    private BigDecimal originalPrice;

    private BigDecimal discountPrice;


    protected Order() {
    }

    public Order(Long productId, int count, BigDecimal originalPrice, BigDecimal discountPrice) {
        this.productId = productId;
        this.count = count;
        this.deliveryStatus = DeliveryStatus.PAYMENT_COMPLETED;
        this.originalPrice = originalPrice;
        this.discountPrice = discountPrice;
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

    public void startShipping() {
        this.deliveryStatus = DeliveryStatus.SHIPPING;
    }


    private void changeDeliveryStatus() {
        this.deliveryStatus = DeliveryStatus.CANCELLED;
    }

}