package com.toy.modulithdemo.promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class PromotionWaitingQueueWorker {

    private final StringRedisTemplate stringRedisTemplate;
    private final PromotionRedisRepository promotionRedisRepository;
    private final PromotionRepository promotionRepository;


    @Scheduled(fixedDelay = 1000)
    public void processWaitingQueue() {
        // 1. 진행 중인 프로모션 목록을 DB에서 모두 가져옵니다.
        List<Promotion> activePromotions = promotionRepository.findAll();
        log.info("스캐줄러 진입 프로모션 : {} 개", activePromotions.size());

        for (Promotion promotion : activePromotions) {
            Long promotionId = promotion.getId();
            String queueKey = "promotion:waiting:" + promotionId;

            // 2. 대기열에 있는 '전체' 유저를 한 번에 조회합니다.
//            Set<String> waitingUsers = stringRedisTemplate.opsForZSet().range(queueKey, 0, -1);
            Set<String> waitingUsers = stringRedisTemplate.opsForZSet().range(queueKey, 0, 999);

            if (waitingUsers == null || waitingUsers.isEmpty()) {
                continue; // 해당 프로모션의 대기열이 비어있으면 다음 프로모션으로 넘어감
            }

            log.info("프로모션 {} 대기열 전체 처리 시작 - 대기 인원: {}명", promotionId, waitingUsers.size());

            // 삭제할 유저들을 모아둘 리스트
            List<String> processedUsers = new ArrayList<>();

            // 3. 전체 유저에 대해 순차적으로 쿠폰 발급/탈락 검증 진행
            for (String userIdStr : waitingUsers) {
                Long userId = Long.valueOf(userIdStr);

                // 기존에 만든 Lua 스크립트를 통해 재고 차감 및 중복(탈락) 검증
                String result = promotionRedisRepository.issue(promotionId, userId);

                // 성공이든 중복(탈락)이든 처리가 끝났으므로 삭제 목록에 추가
                processedUsers.add(userIdStr);

                if ("SOLD_OUT".equals(result)) {
                    log.info("프로모션 {} 재고 소진. 남은 대기열 인원들은 모두 탈락 처리(초기화)합니다.", promotionId);
                    // 큐 자체를 날려버려서 남은 사람들은 API 호출 시 'NOT_FOUND_OR_SOLD_OUT'을 받게 함
                    stringRedisTemplate.delete(queueKey);
                    break; // 해당 프로모션 처리를 즉시 중단
                }
                else if ("DUPLICATED".equals(result)) {
                    log.info("유저 {}는 이미 발급받아 대기열에서 탈락 처리되었습니다.", userId);
                }
                else {
                    log.info("유저 {} 쿠폰 발급 성공.", userId);
                }
            }

            // 4. 발급 성공 및 중복으로 탈락한 유저들을 대기열에서 일괄 제거
            // (SOLD_OUT으로 큐가 이미 삭제된 경우가 아닐 때만 실행)
            if (!processedUsers.isEmpty() && Boolean.TRUE.equals(stringRedisTemplate.hasKey(queueKey))) {
                stringRedisTemplate.opsForZSet().remove(queueKey, processedUsers.toArray());
            }
        }
    }
}