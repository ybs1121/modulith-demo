package com.toy.modulithdemo.promotion;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PromotionRedisRepository {

    private final StringRedisTemplate stringRedisTemplate;

    // 1. 재고 차감 및 중복 검증을 '동시에' 처리하는 Lua Script (원자성 보장)
    private static final String LUA_SCRIPT =
            "local stockKey = KEYS[1]; " +
                    "local userKey = KEYS[2]; " +
                    "local userId = ARGV[1]; " +
                    // 1) 중복 발급 검증 (Redis Set)
                    "if redis.call('SISMEMBER', userKey, userId) == 1 then " +
                    "    return 2; " + // 2: 이미 발급받은 유저
                    "end; " +
                    // 2) 재고 검증
                    "local stock = tonumber(redis.call('GET', stockKey)); " +
                    "if stock == nil or stock <= 0 then " +
                    "    return 1; " + // 1: 재고 소진
                    "end; " +
                    // 3) 재고 차감 및 유저 등록
                    "redis.call('DECR', stockKey); " +
                    "redis.call('SADD', userKey, userId); " +
                    "return 0; "; // 0: 성공

    public String issue(Long promotionId, Long userKey) {
        String stockKey = "promotion:stock:" + promotionId;
        String userPromotionKey = "promotion:users:" + promotionId;

        // Lua 스크립트 실행
        DefaultRedisScript<Long> script = new DefaultRedisScript<>(LUA_SCRIPT, Long.class);
        Long result = stringRedisTemplate.execute(script, List.of(stockKey, userPromotionKey), String.valueOf(userKey));

        if (result == null || result == 1) {
            return "SOLD_OUT";
        }
        if (result == 2) {
            return "DUPLICATED";
        }

        // 성공(0)했다면 Redis 대기열(List)에 데이터를 밀어 넣음
        String queueValue = promotionId + ":" + userKey;
        stringRedisTemplate.opsForList().rightPush("promotion:queue", queueValue);

        return "SUCCESS";
    }

}