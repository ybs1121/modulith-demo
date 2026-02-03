package com.toy.modulithdemo.order.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum DeliveryStatus {

    // 1. 초기 상태
    PAYMENT_COMPLETED("PAY_DONE", "결제 완료"),
    PREPARING("PREP", "상품 준비 중"),

    // 2. 배송 진행
    SHIPPING("SHIP", "배송 중"),
    DELIVERED("DONE", "배송 완료"),

    // 3. 예외/종료
    CANCELLED("CNCL", "취소됨"),
    RETURNED("RTN", "반품"),

    ;

    @JsonValue
    private final String code;
    private final String description;

    DeliveryStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    // 코드로 Enum 찾기 (JPA Converter 등에서 사용)
    @JsonCreator
    public static DeliveryStatus ofCode(String code) {
        return Arrays.stream(DeliveryStatus.values())
                .filter(v -> v.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 배송 상태 코드입니다: " + code));
    }
}
