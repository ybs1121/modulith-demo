package com.toy.modulithdemo.coupon.constant;



import lombok.Getter;

import java.util.Arrays;

@Getter
public enum CouponType {
    AMOUNT("AMOUNT", "금액 할인"),
    RATE("RATE","할인률")
    ;

    private final String code;
    private final String description;

    CouponType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    // 코드로 Enum 찾기 (JPA Converter 등에서 사용)
    public static CouponType ofCode(String code) {
        return Arrays.stream(CouponType.values())
                .filter(v -> v.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰 타입 코드입니다: " + code));
    }
    }
