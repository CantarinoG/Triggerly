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
        for (TriggerEvent trigger : pendingTriggers) {
            alarmScheduler.schedule(trigger);
        }
    }

    public void executeMissed() {
        List<TriggerEvent> pendingTriggers = reminderRepository.getAllPendingTriggers();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tenMinutesAgo = now.minusMinutes(10);

        for (TriggerEvent trigger : pendingTriggers) {
            if (trigger.triggerTime().isBefore(tenMinutesAgo)) {
                alarmScheduler.schedule(trigger);
            }
        }
    }
}
