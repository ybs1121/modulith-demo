package com.toy.modulithdemo.coupon;

import com.toy.modulithdemo.coupon.constant.CouponType;
import com.toy.modulithdemo.shared.QueueStatusResponse;
import com.toy.modulithdemo.shared.annotation.DistributedLock;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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

    @Override
    public String issueFcfsCouponByRedis(Long userKey, Long promotionId) {
        return promotionPort.decreasePromotionRemainQuantityByRedis(promotionId, userKey);
    }

    @Override
    public void registerQueue(Long userKey, Long promotionId) {
        promotionPort.registerQueue(promotionId, userKey);
    }

    @Override
    public QueueStatusResponse getStatus(Long userKey, Long promotionId) {
        return promotionPort.getQueueStatus(promotionId, userKey);
    }

    @Override
    public Long issueCoupon(Long userKey, Long promotionId) {
        Coupon coupon = new Coupon(userKey, promotionId, CouponType.RATE, BigDecimal.valueOf(10),
                BigDecimal.ZERO, null);
        return couponRepository.save(coupon).getId();
    }

    @Override
    public Long issueAllCoupon(Set<Long> userKeys, Long promotionId) {
        List<Coupon> coupons = new ArrayList<>();
        for (Long userKey : userKeys) {
            Coupon coupon = new Coupon(userKey, promotionId, CouponType.RATE, BigDecimal.valueOf(10),
                    BigDecimal.ZERO, null);
            coupons.add(coupon);
        }

        couponRepository.saveAll(coupons);
        return 0L;
    }
}
