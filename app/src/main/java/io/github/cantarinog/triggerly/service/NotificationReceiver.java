package io.github.cantarinog.triggerly.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import io.github.cantarinog.triggerly.data.local.AppDatabase;
import io.github.cantarinog.triggerly.data.repository.ReminderRepositoryImpl;
import io.github.cantarinog.triggerly.domain.model.Reminder;
import io.github.cantarinog.triggerly.domain.model.TriggerEvent;
import io.github.cantarinog.triggerly.domain.usecase.FireTriggerUseCase;
import io.github.cantarinog.triggerly.domain.usecase.RescheduleAlarmsUseCase;
import io.github.cantarinog.triggerly.service.AlarmSchedulerImpl;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationReceiver";
    private static final String CHANNEL_ID = "reminder_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String triggerId = intent.getStringExtra("TRIGGER_EVENT_ID");
        Log.d(TAG, "onReceive called. triggerId=" + triggerId);
        if (triggerId == null) return;

        final PendingResult pendingResult = goAsync();
        Log.d(TAG, "goAsync obtained");

        new Thread(() -> {
            try {
                Log.d(TAG, "[1/5] Getting DB instance...");
                AppDatabase db = AppDatabase.getInstance(context);

                Log.d(TAG, "[2/5] Querying trigger: " + triggerId);
                ReminderRepositoryImpl repository = new ReminderRepositoryImpl(
                        db.reminderDao(),
                        db.triggerEventDao()
                );
                TriggerEvent currentTrigger = repository.getTriggerById(triggerId);
                Log.d(TAG, "[2/5] Trigger found: " + (currentTrigger != null));

                if (currentTrigger != null) {
                    Log.d(TAG, "[3/5] Querying reminder: " + currentTrigger.reminderId());
                    Reminder reminder = repository.getReminderById(currentTrigger.reminderId());
                    Log.d(TAG, "[3/5] Reminder found: " + (reminder != null));

                    if (reminder != null) {
                        Log.d(TAG, "[4/5] Showing notification for: " + reminder.name());
                        showNotification(context, reminder);

                        Log.d(TAG, "[5/5] Firing trigger and scheduling next day...");
                        FireTriggerUseCase useCase =
                                new FireTriggerUseCase(repository, new AlarmSchedulerImpl(context));
                        useCase.execute(triggerId);

                        Log.d(TAG, "[Self-Healing] Checking for missed triggers...");
                        RescheduleAlarmsUseCase rescheduleUseCase =
                                new RescheduleAlarmsUseCase(repository, new AlarmSchedulerImpl(context));
                        rescheduleUseCase.executeMissed();

                        Log.d(TAG, "[DONE] All steps completed successfully for: " + reminder.name());
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "CRASH in notification pipeline!", e);
            } finally {
                pendingResult.finish();
                Log.d(TAG, "pendingResult.finish() called");
            }
        }).start();
    }

    private void showNotification(Context context, Reminder reminder) {
        NotificationManager notificationManager = 
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String soundUriStr = reminder.soundUri();
        String channelId = CHANNEL_ID;
        android.net.Uri soundUri = null;
        if (soundUriStr != null && !soundUriStr.isEmpty()) {
            soundUri = android.net.Uri.parse(soundUriStr);
            channelId = CHANNEL_ID + "_" + soundUriStr.hashCode();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId, "Triggerly Reminders", NotificationManager.IMPORTANCE_HIGH);
            
            if (soundUri != null) {
                android.media.AudioAttributes audioAttributes = new android.media.AudioAttributes.Builder()
                        .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(android.media.AudioAttributes.USAGE_NOTIFICATION)
                        .build();
                channel.setSound(soundUri, audioAttributes);
            }
            notificationManager.createNotificationChannel(channel);
        }

        int iconResId = context.getResources().getIdentifier(
                reminder.iconName(), "drawable", context.getPackageName());
        if (iconResId == 0) {
            iconResId = android.R.drawable.ic_dialog_info;
        }

        int colorInt = android.graphics.Color.parseColor(reminder.colorHex());

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(iconResId)
                .setContentTitle(reminder.name())
                .setContentText(reminder.description())
                .setColor(colorInt)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setAutoCancel(true);

        if (soundUri != null && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            builder.setSound(soundUri);
        }

        notificationManager.notify(reminder.id().hashCode(), builder.build());
    }
}
