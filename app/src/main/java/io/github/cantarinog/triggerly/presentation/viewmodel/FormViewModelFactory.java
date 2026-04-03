package io.github.cantarinog.triggerly.presentation.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import io.github.cantarinog.triggerly.domain.usecase.GetRemindersUseCase;
import io.github.cantarinog.triggerly.domain.usecase.SaveReminderUseCase;

public class FormViewModelFactory implements ViewModelProvider.Factory {
    private final SaveReminderUseCase saveReminderUseCase;
    private final GetRemindersUseCase getRemindersUseCase;

    public FormViewModelFactory(SaveReminderUseCase saveReminderUseCase, GetRemindersUseCase getRemindersUseCase) {
        this.saveReminderUseCase = saveReminderUseCase;
        this.getRemindersUseCase = getRemindersUseCase;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(FormViewModel.class)) {
            return (T) new FormViewModel(saveReminderUseCase, getRemindersUseCase);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
