package io.github.cantarinog.triggerly.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.time.ZoneId;

import io.github.cantarinog.triggerly.domain.model.TriggerEvent;
import io.github.cantarinog.triggerly.domain.service.AlarmScheduler;

public class AlarmSchedulerImpl implements AlarmScheduler {

    private final Context context;
    private final AlarmManager alarmManager;

    public AlarmSchedulerImpl(Context context) {
        this.context = context;
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public void schedule(TriggerEvent triggerEvent) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("TRIGGER_EVENT_ID", triggerEvent.id());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                triggerEvent.id().hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        long triggerTimeMillis = triggerEvent.triggerTime()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMillis,
                    pendingIntent
            );
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMillis,
                    pendingIntent
            );
        } else {
            alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMillis,
                    pendingIntent
            );
        }
    }

    @Override
    public void cancel(TriggerEvent triggerEvent) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                triggerEvent.id().hashCode(),
                intent,
                PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE
        );

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }
}
