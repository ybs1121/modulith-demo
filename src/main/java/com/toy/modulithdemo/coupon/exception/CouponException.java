package com.toy.modulithdemo.coupon.exception;

import com.toy.modulithdemo.coupon.constant.CouponErrorCode;
import com.toy.modulithdemo.order.constant.OrderErrorCode;
import lombok.Getter;

@Getter
public class CouponException extends RuntimeException {
    private final CouponErrorCode errorCode;

    public CouponException(CouponErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public String getCode() {
        return errorCode.getCode();
    }

}
