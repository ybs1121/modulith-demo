package com.toy.modulithdemo.coupon;

import com.toy.modulithdemo.shared.QueueStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // 1. 이벤트 참여 버튼 클릭 (대기열 진입)
    @PostMapping("/{promotionId}/join")
    public ResponseEntity<String> joinQueue(@PathVariable Long promotionId, @RequestParam Long userId) {
        couponService.registerQueue(userId, promotionId);
        return ResponseEntity.ok("대기열 진입 완료");
    }

    // 2. 프론트엔드에서 1~3초 주기로 계속 호출하여 순번 확인 (Polling)
    @GetMapping("/{promotionId}/status")
    public ResponseEntity<QueueStatusResponse> getStatus(@PathVariable Long promotionId, @RequestParam Long userId) {
        QueueStatusResponse status = couponService.getStatus(userId, promotionId);
        return ResponseEntity.ok(status);
    }
}