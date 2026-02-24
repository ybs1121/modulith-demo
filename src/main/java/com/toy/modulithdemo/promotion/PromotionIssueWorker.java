package com.toy.modulithdemo.promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PromotionIssueWorker {

    private final StringRedisTemplate stringRedisTemplate;
    private final PromotionRepository promotionRepository;

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

        // 2. promotionId 별로 count를 집계합니다
        // {"1" -> 37, "2" -> 12} 형태로 그룹화
        Map<Long, Long> countByPromotion = values.stream()
                .map(v -> Long.parseLong(v.split(":")[0]))
                .collect(Collectors.groupingBy(id -> id, Collectors.counting()));

        // 3. promotionId 별로 DB 수량을 한 번에 감소
        countByPromotion.forEach((promotionId, count) -> {
            try {
                decreaseInBatch(promotionId, count);
                log.info("Promotion {} - {}건 DB 저장 완료", promotionId, count);
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
