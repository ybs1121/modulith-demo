package com.toy.modulithdemo.order.port;

import java.math.BigDecimal;

public interface CouponPort {
    BigDecimal calculateDiscountPrice(Long couponId, BigDecimal originalPrice);
}
