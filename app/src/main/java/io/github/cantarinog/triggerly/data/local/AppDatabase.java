package io.github.cantarinog.triggerly.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import io.github.cantarinog.triggerly.data.local.dao.ReminderDao;
import io.github.cantarinog.triggerly.data.local.dao.TriggerEventDao;
import io.github.cantarinog.triggerly.data.local.entity.ReminderEntity;
import io.github.cantarinog.triggerly.data.local.entity.TriggerEventEntity;

@Database(entities = {ReminderEntity.class, TriggerEventEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ReminderDao reminderDao();
    public abstract TriggerEventDao triggerEventDao();
}
