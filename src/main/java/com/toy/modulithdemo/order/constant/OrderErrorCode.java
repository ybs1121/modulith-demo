package com.toy.modulithdemo.order.constant;

import lombok.Getter;

@Getter
public enum OrderErrorCode {
    ALREADY_CANCELLED("ORD00001", "이미 취소된 주문입니다"),
    NOT_PAID("ORD00002", "결제 완료 상태에서만 취소할 수 있습니다"),
    NOT_ALLOWED_STATUS("ORD00003", "현재 상태에서는 취소할 수 없습니다"),
    NOT_FOUND("ORD00004", "주문을 찾을 수 없습니다."),

    ;

    private final String code;
    private final String message;

    OrderErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }


}
