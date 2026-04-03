package io.github.cantarinog.triggerly.presentation.ui;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import io.github.cantarinog.triggerly.R;
import io.github.cantarinog.triggerly.data.local.AppDatabase;
import io.github.cantarinog.triggerly.data.repository.ReminderRepositoryImpl;
import io.github.cantarinog.triggerly.domain.service.AlarmScheduler;
import io.github.cantarinog.triggerly.domain.usecase.GenerateRandomTimestampsUseCase;
import io.github.cantarinog.triggerly.domain.usecase.SaveReminderUseCase;
import io.github.cantarinog.triggerly.presentation.viewmodel.FormViewModel;
import io.github.cantarinog.triggerly.presentation.viewmodel.FormViewModelFactory;
import io.github.cantarinog.triggerly.service.AlarmSchedulerImpl;
import io.github.cantarinog.triggerly.domain.usecase.GetRemindersUseCase;

public class ReminderFormActivity extends AppCompatActivity {
    public static final String EXTRA_REMINDER_ID = "reminder_id";

    private TextInputEditText editTextName;
    private TextInputEditText editTextDescription;
    private Button buttonStartTime;
    private Button buttonEndTime;
    private Slider sliderFrequency;
    private Button buttonSave;

    private String reminderId = null;
    private LocalTime startTime = LocalTime.of(9, 0);
    private LocalTime endTime = LocalTime.of(18, 0);
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    private FormViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_form);

        reminderId = getIntent().getStringExtra(EXTRA_REMINDER_ID);
        if (reminderId != null) {
            setTitle("Edit Reminder");
        } else {
            setTitle("New Reminder");
        }

        initViews();
        setupViewModel();
        setupListeners();

        if (reminderId != null) {
            viewModel.loadReminder(reminderId);
        }
    }

    private void initViews() {
        editTextName = findViewById(R.id.editTextName);
        editTextDescription = findViewById(R.id.editTextDescription);
        buttonStartTime = findViewById(R.id.buttonStartTime);
        buttonEndTime = findViewById(R.id.buttonEndTime);
        sliderFrequency = findViewById(R.id.sliderFrequency);
        buttonSave = findViewById(R.id.buttonSave);

        updateTimeButtons();
    }

    private void setupViewModel() {
        AppDatabase db = androidx.room.Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "triggerly-db").build();
        ReminderRepositoryImpl repository = new ReminderRepositoryImpl(db.reminderDao(), db.triggerEventDao());
        AlarmScheduler scheduler = new AlarmSchedulerImpl(this);
        GenerateRandomTimestampsUseCase generateUseCase = new GenerateRandomTimestampsUseCase();
        SaveReminderUseCase saveUseCase = new SaveReminderUseCase(repository, scheduler, generateUseCase);
        GetRemindersUseCase getUseCase = new GetRemindersUseCase(repository);

        FormViewModelFactory factory = new FormViewModelFactory(saveUseCase, getUseCase);
        viewModel = new ViewModelProvider(this, factory).get(FormViewModel.class);

        viewModel.reminder.observe(this, reminder -> {
            if (reminder != null) {
                editTextName.setText(reminder.name());
                editTextDescription.setText(reminder.description());
                startTime = reminder.startTime();
                endTime = reminder.endTime();
                sliderFrequency.setValue(reminder.numReminders());
                updateTimeButtons();
            }
        });
    }

    private void setupListeners() {
        buttonStartTime.setOnClickListener(v -> showTimePicker(true));
        buttonEndTime.setOnClickListener(v -> showTimePicker(false));

        buttonSave.setOnClickListener(v -> {
            String name = editTextName.getText().toString();
            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.saveReminder(
                    reminderId,
                    name,
                    editTextDescription.getText().toString(),
                    startTime,
                    endTime,
                    (int) sliderFrequency.getValue(),
                    "#6200EE"
            );
            
            Toast.makeText(this, "Reminder saved!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void showTimePicker(boolean isStart) {
        LocalTime initial = isStart ? startTime : endTime;
        TimePickerDialog picker = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            if (isStart) startTime = LocalTime.of(hourOfDay, minute);
            else endTime = LocalTime.of(hourOfDay, minute);
            updateTimeButtons();
        }, initial.getHour(), initial.getMinute(), true);
        picker.show();
    }

    private void updateTimeButtons() {
        buttonStartTime.setText("Starts: " + startTime.format(timeFormatter));
        buttonEndTime.setText("Ends: " + endTime.format(timeFormatter));
    }
}
