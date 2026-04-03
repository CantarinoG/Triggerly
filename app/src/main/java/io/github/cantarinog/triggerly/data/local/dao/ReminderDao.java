package io.github.cantarinog.triggerly.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.github.cantarinog.triggerly.data.local.entity.ReminderEntity;

@Dao
public interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertReminder(ReminderEntity reminder);

    @Query("SELECT * FROM reminders")
    List<ReminderEntity> getAllReminders();

    @Query("SELECT * FROM reminders WHERE id = :id")
    ReminderEntity getReminderById(String id);

    @Query("DELETE FROM reminders WHERE id = :id")
    void deleteReminder(String id);
}
