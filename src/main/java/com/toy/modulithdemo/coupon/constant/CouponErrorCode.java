package com.toy.modulithdemo.coupon.constant;

import lombok.Getter;

@Getter
public enum CouponErrorCode {
    NOT_FOUND("COU00001", "쿠폰을 찾을 수 없습니다."),


    ;

    private final String code;
    private final String message;

    CouponErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }


}
