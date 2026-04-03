package io.github.cantarinog.triggerly.presentation.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import io.github.cantarinog.triggerly.domain.usecase.SaveReminderUseCase;

public class FormViewModelFactory implements ViewModelProvider.Factory {
    private final SaveReminderUseCase saveReminderUseCase;

    public FormViewModelFactory(SaveReminderUseCase saveReminderUseCase) {
        this.saveReminderUseCase = saveReminderUseCase;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(FormViewModel.class)) {
            return (T) new FormViewModel(saveReminderUseCase);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
