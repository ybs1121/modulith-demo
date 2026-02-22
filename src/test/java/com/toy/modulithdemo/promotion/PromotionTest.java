package com.toy.modulithdemo.promotion;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class PromotionTest {


    Promotion promotion;

    @BeforeEach
    void init() {
        promotion = Promotion.create("선착순 100명 할인 쿠폰", 100L, 100L);
    }


    @Test
    void decrease_promotion_remain_quantity_success() {
        //when
        promotion.decrease();

        //then
        Assertions.assertThat(promotion.getRemainQuantity()).isEqualTo(99L);
    }

}