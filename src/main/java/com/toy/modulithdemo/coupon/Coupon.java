package com.toy.modulithdemo.coupon;

import com.toy.modulithdemo.coupon.constant.CouponType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "Coupon")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    //    AMOUNT("AMOUNT", "금액 할인"),
    //    RATE("RATE","할인률")
    private CouponType couponType;

    private BigDecimal discountValue; // 할인 금액 OR 할인 퍼센테이지

    private BigDecimal minimumApplyAmount; // 최소 적용 금액

    private BigDecimal maxDiscountAmount;// 최대 할인 금액 (Null 이면 할인 제안 없다)


    public Coupon(CouponType couponType, BigDecimal discountValue,
                  BigDecimal minimumApplyAmount, BigDecimal maxDiscountAmount) {
        if (discountValue == null || discountValue.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("할인값은 양수여야 함");
        }
        if (minimumApplyAmount == null || minimumApplyAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("최소 금액은 0 이상이어야 함");
        }

        this.couponType = couponType;
        this.discountValue = discountValue;
        this.minimumApplyAmount = minimumApplyAmount;
        this.maxDiscountAmount = maxDiscountAmount;
    }


    public BigDecimal calculateFinalPrice(BigDecimal originalPrice) {

        if (originalPrice == null || originalPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("원가는 양수여야 함");
        }

        // 최소 금액 체크
        if (originalPrice.compareTo(this.minimumApplyAmount) < 0) {
            return originalPrice;  // 할인 불가 → 원가 그대로
        }


        BigDecimal discountAmount = couponType == CouponType.RATE
                ? calculateRateDiscount(originalPrice)
                : calculateAmountDiscount(originalPrice);

        // 최대 할인액 제한
        if (this.maxDiscountAmount != null) {
            discountAmount = discountAmount.min(this.maxDiscountAmount);
        }

        //  최종 결제액 반환
        return originalPrice.subtract(discountAmount);
    }

    private BigDecimal calculateRateDiscount(BigDecimal originalPrice) {
        BigDecimal rate = this.discountValue.divide(BigDecimal.valueOf(100),
                4, RoundingMode.HALF_UP);
        return originalPrice.multiply(rate).setScale(0, RoundingMode.DOWN);
    }

    private BigDecimal calculateAmountDiscount(BigDecimal originalPrice) {
        // 할인액이 원가를 초과하지 않도록
        return this.discountValue.min(originalPrice);
    }


}
