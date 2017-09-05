package tei.com.hchl.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import tei.com.hchl.R;
import tei.com.hchl.notification.AlarmUtil;
import tei.com.hchl.data.Data;
import tei.com.hchl.notification.NotificationUtil;

/**
 * Created by ktds on 2017-07-05.
 */

public class StartReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Notification 실행
        boolean alarmYn = Data.getBoolean(context, "alarm_yn", false);
        if (alarmYn) {
            int syncTerm = context.getApplicationContext().getResources().getIntArray(R.array.sync_term_code)[Data.getInt(context, "sync_term", 0)];

            NotificationUtil.setNotification(context.getApplicationContext());
            AlarmUtil.setAlarm(context.getApplicationContext(), syncTerm);
        } else {
            NotificationUtil.releaseAlarm(context.getApplicationContext());
            AlarmUtil.cancelAlarm(context.getApplicationContext());
        }
    }
}
