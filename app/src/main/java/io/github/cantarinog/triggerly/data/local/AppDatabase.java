package io.github.cantarinog.triggerly.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import io.github.cantarinog.triggerly.data.local.dao.ReminderDao;
import io.github.cantarinog.triggerly.data.local.dao.TriggerEventDao;
import io.github.cantarinog.triggerly.data.local.entity.ReminderEntity;
import io.github.cantarinog.triggerly.data.local.entity.TriggerEventEntity;

@Database(entities = {ReminderEntity.class, TriggerEventEntity.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public abstract ReminderDao reminderDao();
    public abstract TriggerEventDao triggerEventDao();

    public static AppDatabase getInstance(android.content.Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = androidx.room.Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "triggerly-db"
                    )
                    .fallbackToDestructiveMigration()
                    .build();
                }
            }
        }
        return INSTANCE;
    }
}
