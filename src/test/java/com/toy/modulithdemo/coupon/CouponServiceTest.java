package com.toy.modulithdemo.coupon;


import com.toy.modulithdemo.promotion.Promotion;
import com.toy.modulithdemo.promotion.PromotionRepository;
import org.aspectj.lang.annotation.Before;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(properties = {"app.scheduling.enable=false"}) // 스케줄러 완전 OFF

class CouponServiceTest {

    private static final Logger log = LoggerFactory.getLogger(CouponServiceTest.class);
    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private String PROMOTION_REDIS_PREFIX_KEY = "promotion:stock:";

    Promotion promotion;

    @BeforeEach
    void init() {
        promotion = promotionRepository.save(Promotion.create("테스트", 100L, 100L));
        stringRedisTemplate.opsForValue().set("promotion:stock:" + promotion.getId(), String.valueOf(promotion.getRemainQuantity()));
        stringRedisTemplate.delete("promotion:users:" + promotion.getId()); // 기존 유저 초기화
        stringRedisTemplate.delete("promotion:queue"); // 기존 큐 초기화
    }

    @AfterEach
    void tearDown() {
        // 다음 테스트를 위해 깔끔하게 정리
        stringRedisTemplate.delete("promotion:stock:" + promotion.getId());
        stringRedisTemplate.delete("promotion:users:" + promotion.getId());
        stringRedisTemplate.delete("promotion:queue");
    }


    @Test
    void 동시성_문제_발생_예상된_수량을_넘어서_쿠폰이_발급되어버린다() throws InterruptedException {

        // given
        int threadCount = 1000; // 1000명의 유저가 동시에 요청
        AtomicInteger exceptionCount = new AtomicInteger();


        // 멀티스레드 환경을 구성
        try (ExecutorService executorService = Executors.newFixedThreadPool(32)) {
            // 1000개의 요청이 모두 끝날 때까지 메인 스레드를 대기시키기 위한 장치
            CountDownLatch latch = new CountDownLatch(threadCount);


            // when
            for (int i = 0; i < threadCount; i++) {
                long userId = i;
                executorService.submit(() -> {
                    try {
                        couponService.issueFcfsCoupon(userId, 1L);
                    } catch (ObjectOptimisticLockingFailureException e) {
                        exceptionCount.incrementAndGet();

                    } finally {
                        latch.countDown();
                    }
                });
            }

            // 모든 스레드의 작업이 끝날 때까지 대기
            latch.await();


        }

        // then
        long count = couponRepository.count();
        System.out.println("실제 발급된 쿠폰 개수: " + count);

        // then
        System.out.println("발생한 낙관적 락 예외 횟수: " + exceptionCount.get());

        // 100개만 발급되기를 기대하지만 테스트는 실패
        assertThat(count).

                isEqualTo(100);

    }


    @Test
    void REDIS_LUA_SCRPIT를_통해서_쿠폰을_차감한다() {
        // GIVEN
        Long userKey = 1L;
        Long promotionId = promotion.getId();


        // WHEN
        couponService.issueFcfsCouponByRedis(userKey, promotionId);

        // THEN
        String remainQty = stringRedisTemplate.opsForValue().get(String.format("%s%s", PROMOTION_REDIS_PREFIX_KEY, promotionId));
        Assertions.assertThat(remainQty).isEqualTo("99");

    }


    @Test
    @DisplayName("1000명이 동시에 쿠폰 발급을 요청하면 딱 100명만 성공해야 한다.")
    void issueCoupon_Concurrency_Test() throws InterruptedException {
        // GIVEN
        // 32개의 스레드가 동시에 쏟아지도록 스레드 풀 생성 (가상 스레드 환경이면 Executors.newVirtualThreadPerTaskExecutor() 추천)
        try (ExecutorService executorService = Executors.newFixedThreadPool(32)) {
            // 1000개의 작업이 모두 끝날 때까지 메인 스레드를 기다리게 할 Latch
            CountDownLatch latch = new CountDownLatch(1000);

            // 성공 횟수와 실패 횟수를 스레드 안전하게 카운트
            AtomicInteger successCount = new AtomicInteger();
            AtomicInteger failCount = new AtomicInteger();

            // WHEN: 1000번의 요청을 스레드 풀에 던짐
            for (int i = 1; i <= 1000; i++) {
                final Long userId = (long) i; // 1번 유저부터 1000번 유저까지

                executorService.submit(() -> {
                    try {
                        // 방금 작성한 Redis Lua Script 기반의 초고속 API 호출
                        String code = couponService.issueFcfsCouponByRedis(userId, promotion.getId());
                        if (!"SUCCESS".equals(code)) {
                            throw new RuntimeException("실패");
                        }
                        successCount.incrementAndGet(); // 예외가 안 터지면 성공!
                    } catch (RuntimeException e) {
                        failCount.incrementAndGet(); // "소진되었습니다" 예외가 터지면 실패!
                    } finally {
                        latch.countDown(); // 작업 완료 신호
                    }
                });
            }

            // 1000개의 스레드 작업이 다 끝날 때까지 대기
            latch.await();

            // THEN: 결과 검증
            // 1. 성공한 사람은 딱 100명이어야 한다.
            assertThat(successCount.get()).isEqualTo(100);

            // 2. 실패한 사람은 900명이어야 한다.
            assertThat(failCount.get()).isEqualTo(900);

            // 3. Redis에 남은 재고는 정확히 0이어야 한다.
            String remainStock = stringRedisTemplate.opsForValue().get("promotion:stock:" + promotion.getId());
            assertThat(remainStock).isEqualTo("0");

            // 4. 발급 성공한 유저 Set(중복 방지)의 크기도 100이어야 한다.
            Long userSetSize = stringRedisTemplate.opsForSet().size("promotion:users:" + promotion.getId());
            assertThat(userSetSize).isEqualTo(100L);

            // 5. DB 저장 대기열(Queue)에 들어간 데이터도 딱 100개여야 한다.
            Long queueSize = stringRedisTemplate.opsForList().size("promotion:queue");
            assertThat(queueSize).isEqualTo(100L);
        }
    }
}
