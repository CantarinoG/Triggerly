package io.github.cantarinog.triggerly.domain.usecase;

import java.util.List;
import io.github.cantarinog.triggerly.domain.model.TriggerEvent;
import io.github.cantarinog.triggerly.domain.repository.ReminderRepository;
import io.github.cantarinog.triggerly.domain.service.AlarmScheduler;

public class DeleteReminderUseCase {
    private final ReminderRepository reminderRepository;
    private final AlarmScheduler alarmScheduler;

    public DeleteReminderUseCase(ReminderRepository reminderRepository, AlarmScheduler alarmScheduler) {
        this.reminderRepository = reminderRepository;
        this.alarmScheduler = alarmScheduler;
    }

    public void execute(String reminderId) {
        List<TriggerEvent> triggers = reminderRepository.getTriggerEventsForReminder(reminderId);
        
        for (TriggerEvent trigger : triggers) {
            alarmScheduler.cancel(trigger);
        }
        
        reminderRepository.deleteTriggerEventsByReminderId(reminderId);

        reminderRepository.deleteReminder(reminderId);
    }
}
