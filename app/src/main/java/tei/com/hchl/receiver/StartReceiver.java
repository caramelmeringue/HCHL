package tei.com.hchl.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import tei.com.hchl.R;
import tei.com.hchl.util.AlarmUtil;
import tei.com.hchl.util.DataUtil;
import tei.com.hchl.util.NotificationUtil;

/**
 * Created by ktds on 2017-07-05.
 */

public class StartReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Notification 실행
        boolean alarmYn = DataUtil.getBoolean(context, "alarm_yn", false);
        if (alarmYn) {
            int syncTerm = context.getApplicationContext().getResources().getIntArray(R.array.sync_term_code)[DataUtil.getInt(context, "sync_term", 0)];

            NotificationUtil.setNotification(context.getApplicationContext());
            AlarmUtil.setAlarm(context.getApplicationContext(), syncTerm);
        } else {
            NotificationUtil.releaseAlarm(context.getApplicationContext());
            AlarmUtil.cancelAlarm(context.getApplicationContext());
        }
    }
}
