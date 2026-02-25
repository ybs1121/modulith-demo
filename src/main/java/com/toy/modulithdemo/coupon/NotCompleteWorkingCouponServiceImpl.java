package com.toy.modulithdemo.coupon;

import com.toy.modulithdemo.coupon.constant.CouponErrorCode;
import com.toy.modulithdemo.coupon.constant.CouponType;
import com.toy.modulithdemo.coupon.exception.CouponException;
import com.toy.modulithdemo.shared.QueueStatusResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class NotCompleteWorkingCouponServiceImpl implements CouponService {


    private final CouponRepository couponRepository;

    //  선착순 쿠폰을 발급해 준다.
    @Override
    @Transactional
    public Long issueFcfsCoupon(Long userKey, Long promotionId) {

        Long promotionTargetRemainCount = Promotion.getPromotionTargetRemainCount(promotionId);

        if (promotionTargetRemainCount <= 0L) {
            // 마감 얼리리턴
            return -1L;
        }

        Coupon coupon = new Coupon(1L, 1L, CouponType.RATE, BigDecimal.valueOf(10),
                BigDecimal.ZERO, BigDecimal.valueOf(100_000L));

        Coupon saveCoupon = couponRepository.save(coupon);

        Promotion.reducePromotionTargetRemainCount(promotionId);

        return saveCoupon.getId();
    }

    @Override
    public String issueFcfsCouponByRedis(Long userKey, Long promotionId) {
        throw new CouponException(CouponErrorCode.NOT_SUPPORT);
    }

    @Override
    public void registerQueue(Long userKey, Long promotionId) {
        throw new CouponException(CouponErrorCode.NOT_SUPPORT);
    }

    @Override
    public QueueStatusResponse getStatus(Long userKey, Long promotionId) {
        throw new CouponException(CouponErrorCode.NOT_SUPPORT);
    }

    @Override
    public Long issueCoupon(Long userKey, Long promotionId) {
        throw new CouponException(CouponErrorCode.NOT_SUPPORT);
    }

    @Override
    public Long issueAllCoupon(Set<Long> userKey, Long promotionId) {
        throw new CouponException(CouponErrorCode.NOT_SUPPORT);
    }


    @Component
    public static class Promotion {
        private static Map<Long, Long> data;

        @PostConstruct
        void init() {
            data = new HashMap<>();
            data.put(1L, 100L);
        }

        public static Long getPromotionTargetRemainCount(Long promotionId) {
            return data.getOrDefault(promotionId, 0L);
        }

        public static void reducePromotionTargetRemainCount(Long promotionId) {
            data.put(promotionId, getPromotionTargetRemainCount(promotionId) - 1L);
        }
    }
}
