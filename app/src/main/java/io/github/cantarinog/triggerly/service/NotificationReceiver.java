package io.github.cantarinog.triggerly.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String triggerId = intent.getStringExtra("TRIGGER_EVENT_ID");

        // TODO: Implement notification
        // 1. Fetch the Reminder from DB using the ID
        // 2. Build the Notification (Title, Icon, Color)
        // 3. Play the Notification sound
        // 4. Update isFired = true in the DB
    }
}
