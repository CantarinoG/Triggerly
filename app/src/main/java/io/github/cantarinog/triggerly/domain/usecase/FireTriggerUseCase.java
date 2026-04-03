package io.github.cantarinog.triggerly.domain.usecase;

import java.util.UUID;

import io.github.cantarinog.triggerly.domain.model.TriggerEvent;
import io.github.cantarinog.triggerly.domain.repository.ReminderRepository;
import io.github.cantarinog.triggerly.domain.service.AlarmScheduler;

public class FireTriggerUseCase {
    private final ReminderRepository repository;
    private final AlarmScheduler scheduler;

    public FireTriggerUseCase(ReminderRepository repository, AlarmScheduler scheduler) {
        this.repository = repository;
        this.scheduler = scheduler;
    }

    public void execute(String triggerId) {
        TriggerEvent currentTrigger = repository.getTriggerById(triggerId);
        
        if (currentTrigger != null) {
            TriggerEvent firedEvent = new TriggerEvent(
                    currentTrigger.id(),
                    currentTrigger.reminderId(),
                    currentTrigger.triggerTime(),
                    true
            );
            repository.saveTriggerEvent(firedEvent);

            TriggerEvent nextDayEvent = new TriggerEvent(
                    UUID.randomUUID().toString(),
                    currentTrigger.reminderId(),
                    currentTrigger.triggerTime().plusDays(1),
                    false
            );
            repository.saveTriggerEvent(nextDayEvent);
            scheduler.schedule(nextDayEvent);
        }
    }
}
