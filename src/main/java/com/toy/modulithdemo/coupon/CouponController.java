package com.toy.modulithdemo.coupon;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/coupons")
public class CouponController {

    private final CouponService couponService;

    // 동시성 이슈 제어 테스트

    @PostMapping("/fcfs")
    public Long issueFcfsCoupon(@RequestParam Long userKey,
                                @RequestParam Long promotionId) {
        return couponService.issueFcfsCoupon(userKey, promotionId);
    }
}