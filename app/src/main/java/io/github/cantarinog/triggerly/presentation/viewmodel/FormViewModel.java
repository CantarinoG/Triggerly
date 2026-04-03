package io.github.cantarinog.triggerly.presentation.viewmodel;

import androidx.lifecycle.ViewModel;

import java.time.LocalTime;

import io.github.cantarinog.triggerly.domain.model.Reminder;
import io.github.cantarinog.triggerly.domain.usecase.SaveReminderUseCase;

public class FormViewModel extends ViewModel {
    private final SaveReminderUseCase saveReminderUseCase;

    public FormViewModel(SaveReminderUseCase saveReminderUseCase) {
        this.saveReminderUseCase = saveReminderUseCase;
    }

    public void saveReminder(
            String id,
            String name,
            String description,
            LocalTime startTime,
            LocalTime endTime,
            int count,
            String colorHex
    ) {
        new Thread(() -> {
            Reminder reminder = new Reminder(
                    id != null ? id : java.util.UUID.randomUUID().toString(),
                    name,
                    description,
                    "ic_bell",
                    colorHex != null ? colorHex : "#6200EE",
                    null,
                    startTime,
                    endTime,
                    count,
                    null
            );
            saveReminderUseCase.execute(reminder);
        }).start();
    }
}
