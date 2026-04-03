package io.github.cantarinog.triggerly.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record TriggerEvent(
    String id,
    String reminderId,
    LocalDateTime triggerTime,
    boolean isFired
) {
    public TriggerEvent {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }
}
