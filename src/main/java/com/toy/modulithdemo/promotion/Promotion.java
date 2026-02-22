package com.toy.modulithdemo.promotion;

import com.toy.modulithdemo.promotion.constant.PromotionErrorCode;
import com.toy.modulithdemo.promotion.exception.PromotionException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Promotion {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private Long totalQuantity;    // 총 수량
    private Long remainQuantity;   // 잔여 수량

    // 재고 차감 비즈니스 로직
    public void decrease() {
        if (this.remainQuantity <= 0) {
            throw new PromotionException(PromotionErrorCode.ALL_USED_UP);
        }
        this.remainQuantity--;
    }

    public static Promotion create(String name, Long totalQuantity, Long remainQuantity) {
        return new Promotion(null, name, totalQuantity, remainQuantity);
    }
}
