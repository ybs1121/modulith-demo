package com.toy.modulithdemo.coupon;

public interface CouponService {

    Long issueFcfsCoupon(Long userKey, Long promotionId);

    String issueFcfsCouponByRedis(Long userKey, Long promotionId);
}
