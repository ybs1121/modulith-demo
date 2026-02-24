package com.toy.modulithdemo.coupon;

import com.toy.modulithdemo.coupon.constant.CouponType;
import com.toy.modulithdemo.shared.annotation.DistributedLock;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Transactional
@Primary
@RequiredArgsConstructor
@Service
public class CouponServiceImpl implements CouponService {

    private final PromotionPort promotionPort;

    private final CouponRepository couponRepository;

    private final PromotionLockFacade promotionLockFacade;

    @DistributedLock(key = "coupon", value = "#promotionId", waitTime = 10L, leaseTime = 3L, timeUnit = TimeUnit.SECONDS)
    @Override
    public Long issueFcfsCoupon(Long userKey, Long promotionId) {

        promotionPort.decreasePromotionRemainQuantity(promotionId);


        Coupon coupon = new Coupon(userKey, promotionId, CouponType.RATE, BigDecimal.valueOf(10),
                BigDecimal.ZERO, null);
        return couponRepository.save(coupon).getId();
    }

    public String issueFcfsCouponByRedis(Long userKey, Long promotionId) {
        return promotionPort.decreasePromotionRemainQuantityByRedis(promotionId, userKey);
    }
}
