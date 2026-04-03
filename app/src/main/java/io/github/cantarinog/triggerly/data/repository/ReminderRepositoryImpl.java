package io.github.cantarinog.triggerly.data.repository;

import java.util.List;
import java.util.stream.Collectors;

import io.github.cantarinog.triggerly.data.local.dao.ReminderDao;
import io.github.cantarinog.triggerly.data.local.dao.TriggerEventDao;
import io.github.cantarinog.triggerly.data.local.entity.ReminderEntity;
import io.github.cantarinog.triggerly.data.local.entity.TriggerEventEntity;
import io.github.cantarinog.triggerly.domain.model.Reminder;
import io.github.cantarinog.triggerly.domain.model.TriggerEvent;
import io.github.cantarinog.triggerly.domain.repository.ReminderRepository;

public class ReminderRepositoryImpl implements ReminderRepository {

    private final ReminderDao reminderDao;
    private final TriggerEventDao triggerEventDao;

    public ReminderRepositoryImpl(ReminderDao reminderDao, TriggerEventDao triggerEventDao) {
        this.reminderDao = reminderDao;
        this.triggerEventDao = triggerEventDao;
    }

    @Override
    public void saveReminder(Reminder reminder) {
        reminderDao.insertReminder(ReminderEntity.fromDomain(reminder));
    }

    @Override
    public void saveTriggerEvent(TriggerEvent triggerEvent) {
        triggerEventDao.insertTriggerEvent(TriggerEventEntity.fromDomain(triggerEvent));
    }

    @Override
    public List<Reminder> getAllReminders() {
        return reminderDao.getAllReminders().stream()
                .map(ReminderEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Reminder getReminderById(String id) {
        ReminderEntity entity = reminderDao.getReminderById(id);
        return entity != null ? entity.toDomain() : null;
    }

    @Override
    public void deleteReminder(String id) {
        reminderDao.deleteReminder(id);
    }

    @Override
    public List<TriggerEvent> getTriggerEventsForReminder(String reminderId) {
        return triggerEventDao.getTriggerEventsForReminder(reminderId).stream()
                .map(TriggerEventEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteTriggerEventsByReminderId(String reminderId) {
        triggerEventDao.deleteTriggerEventsByReminderId(reminderId);
    }
}
