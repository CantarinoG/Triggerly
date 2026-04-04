package io.github.cantarinog.triggerly.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.time.LocalTime;

import io.github.cantarinog.triggerly.domain.model.Reminder;
import io.github.cantarinog.triggerly.domain.usecase.GetRemindersUseCase;
import io.github.cantarinog.triggerly.domain.usecase.SaveReminderUseCase;

public class FormViewModel extends ViewModel {
    private final SaveReminderUseCase saveReminderUseCase;
    private final GetRemindersUseCase getRemindersUseCase;
    
    private final MutableLiveData<Reminder> _reminder = new MutableLiveData<>();
    public final LiveData<Reminder> reminder = _reminder;

    public FormViewModel(SaveReminderUseCase saveReminderUseCase, GetRemindersUseCase getRemindersUseCase) {
        this.saveReminderUseCase = saveReminderUseCase;
        this.getRemindersUseCase = getRemindersUseCase;
    }

    public void loadReminder(String id) {
        new Thread(() -> {
            Reminder r = getRemindersUseCase.execute(id);
            _reminder.postValue(r);
        }).start();
    }

    public void saveReminder(
            String id,
            String name,
            String description,
            String iconName,
            LocalTime startTime,
            LocalTime endTime,
            int count,
            String colorHex,
            String soundUri
    ) {
        new Thread(() -> {
            Reminder reminder = new Reminder(
                    id,
                    name,
                    description,
                    iconName != null ? iconName : "ic_water_drop",
                    colorHex != null ? colorHex : "#2962FF",
                    startTime,
                    endTime,
                    count,
                    soundUri
            );
            saveReminderUseCase.execute(reminder);
        }).start();
    }
}

