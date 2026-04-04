package io.github.cantarinog.triggerly.presentation.ui;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

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

    private EditText editTextName;
    private EditText editTextDescription;
    private TextView textViewStartTime;
    private TextView textViewEndTime;
    private TextView textViewFrequency;
    private ImageButton buttonFreqMinus;
    private ImageButton buttonFreqPlus;
    private View buttonSave;
    private ImageButton buttonBack;

    private RecyclerView recyclerViewIcons;
    private RecyclerView recyclerViewColors;
    private TextView textViewSoundName;

    private static final int REQUEST_CODE_SOUND_PICKER = 1001;
    private String selectedSoundUri = null;

    private final String[] icons = {"ic_water_drop", "ic_target", "ic_person", "ic_medical", "ic_gym", "ic_lightbulb", "ic_book", "ic_bell"};
    private final String[] colors = {"#2962FF", "#00838F", "#AD1457", "#B71C1C", "#7986CB", "#00E5FF", "#F06292", "#2D3250"};

    private String reminderId = null;
    private LocalTime startTime = LocalTime.of(9, 0);
    private LocalTime endTime = LocalTime.of(18, 0);
    private int frequency = 8;
    private String selectedColor = "#2962FF";
    private String selectedIcon = "ic_water_drop";
    
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private FormViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_form);

        reminderId = getIntent().getStringExtra(EXTRA_REMINDER_ID);
        
        initViews();
        setupViewModel();
        setupAdapters();
        setupListeners();

        if (reminderId != null) {
            viewModel.loadReminder(reminderId);
        }
    }

    private void initViews() {
        editTextName = findViewById(R.id.editTextName);
        editTextDescription = findViewById(R.id.editTextDescription);
        textViewStartTime = findViewById(R.id.textViewStartTime);
        textViewEndTime = findViewById(R.id.textViewEndTime);
        textViewFrequency = findViewById(R.id.textViewFrequency);
        buttonFreqMinus = findViewById(R.id.buttonFreqMinus);
        buttonFreqPlus = findViewById(R.id.buttonFreqPlus);
        buttonSave = findViewById(R.id.buttonSave);
        buttonBack = findViewById(R.id.buttonBack);
        recyclerViewIcons = findViewById(R.id.recyclerViewIcons);
        recyclerViewColors = findViewById(R.id.recyclerViewColors);
        textViewSoundName = findViewById(R.id.textViewSoundName);

        updateTimeTexts();
        updateFrequencyText();
    }

    private void setupAdapters() {
        IconAdapter iconAdapter = new IconAdapter(icons, icon -> selectedIcon = icon);
        recyclerViewIcons.setAdapter(iconAdapter);

        ColorAdapter colorAdapter = new ColorAdapter(colors, color -> selectedColor = color);
        recyclerViewColors.setAdapter(colorAdapter);
    }

    // Inner Adapters for Selection
    private class IconAdapter extends RecyclerView.Adapter<IconAdapter.ViewHolder> {
        private final String[] data;
        private final java.util.function.Consumer<String> onSelect;
        private int selectedPos = 0;

        IconAdapter(String[] data, java.util.function.Consumer<String> onSelect) {
            this.data = data;
            this.onSelect = onSelect;
        }

        @Override
        public ViewHolder onCreateViewHolder(android.view.ViewGroup p, int vt) {
            android.widget.FrameLayout container = new android.widget.FrameLayout(p.getContext());
            int cellSize = p.getMeasuredWidth() / 4;
            if (cellSize == 0) cellSize = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
            container.setLayoutParams(new android.view.ViewGroup.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT, 
                    (int) (64 * p.getContext().getResources().getDisplayMetrics().density)));
            
            android.widget.ImageView iv = new android.widget.ImageView(p.getContext());
            int iconSize = (int) (48 * p.getContext().getResources().getDisplayMetrics().density);
            android.widget.FrameLayout.LayoutParams lp = new android.widget.FrameLayout.LayoutParams(iconSize, iconSize);
            lp.gravity = android.view.Gravity.CENTER;
            iv.setLayoutParams(lp);
            iv.setPadding(24, 24, 24, 24);
            container.addView(iv);
            return new ViewHolder(container, iv);
        }

        @Override
        public void onBindViewHolder(ViewHolder h, int p) {
            String iconName = data[p];
            int resId = getResources().getIdentifier(iconName, "drawable", getPackageName());
            h.iv.setImageResource(resId);
            
            if (p == selectedPos) {
                h.iv.setBackgroundResource(R.drawable.bg_rounded_icon);
                h.iv.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#E8EAF6")));
            } else {
                h.iv.setBackground(null);
            }

            h.itemView.setOnClickListener(v -> {
                int old = selectedPos;
                selectedPos = p;
                onSelect.accept(iconName);
                notifyItemChanged(old);
                notifyItemChanged(selectedPos);
            });
        }

        @Override public int getItemCount() { return data.length; }
        class ViewHolder extends RecyclerView.ViewHolder {
            android.widget.ImageView iv;
            ViewHolder(android.view.View v, android.widget.ImageView iv) { super(v); this.iv = iv; }
        }
    }

    private class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ViewHolder> {
        private final String[] data;
        private final java.util.function.Consumer<String> onSelect;
        private int selectedPos = 0;

        ColorAdapter(String[] data, java.util.function.Consumer<String> onSelect) {
            this.data = data;
            this.onSelect = onSelect;
        }

        @Override
        public ViewHolder onCreateViewHolder(android.view.ViewGroup p, int vt) {
            android.widget.FrameLayout container = new android.widget.FrameLayout(p.getContext());
            container.setLayoutParams(new android.view.ViewGroup.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT, 
                    (int) (56 * p.getContext().getResources().getDisplayMetrics().density)));

            android.view.View v = new android.view.View(p.getContext());
            int size = (int) (40 * p.getContext().getResources().getDisplayMetrics().density);
            android.widget.FrameLayout.LayoutParams lp = new android.widget.FrameLayout.LayoutParams(size, size);
            lp.gravity = android.view.Gravity.CENTER;
            v.setLayoutParams(lp);
            container.addView(v);
            return new ViewHolder(container, v);
        }

        @Override
        public void onBindViewHolder(ViewHolder h, int p) {
            String colorHex = data[p];
            android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
            gd.setShape(android.graphics.drawable.GradientDrawable.OVAL);
            gd.setColor(android.graphics.Color.parseColor(colorHex));
            
            if (p == selectedPos) {
                gd.setStroke(4, android.graphics.Color.LTGRAY);
            }
            
            h.v.setBackground(gd);
            h.itemView.setOnClickListener(v -> {
                int old = selectedPos;
                selectedPos = p;
                onSelect.accept(colorHex);
                notifyItemChanged(old);
                notifyItemChanged(selectedPos);
            });
        }

        @Override public int getItemCount() { return data.length; }
        class ViewHolder extends RecyclerView.ViewHolder {
            android.view.View v;
            ViewHolder(android.view.View itemView, android.view.View v) { super(itemView); this.v = v; }
        }
    }

    private void setupViewModel() {
        AppDatabase db = androidx.room.Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "triggerly-db")
                .fallbackToDestructiveMigration()
                .build();
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
                frequency = reminder.numReminders();
                selectedColor = reminder.colorHex();
                selectedIcon = reminder.iconName();
                selectedSoundUri = reminder.soundUri();

                if (selectedSoundUri != null) {
                    android.media.Ringtone ringtone = android.media.RingtoneManager.getRingtone(this, android.net.Uri.parse(selectedSoundUri));
                    if (ringtone != null) {
                        textViewSoundName.setText(ringtone.getTitle(this));
                    }
                }
                updateTimeTexts();
                updateFrequencyText();
            }
        });
    }

    private void setupListeners() {
        buttonBack.setOnClickListener(v -> finish());
        
        textViewStartTime.setOnClickListener(v -> showTimePicker(true));
        textViewEndTime.setOnClickListener(v -> showTimePicker(false));

        buttonFreqMinus.setOnClickListener(v -> {
            if (frequency > 1) {
                frequency--;
                updateFrequencyText();
            }
        });
        
        buttonFreqPlus.setOnClickListener(v -> {
            if (frequency < 24) {
                frequency++;
                updateFrequencyText();
            }
        });

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
                    selectedIcon,
                    startTime,
                    endTime,
                    frequency,
                    selectedColor,
                    selectedSoundUri
            );
            
            Toast.makeText(this, "Reminder saved!", Toast.LENGTH_SHORT).show();
            finish();
        });

        textViewSoundName.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(android.media.RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(android.media.RingtoneManager.EXTRA_RINGTONE_TYPE, android.media.RingtoneManager.TYPE_NOTIFICATION);
            intent.putExtra(android.media.RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Notification Sound");
            intent.putExtra(android.media.RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, 
                selectedSoundUri != null ? android.net.Uri.parse(selectedSoundUri) : (android.net.Uri) null);
            startActivityForResult(intent, REQUEST_CODE_SOUND_PICKER);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SOUND_PICKER && resultCode == RESULT_OK) {
            android.net.Uri uri = data.getParcelableExtra(android.media.RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (uri != null) {
                selectedSoundUri = uri.toString();
                android.media.Ringtone ringtone = android.media.RingtoneManager.getRingtone(this, uri);
                textViewSoundName.setText(ringtone.getTitle(this));
            } else {
                selectedSoundUri = null;
                textViewSoundName.setText("Silent");
            }
        }
    }

    private void showTimePicker(boolean isStart) {
        LocalTime initial = isStart ? startTime : endTime;
        com.google.android.material.timepicker.MaterialTimePicker picker = 
            new com.google.android.material.timepicker.MaterialTimePicker.Builder()
                .setTimeFormat(com.google.android.material.timepicker.TimeFormat.CLOCK_24H)
                .setHour(initial.getHour())
                .setMinute(initial.getMinute())
                .setTitleText(isStart ? "Select Start Time" : "Select End Time")
                .build();

        picker.addOnPositiveButtonClickListener(v -> {
            if (isStart) startTime = LocalTime.of(picker.getHour(), picker.getMinute());
            else endTime = LocalTime.of(picker.getHour(), picker.getMinute());
            updateTimeTexts();
        });
        
        picker.show(getSupportFragmentManager(), "TIME_PICKER");
    }

    private void updateTimeTexts() {
        textViewStartTime.setText(startTime.format(timeFormatter));
        textViewEndTime.setText(endTime.format(timeFormatter));
    }

    private void updateFrequencyText() {
        textViewFrequency.setText(String.valueOf(frequency));
    }
}
