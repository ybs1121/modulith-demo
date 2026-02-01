package com.toy.modulithdemo.order;

import com.toy.modulithdemo.order.constant.DeliveryStatus;


import com.toy.modulithdemo.order.constant.OrderErrorCode;
import com.toy.modulithdemo.order.exception.OrderException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;


class OrderTest {


    Order order;

    @BeforeEach
    void init() {
        order = new Order(1L, 2, BigDecimal.valueOf(20000), BigDecimal.valueOf(20000));
    }

    @Test
    @DisplayName("결제완료 인 상태일때만 주문을 취소할 수 있다.")
    void cancelOrderSuccess() {
        Order cancel = order.cancel();
        Assertions.assertThat(cancel.getDeliveryStatus()).isEqualTo(DeliveryStatus.CANCELLED);
    }


    @Test
    @DisplayName("이미 취소된 주문은 다시 취소할 수 없다")
    void cancelOrderFail_AlreadyCancelled() {
        // 1. Given: 정상적으로 한 번 취소함
        order.cancel();
        // 이제 order의 상태는 CANCELLED 입니다.

        // 2. When & Then: 또 취소하려고 시도하면 예외 발생
        Assertions.assertThatThrownBy(() -> order.cancel())
                .isInstanceOf(OrderException.class)
                .hasMessage(OrderErrorCode.ALREADY_CANCELLED.getMessage());
    }


    @Test
    @DisplayName("배송 중인 상태에서는 취소할 수 없다")
    void cancelOrderFail_Shipping() {
        // 1. Given: 리플렉션으로 강제로 상태를 SHIPPING으로 변경
//        ReflectionTestUtils.setField(order, "deliveryStatus", DeliveryStatus.SHIPPING);

        // Public
        order.startShipping();

        // 2. When & Then
        Assertions.assertThatThrownBy(() -> order.cancel())
                .isInstanceOf(OrderException.class)
                .hasMessage(OrderErrorCode.NOT_PAID.getMessage());
    }
}