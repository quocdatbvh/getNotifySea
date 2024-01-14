package com.notifySeabank;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.notifySeabank.NotiCatchService.ACTION_RECEIVE_NOTIFICATION;

public class NewNotificationReceiver extends BroadcastReceiver {
    private NotificationListener listener;

    public NewNotificationReceiver(NotificationListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ACTION_RECEIVE_NOTIFICATION)) {
            NotificationInfo notificationInfo = (NotificationInfo) intent.getSerializableExtra(Constants.EXTRA_NOTIFICATION);
            listener.onNewNotificationPosted(notificationInfo);
        }
    }

    public interface NotificationListener {
        void onNewNotificationPosted(NotificationInfo newNotification);
    }
}
