package io.github.cantarinog.triggerly.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.room.Room;

import io.github.cantarinog.triggerly.data.local.AppDatabase;
import io.github.cantarinog.triggerly.data.repository.ReminderRepositoryImpl;
import io.github.cantarinog.triggerly.domain.model.Reminder;
import io.github.cantarinog.triggerly.domain.model.TriggerEvent;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationReceiver";
    private static final String CHANNEL_ID = "reminder_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String triggerId = intent.getStringExtra("TRIGGER_EVENT_ID");
        if (triggerId == null) return;

        new Thread(() -> {
            AppDatabase db = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "triggerly-db").build();
            ReminderRepositoryImpl repository = new ReminderRepositoryImpl(
                    db.reminderDao(),
                    db.triggerEventDao()
            );

            TriggerEvent currentTrigger = repository.getTriggerById(triggerId);

            if (currentTrigger != null) {
                Reminder reminder = repository.getReminderById(currentTrigger.reminderId());
                if (reminder != null) {
                    showNotification(context, reminder);
                    
                    TriggerEvent firedEvent = new TriggerEvent(
                            currentTrigger.id(),
                            currentTrigger.reminderId(),
                            currentTrigger.triggerTime(),
                            true
                    );
                    repository.saveTriggerEvent(firedEvent);
                }
            }
        }).start();
    }

    private void showNotification(Context context, Reminder reminder) {
        NotificationManager notificationManager = 
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Reminders", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info) // Placeholder
                .setContentTitle(reminder.name())
                .setContentText(reminder.description())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        notificationManager.notify(reminder.id().hashCode(), builder.build());
    }
}
