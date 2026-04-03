package io.github.cantarinog.triggerly.service;

import io.github.cantarinog.triggerly.data.local.AppDatabase;
import io.github.cantarinog.triggerly.data.repository.ReminderRepositoryImpl;
import io.github.cantarinog.triggerly.domain.repository.ReminderRepository;
import io.github.cantarinog.triggerly.domain.service.AlarmScheduler;
import io.github.cantarinog.triggerly.domain.usecase.RescheduleAlarmsUseCase;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

            AppDatabase db =
                    androidx.room.Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "triggerly-db").build();

            ReminderRepository repository =
                    new ReminderRepositoryImpl(
                            db.reminderDao(),
                            db.triggerEventDao()
                    );

            AlarmScheduler scheduler =
                    new AlarmSchedulerImpl(context);

            RescheduleAlarmsUseCase useCase =
                    new RescheduleAlarmsUseCase(repository, scheduler);

            new Thread(useCase::execute).start();
        }
    }
}
