package com.toy.modulithdemo.shared.sse.publisher;

import com.toy.modulithdemo.shared.sse.dto.NotificationEvent;
import com.toy.modulithdemo.shared.sse.emitter.SseEmitterManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationPublisherImpl implements NotificationPublisher {


    private final SseEmitterManager sseEmitterManager;

    @Override
    public void publish(NotificationEvent event) {
        sseEmitterManager.notifyAll(event);
    }
}
