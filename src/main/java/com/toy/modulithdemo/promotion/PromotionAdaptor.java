package com.toy.modulithdemo.promotion;

import com.toy.modulithdemo.coupon.PromotionPort;
import com.toy.modulithdemo.promotion.constant.PromotionErrorCode;
import com.toy.modulithdemo.promotion.exception.PromotionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class PromotionAdaptor implements PromotionPort {

    private final PromotionRepository promotionRepository;

    @Override
    public void decreasePromotionRemainQuantity(Long promotionId) {
//        Promotion promotion = promotionRepository.findById(promotionId).orElseThrow(() -> new PromotionException(PromotionErrorCode.NOT_FOUND));
//        Promotion promotion = promotionRepository.findByIdWithPessimisticLock(promotionId).orElseThrow(() -> new PromotionException(PromotionErrorCode.NOT_FOUND)); // 비관적 락
        Promotion promotion = promotionRepository.findByIdWithOptimisticLock(promotionId).orElseThrow(() -> new PromotionException(PromotionErrorCode.NOT_FOUND)); // 낙관적 락

        promotion.decrease();
    }
}
