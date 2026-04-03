package io.github.cantarinog.triggerly.domain.usecase;

import java.time.LocalDateTime;
import java.util.List;

import io.github.cantarinog.triggerly.domain.model.TriggerEvent;
import io.github.cantarinog.triggerly.domain.repository.ReminderRepository;
import io.github.cantarinog.triggerly.domain.service.AlarmScheduler;

public class RescheduleAlarmsUseCase {
    private final ReminderRepository reminderRepository;
    private final AlarmScheduler alarmScheduler;

    public RescheduleAlarmsUseCase(ReminderRepository reminderRepository, AlarmScheduler alarmScheduler) {
        this.reminderRepository = reminderRepository;
        this.alarmScheduler = alarmScheduler;
    }

    public void execute() {
        List<TriggerEvent> pendingTriggers = reminderRepository.getAllPendingTriggers();
        LocalDateTime now = LocalDateTime.now();

        for (TriggerEvent trigger : pendingTriggers) {
            if (trigger.triggerTime().isAfter(now)) {
                alarmScheduler.schedule(trigger);
            }
        }
    }
}
