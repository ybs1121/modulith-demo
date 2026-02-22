package com.toy.modulithdemo.coupon;

import com.toy.modulithdemo.coupon.constant.CouponType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Primary
@RequiredArgsConstructor
@Service
public class CouponServiceImpl implements CouponService {

    private final PromotionPort promotionPort;

    private final CouponRepository couponRepository;

    @Override
    public Long issueFcfsCoupon(Long userKey, Long promotionId) {

        promotionPort.decreasePromotionRemainQuantity(promotionId);

        // Todo : 프로모션에서 coupon에 대한 할인 정책을 가지고 있는게 맞는 거 같아서 추후 수정해야한다. 테스트와는 별개이기 때문에 진행
        Coupon coupon = new Coupon(userKey, promotionId, CouponType.RATE, BigDecimal.valueOf(10),
                BigDecimal.ZERO, null);

        return couponRepository.save(coupon).getId();
    }
}
