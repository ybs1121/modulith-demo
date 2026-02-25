package com.toy.modulithdemo.coupon;

import com.toy.modulithdemo.shared.QueueStatusResponse;

import java.util.Set;

public interface CouponService {

    Long issueFcfsCoupon(Long userKey, Long promotionId);

    String issueFcfsCouponByRedis(Long userKey, Long promotionId);

    void registerQueue(Long userKey, Long promotionId);

    QueueStatusResponse getStatus(Long userKey, Long promotionId);

    Long issueCoupon(Long userKey, Long promotionId);
    Long issueAllCoupon(Set<Long> userKey, Long promotionId);
}
