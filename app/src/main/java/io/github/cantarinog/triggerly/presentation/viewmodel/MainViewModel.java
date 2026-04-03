package io.github.cantarinog.triggerly.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import io.github.cantarinog.triggerly.domain.model.Reminder;
import io.github.cantarinog.triggerly.domain.usecase.DeleteReminderUseCase;
import io.github.cantarinog.triggerly.domain.usecase.GetRemindersUseCase;

public class MainViewModel extends ViewModel {
    private final GetRemindersUseCase getRemindersUseCase;
    private final DeleteReminderUseCase deleteReminderUseCase;
    private final MutableLiveData<List<Reminder>> _reminders = new MutableLiveData<>();
    public final LiveData<List<Reminder>> reminders = _reminders;

    public MainViewModel(GetRemindersUseCase getRemindersUseCase, DeleteReminderUseCase deleteReminderUseCase) {
        this.getRemindersUseCase = getRemindersUseCase;
        this.deleteReminderUseCase = deleteReminderUseCase;
        loadReminders();
    }

    public void loadReminders() {
        new Thread(() -> {
            List<Reminder> list = getRemindersUseCase.execute();
            _reminders.postValue(list);
        }).start();
    }

    public void deleteReminder(String id) {
        new Thread(() -> {
            deleteReminderUseCase.execute(id);
            loadReminders();
        }).start();
    }
}

