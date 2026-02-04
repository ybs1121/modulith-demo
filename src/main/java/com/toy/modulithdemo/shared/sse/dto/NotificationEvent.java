package com.toy.modulithdemo.shared.sse.dto;

import com.toy.modulithdemo.shared.sse.constant.NotificationType;
import lombok.Builder;
import lombok.Value;
import tools.jackson.databind.annotation.JsonDeserialize;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
@JsonDeserialize(builder = NotificationEvent.NotificationEventBuilder.class)
public class NotificationEvent {
    String eventId;
    Long userKey;
    NotificationType notificationType;
    Map<String, Object> payload;

    long occurredAt;
    int version;

    @Builder.Default
    Map<String, String> headers = new HashMap<>();

    public static NotificationEvent create(
            Long userKey,
            NotificationType type,
            Map<String, Object> payload) {
        return NotificationEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .userKey(userKey)
                .notificationType(type)
                .payload(payload)
                .occurredAt(System.currentTimeMillis())
                .version(1)
                .build();
    }
}
