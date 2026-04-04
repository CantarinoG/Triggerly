package io.github.cantarinog.triggerly.data.local.entity;

import io.github.cantarinog.triggerly.domain.model.Reminder;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalTime;

@Entity(tableName = "reminders")
public class ReminderEntity {
    @PrimaryKey
    @NonNull
    public String id;
    public String name;
    public String description;
    public String iconName;
    public String colorHex;
    public String startTime;
    public String endTime;
    public int numReminders;
    public String soundUri;

    public ReminderEntity() {}

    public static ReminderEntity fromDomain(Reminder reminder) {
        ReminderEntity entity = new ReminderEntity();
        entity.id = reminder.id();
        entity.name = reminder.name();
        entity.description = reminder.description();
        entity.iconName = reminder.iconName();
        entity.colorHex = reminder.colorHex();
        entity.startTime = reminder.startTime().toString();
        entity.endTime = reminder.endTime().toString();
        entity.numReminders = reminder.numReminders();
        entity.soundUri = reminder.soundUri();
        return entity;
    }

    public Reminder toDomain() {
        return new Reminder(
            id,
            name,
            description,
            iconName,
            colorHex,
            java.time.LocalTime.parse(startTime),
            java.time.LocalTime.parse(endTime),
            numReminders,
            soundUri
        );
    }
}
