package io.github.cantarinog.triggerly.data.local.entity;

import io.github.cantarinog.triggerly.domain.model.TriggerEvent;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "trigger_events",
    foreignKeys = @ForeignKey(
        entity = ReminderEntity.class,
        parentColumns = "id",
        childColumns = "reminderId",
        onDelete = ForeignKey.CASCADE
    ),
    indices = @Index("reminderId")
)
public class TriggerEventEntity {
    @PrimaryKey
    @NonNull
    public String id;
    public String reminderId;
    public String triggerTime;
    public boolean isFired;

    public TriggerEventEntity() {}

    public static TriggerEventEntity fromDomain(TriggerEvent event) {
        TriggerEventEntity entity = new TriggerEventEntity();
        entity.id = event.id();
        entity.reminderId = event.reminderId();
        entity.triggerTime = event.triggerTime().toString();
        entity.isFired = event.isFired();
        return entity;
    }

    public TriggerEvent toDomain() {
        return new TriggerEvent(
            id,
            reminderId,
            java.time.LocalDateTime.parse(triggerTime),
            isFired
        );
    }
}
