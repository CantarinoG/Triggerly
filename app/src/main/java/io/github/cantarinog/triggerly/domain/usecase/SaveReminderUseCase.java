package io.github.cantarinog.triggerly.domain.usecase;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import io.github.cantarinog.triggerly.domain.model.Reminder;
import io.github.cantarinog.triggerly.domain.model.TriggerEvent;
import io.github.cantarinog.triggerly.domain.service.AlarmScheduler;
import io.github.cantarinog.triggerly.domain.repository.ReminderRepository;

public class SaveReminderUseCase {

    private final ReminderRepository reminderRepository;
    private final AlarmScheduler alarmScheduler;
    private final GenerateRandomTimestampsUseCase generateRandomTimestampsUseCase;

    public SaveReminderUseCase(
            ReminderRepository reminderRepository,
            AlarmScheduler alarmScheduler,
            GenerateRandomTimestampsUseCase generateRandomTimestampsUseCase) {
        this.reminderRepository = reminderRepository;
        this.alarmScheduler = alarmScheduler;
        this.generateRandomTimestampsUseCase = generateRandomTimestampsUseCase;
    }

    public void execute(Reminder reminder) {
        List<TriggerEvent> oldTriggers = reminderRepository.getTriggerEventsForReminder(reminder.id());
        
        for (TriggerEvent oldTrigger : oldTriggers) {
            alarmScheduler.cancel(oldTrigger);
        }
        if (!oldTriggers.isEmpty()) {
            reminderRepository.deleteTriggerEventsByReminderId(reminder.id());
        }

        reminderRepository.saveReminder(reminder);

        LocalDate targetDate = LocalDate.now();

        List<LocalDateTime> randomTimes = generateRandomTimestampsUseCase.execute(
                targetDate,
                reminder.startTime(),
                reminder.endTime(),
                reminder.numReminders()
        );

        for (LocalDateTime time : randomTimes) {
            TriggerEvent newTrigger = new TriggerEvent(
                    null,
                    reminder.id(),
                    time,
                    false
            );

            reminderRepository.saveTriggerEvent(newTrigger);
            alarmScheduler.schedule(newTrigger);
        }
    }
}
