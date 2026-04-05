package io.github.cantarinog.triggerly;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.util.Log;

import io.github.cantarinog.triggerly.domain.model.Reminder;
import io.github.cantarinog.triggerly.data.local.AppDatabase;
import io.github.cantarinog.triggerly.data.repository.ReminderRepositoryImpl;
import io.github.cantarinog.triggerly.domain.usecase.DeleteReminderUseCase;
import io.github.cantarinog.triggerly.domain.usecase.GetRemindersUseCase;
import io.github.cantarinog.triggerly.presentation.ui.ReminderAdapter;
import io.github.cantarinog.triggerly.presentation.viewmodel.MainViewModel;
import io.github.cantarinog.triggerly.presentation.viewmodel.MainViewModelFactory;
import io.github.cantarinog.triggerly.service.AlarmSchedulerImpl;
import io.github.cantarinog.triggerly.presentation.ui.ReminderFormActivity;
import io.github.cantarinog.triggerly.presentation.ui.ReminderFormActivity;

public class MainActivity extends AppCompatActivity {
    private MainViewModel viewModel;
    private ReminderAdapter adapter;
    private final androidx.activity.result.ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new androidx.activity.result.contract.ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Log.d("MainActivity", "Notification permission granted");
                } else {
                    Log.d("MainActivity", "Notification permission denied");
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkNotificationPermission();
        setupViewModel();
        setupRecyclerView();
        setupFab();

        viewModel.reminders.observe(this, reminders -> {
            adapter.setReminders(reminders);
        });
    }

    private void checkNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != 
                    android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void setupViewModel() {
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        ReminderRepositoryImpl repository = new ReminderRepositoryImpl(db.reminderDao(), db.triggerEventDao());
        GetRemindersUseCase getUseCase = new GetRemindersUseCase(repository);
        DeleteReminderUseCase deleteUseCase = new DeleteReminderUseCase(repository, new AlarmSchedulerImpl(this));
        
        MainViewModelFactory factory = new MainViewModelFactory(getUseCase, deleteUseCase);
        viewModel = new ViewModelProvider(this, factory).get(MainViewModel.class);
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewReminders);
        adapter = new ReminderAdapter(
                reminder -> {
                    android.content.Intent intent = new android.content.Intent(this, ReminderFormActivity.class);
                    intent.putExtra(ReminderFormActivity.EXTRA_REMINDER_ID, reminder.id());
                    startActivity(intent);
                },
                reminder -> showDeleteDialog(reminder)
        );
        recyclerView.setAdapter(adapter);
    }

    private void showDeleteDialog(Reminder reminder) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Reminder")
                .setMessage("Are you sure you want to delete '" + reminder.name() + "'? This will stop all randomized alarms for this item.")
                .setPositiveButton("Delete", (dialog, which) -> viewModel.deleteReminder(reminder.id()))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void setupFab() {
        FloatingActionButton fab = findViewById(R.id.fabAddReminder);
        fab.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, ReminderFormActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.loadReminders();
    }
}
