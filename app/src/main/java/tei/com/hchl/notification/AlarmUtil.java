package tei.com.hchl.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class AlarmUtil {
    private static final int ALARM_ID = 1;

    public static void setAlarm(Context context, int syncTerm) {
        cancelAlarm(context);

        Intent intent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(context.getApplicationContext(), ALARM_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.SECOND, 0);

        // 동기화 주기에 따른 가장 가까운 실행 시간 계산
        // ex) 주기 15분, 현시간 2시 11분일 경우, 2시 15분을 구하는 로직
        switch (syncTerm) {
            case 1:
                cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE)+1);
                break;
            case 5:
                cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE)+(5-cal.get(Calendar.MINUTE)%5));
                break;
            case 15:
                cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE)+(15-cal.get(Calendar.MINUTE)%15));
                break;
            case 30:
                cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE)+(30-cal.get(Calendar.MINUTE)%30));
                break;
            case 60:
                cal.set(Calendar.HOUR, cal.get(Calendar.HOUR)+1);
                cal.set(Calendar.MINUTE, 0);
                break;
            case 360:
                cal.set(Calendar.DATE, cal.get(Calendar.DATE)+1);
                cal.set(Calendar.HOUR, 0);
                cal.set(Calendar.MINUTE, 0);
                break;
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), syncTerm*60*1000, pIntent);
    }

    public static void cancelAlarm(Context context) {
        Intent intent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(context.getApplicationContext(), ALARM_ID, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pIntent);
    }
}
