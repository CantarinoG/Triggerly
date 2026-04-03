package io.github.cantarinog.triggerly.domain.usecase;

import java.util.List;
import io.github.cantarinog.triggerly.domain.model.Reminder;
import io.github.cantarinog.triggerly.domain.repository.ReminderRepository;

public class GetRemindersUseCase {

    private final ReminderRepository reminderRepository;

    public GetRemindersUseCase(ReminderRepository reminderRepository) {
        this.reminderRepository = reminderRepository;
    }

    public List<Reminder> execute() {
        return reminderRepository.getAllReminders();
    }
}
