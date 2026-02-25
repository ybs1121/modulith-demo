package com.toy.modulithdemo.coupon;

import com.toy.modulithdemo.shared.QueueStatusResponse;

public interface PromotionPort {
    void decreasePromotionRemainQuantity(Long promotionId);
    String decreasePromotionRemainQuantityByRedis(Long promotionId, Long userKey);
    void registerQueue(Long promotionId, Long userId);
    QueueStatusResponse getQueueStatus(Long promotionId, Long userKey);
}
