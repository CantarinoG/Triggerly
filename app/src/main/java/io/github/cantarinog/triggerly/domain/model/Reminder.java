package io.github.cantarinog.triggerly.domain.model;

import java.time.LocalTime;
import java.util.UUID;

public record Reminder(
    String id,
    String name,
    String description,
    String iconName,
    String colorHex,
    String imageUri,
    LocalTime startTime,
    LocalTime endTime,
    int numReminders,
    String soundUri
) {
    public Reminder {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }
}
