package com.toy.modulithdemo.coupon;

import com.toy.modulithdemo.coupon.constant.CouponErrorCode;
import com.toy.modulithdemo.coupon.exception.CouponException;
import com.toy.modulithdemo.order.port.CouponPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Component
public class CouponAdapter implements CouponPort {

    private final CouponRepository couponRepository;


    @Transactional(readOnly = true)
    public BigDecimal calculateDiscountPrice(Long couponId, BigDecimal originalPrice) {
        if (couponId == null) {
            return originalPrice;
        }

        Coupon coupon = couponRepository.findById(couponId).orElseThrow(
                () -> new CouponException(CouponErrorCode.NOT_FOUND)
        );


        return coupon.calculateFinalPrice(originalPrice);

    }
}
