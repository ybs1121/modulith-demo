package com.toy.modulithdemo.order.exception;

import com.toy.modulithdemo.order.constant.OrderErrorCode;
import lombok.Getter;

@Getter
public class OrderException extends RuntimeException {
    private final OrderErrorCode errorCode;

    public OrderException(OrderErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public String getCode() {
        return errorCode.getCode();
    }

}
