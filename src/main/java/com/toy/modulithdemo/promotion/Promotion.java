package com.toy.modulithdemo.promotion;

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
            throw new IllegalArgumentException("쿠폰이 모두 소진되었습니다.");
        }
        this.remainQuantity--;
    }

    public static Promotion create(String name, Long totalQuantity, Long remainQuantity) {
        return new Promotion(null, name, totalQuantity, remainQuantity);
    }
}
