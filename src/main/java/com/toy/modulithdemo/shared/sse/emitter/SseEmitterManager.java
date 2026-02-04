package com.toy.modulithdemo.shared.sse.emitter;

import com.toy.modulithdemo.shared.sse.dto.NotificationEvent;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseEmitterManager {

    SseEmitter subscribe(Long userKey);

    void notifyAll(NotificationEvent event);
}
