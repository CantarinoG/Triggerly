package io.github.cantarinog.triggerly.presentation.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import io.github.cantarinog.triggerly.domain.usecase.DeleteReminderUseCase;
import io.github.cantarinog.triggerly.domain.usecase.GetRemindersUseCase;

public class MainViewModelFactory implements ViewModelProvider.Factory {
    private final GetRemindersUseCase getRemindersUseCase;
    private final DeleteReminderUseCase deleteReminderUseCase;

    public MainViewModelFactory(GetRemindersUseCase getRemindersUseCase, DeleteReminderUseCase deleteReminderUseCase) {
        this.getRemindersUseCase = getRemindersUseCase;
        this.deleteReminderUseCase = deleteReminderUseCase;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(getRemindersUseCase, deleteReminderUseCase);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
