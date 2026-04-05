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
import io.github.cantarinog.triggerly.service.AlarmSchedulerImpl;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationReceiver";
    private static final String CHANNEL_ID = "reminder_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String triggerId = intent.getStringExtra("TRIGGER_EVENT_ID");
        if (triggerId == null) return;

        final PendingResult pendingResult = goAsync();

        new Thread(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(context);
                ReminderRepositoryImpl repository = new ReminderRepositoryImpl(
                        db.reminderDao(),
                        db.triggerEventDao()
                );

                TriggerEvent currentTrigger = repository.getTriggerById(triggerId);

                if (currentTrigger != null) {
                    Reminder reminder = repository.getReminderById(currentTrigger.reminderId());
                    if (reminder != null) {
                        showNotification(context, reminder);

                        FireTriggerUseCase useCase =
                                new FireTriggerUseCase(repository, new AlarmSchedulerImpl(context));

                        useCase.execute(triggerId);
                    }
                }
            } finally {
                pendingResult.finish();
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
