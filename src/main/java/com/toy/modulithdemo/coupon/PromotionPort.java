package com.toy.modulithdemo.coupon;

public interface PromotionPort {
    void decreasePromotionRemainQuantity(Long promotionId);
    String decreasePromotionRemainQuantityByRedis(Long promotionId, Long userKey);
}
