package io.github.cantarinog.triggerly.domain.repository;

import java.util.List;
import io.github.cantarinog.triggerly.domain.model.Reminder;

public interface ReminderRepository {
    void saveReminder(Reminder reminder);
    List<Reminder> getAllReminders();
    Reminder getReminderById(String id);
    void deleteReminder(String id);
}
