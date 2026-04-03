package io.github.cantarinog.triggerly.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.github.cantarinog.triggerly.data.local.entity.TriggerEventEntity;

@Dao
public interface TriggerEventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTriggerEvent(TriggerEventEntity triggerEvent);

    @Query("SELECT * FROM trigger_events WHERE reminderId = :reminderId")
    List<TriggerEventEntity> getTriggerEventsForReminder(String reminderId);

    @Query("DELETE FROM trigger_events WHERE reminderId = :reminderId")
    void deleteTriggerEventsByReminderId(String reminderId);

    @Query("SELECT * FROM trigger_events WHERE isFired = 0")
    List<TriggerEventEntity> getAllPendingTriggers();

    @Query("SELECT * FROM trigger_events WHERE id = :id")
    TriggerEventEntity getTriggerById(String id);
}
