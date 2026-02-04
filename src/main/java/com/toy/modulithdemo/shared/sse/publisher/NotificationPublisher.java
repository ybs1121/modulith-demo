package com.toy.modulithdemo.shared.sse.publisher;

import com.toy.modulithdemo.shared.sse.dto.NotificationEvent;

public interface NotificationPublisher {
    void publish(NotificationEvent event);

}
