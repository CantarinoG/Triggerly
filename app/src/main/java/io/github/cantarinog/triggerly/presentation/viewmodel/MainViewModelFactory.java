package io.github.cantarinog.triggerly.presentation.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import io.github.cantarinog.triggerly.domain.usecase.GetRemindersUseCase;

public class MainViewModelFactory implements ViewModelProvider.Factory {
    private final GetRemindersUseCase getRemindersUseCase;

    public MainViewModelFactory(GetRemindersUseCase getRemindersUseCase) {
        this.getRemindersUseCase = getRemindersUseCase;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(getRemindersUseCase);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
