package tei.com.hchl.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import tei.com.hchl.util.NotificationUtil;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Notification 갱신
        NotificationUtil.setNotification(context);
    }
}
