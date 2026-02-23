package com.toy.modulithdemo.coupon;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class PromotionLockFacade {

    private final RedissonClient redissonClient;
    private final PromotionPort promotionPort; // 위에서 만든 실제 비즈니스 서비스

    public void decreaseWithLock(Long promotionId) {
        // 1. 락 이름(Key) 설정 - 프로모션 ID별로 고유한 락을 생성하여 병목 현상 최소화
        String lockKey = "lock:promotion:" + promotionId;
        RLock lock = redissonClient.getLock(lockKey); // Pub/Sub 기반의 RLock 객체 반환

        try {
            // 2. 락 획득 시도
            // waitTime(10초): 락을 얻기 위해 기다리는 최대 시간
            // leaseTime(3초): 락을 획득한 후 점유하는 최대 시간 (서버 다운 시 데드락 방지)
            boolean isLocked = lock.tryLock(10, 3, TimeUnit.SECONDS);

            if (!isLocked) {
                log.info("프로모션 재고 감소 락 획득 실패 - 대기 시간 초과");
                throw new RuntimeException("현재 요청이 많아 처리가 지연되고 있습니다.");
            }

            // 3. 락 획득 성공 시 실제 비즈니스 로직(트랜잭션) 호출
            promotionPort.decreasePromotionRemainQuantity(promotionId);

        } catch (InterruptedException e) {
            log.error("락 획득 중 인터럽트 발생", e);
            Thread.currentThread().interrupt(); // 스레드 상태 복구
            throw new RuntimeException("재고 감소 처리 중 오류가 발생했습니다.");
        } finally {
            // 4. 로직이 끝난 후 락 해제
            // 현재 스레드가 락을 쥐고 있는 상태인지 확인 후 안전하게 해제
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}