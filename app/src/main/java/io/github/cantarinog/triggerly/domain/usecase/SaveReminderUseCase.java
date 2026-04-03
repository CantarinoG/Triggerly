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
        reminderRepository.saveReminder(reminder);
        /* 
         * Note: If this is an update, we should technically pull the old TriggerEvents 
         * and call alarmScheduler.cancel() on them before we create new ones.
         * We will need to add a feature to the repository to fetch triggers for a reminder.
         */
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

            alarmScheduler.schedule(newTrigger);
        }
    }
}
