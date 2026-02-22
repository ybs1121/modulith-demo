package com.toy.modulithdemo.coupon;


import com.toy.modulithdemo.promotion.Promotion;
import com.toy.modulithdemo.promotion.PromotionRepository;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class CouponServiceTest {

    private static final Logger log = LoggerFactory.getLogger(CouponServiceTest.class);
    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private PromotionRepository promotionRepository;


    @BeforeEach
    void init() {
        promotionRepository.save(Promotion.create("테스트", 100L, 100L));
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

}