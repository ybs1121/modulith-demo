package com.toy.modulithdemo.promotion;

import com.toy.modulithdemo.coupon.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PromotionIssueWorker {

    private final StringRedisTemplate stringRedisTemplate;
    private final PromotionRepository promotionRepository;
    private final CouponService couponService;


    @Transactional
    @Scheduled(fixedDelay = 1000)
    public void processQueue() {
        // 1. 현재 큐에 쌓인 데이터를 전부 꺼냅니다 (Drain)
        List<String> values = new ArrayList<>();
        String value;
        while ((value = stringRedisTemplate.opsForList().leftPop("promotion:queue")) != null) {
            values.add(value);
        }

        if (values.isEmpty()) {
            return; // 처리할 데이터가 없으면 그냥 종료
        }

        // values = ["1:100", "1:101", "2:200", "1:102", "3:300"] (예시 데이터)

        Map<Long, List<Long>> userIdsByPromotion = values.stream()
                // 1. "promotionId:userId" 문자열을 분리하여 Map.Entry 객체로 변환
                .map(v -> {
                    String[] parts = v.split(":");
                    return new AbstractMap.SimpleEntry<>(
                            Long.parseLong(parts[0]), // Key: promotionId
                            Long.parseLong(parts[1])  // Value: userId
                    );
                })
                // 2. promotionId를 기준으로 그룹화하고, 값(userId)들을 List로 모음
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                ));


        // 3. promotionId 별로 DB 수량을 한 번에 감소
        userIdsByPromotion.forEach((promotionId, userKeys) -> {
            try {
                decreaseInBatch(promotionId, (long) userKeys.size());
                couponService.issueAllCoupon(new HashSet<>(userKeys), promotionId);


                log.info("Promotion {} - {}건 DB 저장 완료", promotionId, (long) userKeys.size());
            } catch (Exception e) {
                log.error("Promotion {} DB 저장 실패: {}", promotionId, e.getMessage());
                // 실패 시 다시 큐에 밀어 넣기 (재처리)
                values.stream()
                        .filter(v -> v.startsWith(promotionId + ":"))
                        .forEach(v -> stringRedisTemplate.opsForList().rightPush("promotion:queue", v));
            }
        });
    }


    public void decreaseInBatch(Long promotionId, Long count) {
        Promotion promotion = promotionRepository.findByIdWithPessimisticLock(promotionId)
                .orElseThrow(() -> new RuntimeException("프로모션을 찾을 수 없습니다."));

        // count만큼 한 번에 감소
        for (int i = 0; i < count; i++) {
            promotion.decrease();
        }
    }
}
