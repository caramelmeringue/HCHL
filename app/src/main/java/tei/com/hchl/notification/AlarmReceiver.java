package tei.com.hchl.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import tei.com.hchl.notification.NotificationUtil;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Notification 갱신
        NotificationUtil.setNotification(context);
    }
}
