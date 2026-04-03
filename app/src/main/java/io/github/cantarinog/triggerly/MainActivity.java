package io.github.cantarinog.triggerly;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.util.Log;

import io.github.cantarinog.triggerly.data.local.AppDatabase;
import io.github.cantarinog.triggerly.data.repository.ReminderRepositoryImpl;
import io.github.cantarinog.triggerly.domain.usecase.GetRemindersUseCase;
import io.github.cantarinog.triggerly.presentation.ui.ReminderAdapter;
import io.github.cantarinog.triggerly.presentation.viewmodel.MainViewModel;
import io.github.cantarinog.triggerly.presentation.viewmodel.MainViewModelFactory;

public class MainActivity extends AppCompatActivity {
    private MainViewModel viewModel;
    private ReminderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupViewModel();
        setupRecyclerView();
        setupFab();

        viewModel.reminders.observe(this, reminders -> {
            adapter.setReminders(reminders);
        });
    }

    private void setupViewModel() {
        AppDatabase db = androidx.room.Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "triggerly-db").build();
        ReminderRepositoryImpl repository = new ReminderRepositoryImpl(db.reminderDao(), db.triggerEventDao());
        GetRemindersUseCase useCase = new GetRemindersUseCase(repository);
        
        MainViewModelFactory factory = new MainViewModelFactory(useCase);
        viewModel = new ViewModelProvider(this, factory).get(MainViewModel.class);
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewReminders);
        adapter = new ReminderAdapter(reminder -> {
            // TODO: Open edit screen
            Log.d("MainActivity", "Clicked: " + reminder.name());
        });
        recyclerView.setAdapter(adapter);
    }

    private void setupFab() {
        FloatingActionButton fab = findViewById(R.id.fabAddReminder);
        fab.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, io.github.cantarinog.triggerly.presentation.ui.ReminderFormActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.loadReminders();
    }
}
