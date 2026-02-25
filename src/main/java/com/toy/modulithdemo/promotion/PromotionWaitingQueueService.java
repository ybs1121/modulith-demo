package com.toy.modulithdemo.promotion;

import com.toy.modulithdemo.shared.QueueStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PromotionWaitingQueueService {

    private final StringRedisTemplate stringRedisTemplate;

    // 1. 대기열 등록 (Score는 현재 시간 밀리초)
    public void registerQueue(Long promotionId, Long userId) {
        String queueKey = "promotion:waiting:" + promotionId;
        long timestamp = System.currentTimeMillis();

        // ZADD: 시간순으로 정렬되도록 대기열에 추가
        stringRedisTemplate.opsForZSet().add(queueKey, String.valueOf(userId), timestamp);
    }

    // 2. 대기 상태 및 순번 조회
    public QueueStatusResponse getQueueStatus(Long promotionId, Long userKey) {
        String queueKey = "promotion:waiting:" + promotionId;
        String userPromotionKey = "promotion:users:" + promotionId; // Lua 스크립트에서 쓰는 발급 완료 Set 키
        String userStrKey = String.valueOf(userKey);

        // 1) 이미 발급 완료된 유저인지 확인 (Set에 있는지)
        Boolean isIssued = stringRedisTemplate.opsForSet().isMember(userPromotionKey, userStrKey);
        if (Boolean.TRUE.equals(isIssued)) {
            return new QueueStatusResponse("SUCCESS", 0L);
        }

        // 2) 발급 안됐다면 대기열 순위 확인
        Long rank = stringRedisTemplate.opsForZSet().rank(queueKey, userStrKey);

        if (rank != null) {
            // rank는 0부터 시작하므로 +1 해서 반환 (0번이면 앞에 0명 대기 중 = 1번째)
            return new QueueStatusResponse("WAITING", rank);
        }

        // 3) 대기열에도 없고 발급도 안됐다면 (예외 케이스 혹은 재고 소진 시 대기열 삭제됨)
        return new QueueStatusResponse("NOT_FOUND_OR_SOLD_OUT", -1L);
    }
}
