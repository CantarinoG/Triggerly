package io.github.cantarinog.triggerly.data.local.entity;

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
    public String imageUri;
    public String startTime;
    public String endTime;
    public int numReminders;
    public String soundUri;

    public ReminderEntity() {}
}
