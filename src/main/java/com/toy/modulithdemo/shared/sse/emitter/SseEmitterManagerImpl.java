package com.toy.modulithdemo.shared.sse.emitter;

import com.toy.modulithdemo.shared.sse.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
@RequiredArgsConstructor
public class SseEmitterManagerImpl implements SseEmitterManager {

    private final Map<Long, CopyOnWriteArrayList<SseEmitter>> emitters =
            new ConcurrentHashMap<>();

    @Override
    public SseEmitter subscribe(Long userKey) {
        SseEmitter emitter = new SseEmitter(300000L);

        emitters.computeIfAbsent(userKey, k -> new CopyOnWriteArrayList<>())
                .add(emitter);

        log.info("[메모리] 이벤트 {} 구독 시작", userKey);

        try {

            emitter.send(
                    SseEmitter.event()
                            .id(String.valueOf(System.currentTimeMillis()))
                            .name("CONNECT")
                            .data("Event ON")
                            .build()
            );
        } catch (IOException e) {
            emitter.completeWithError(e);
        }

        emitter.onCompletion(() -> emitters.get(userKey).remove(emitter));
        emitter.onTimeout(() -> emitters.get(userKey).remove(emitter));
        emitter.onError(throwable -> emitters.get(userKey).remove(emitter));

        return emitter;
    }

    @Override
    public void notifyAll(NotificationEvent event) {
        List<SseEmitter> emitterList = emitters.get(event.getUserKey());
        if (emitterList == null || emitterList.isEmpty()) {
            return;
        }

        emitterList.forEach(emitter -> {
            try {
                emitter.send(
                        SseEmitter.event()
                                .id(String.valueOf(System.currentTimeMillis()))
                                .name(event.getNotificationType().name())
                                .data(event)
                                .build()
                );
            } catch (IOException e) {
                log.error("Emitter 전송 실패", e);
                emitterList.remove(emitter);
                if (emitterList.isEmpty()) {
                    emitters.remove(event.getUserKey()); // 리스트가 비면 키 삭제
                }
            }
        });
    }
}