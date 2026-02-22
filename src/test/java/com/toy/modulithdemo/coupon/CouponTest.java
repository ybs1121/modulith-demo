package com.toy.modulithdemo.coupon;

import com.toy.modulithdemo.coupon.constant.CouponType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class CouponTest {

    Long userKey = 1L;
    Long promotionId = 1L;

    @Test
    void 비율할인_최종금액() {
        Coupon coupon = new Coupon(userKey, promotionId, CouponType.RATE, BigDecimal.valueOf(10),
                BigDecimal.ZERO, null);

        // 20000원 × 10% 할인 = 18000원
        assertThat(coupon.calculateFinalPrice(BigDecimal.valueOf(20000)))
                .isEqualTo(BigDecimal.valueOf(18000));
    }

    @Test
    void 비율할인_최소금액미만() {
        Coupon coupon = new Coupon(userKey, promotionId, CouponType.RATE, BigDecimal.valueOf(10),
                BigDecimal.valueOf(10000), null);

        // 5000 < 10000 → 원가 그대로
        assertThat(coupon.calculateFinalPrice(BigDecimal.valueOf(5000)))
                .isEqualTo(BigDecimal.valueOf(5000));
    }

    @Test
    void 금액할인_최종금액() {
        Coupon coupon = new Coupon(userKey, promotionId, CouponType.AMOUNT, BigDecimal.valueOf(5000),
                BigDecimal.ZERO, null);

        // 20000 - 5000 = 15000
        assertThat(coupon.calculateFinalPrice(BigDecimal.valueOf(20000)))
                .isEqualTo(BigDecimal.valueOf(15000));
    }

    @Test
    void 금액할인_최대제한() {
        Coupon coupon = new Coupon(userKey, promotionId, CouponType.AMOUNT, BigDecimal.valueOf(5000),
                BigDecimal.ZERO, BigDecimal.valueOf(3000));

        // 5000 할인이지만 최대 3000까지만
        // 20000 - 3000 = 17000
        assertThat(coupon.calculateFinalPrice(BigDecimal.valueOf(20000)))
                .isEqualTo(BigDecimal.valueOf(17000));
    }

}