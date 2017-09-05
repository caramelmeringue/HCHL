package tei.com.hchl.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import tei.com.hchl.game.Game;
import tei.com.hchl.game.GameUtil;
import tei.com.hchl.MainActivity;
import tei.com.hchl.R;
import tei.com.hchl.data.Data;

public class NotificationUtil {
    final static int NOTIFICATION_CODE = 0;

    public static void setNotification(Context context) {
        String teamCode = context.getResources().getStringArray(R.array.team_code)[Data.getInt(context, "team", 0)];
        Game game = GameUtil.getGame(teamCode);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(context.getResources().getIdentifier("emblem_s_"+teamCode.toLowerCase(), "drawable", context.getPackageName()))
                .setPriority(NotificationCompat.PRIORITY_MAX);

        if (game != null) {
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier("emblem_l_"+(game.getHOME_ID().equals(teamCode)?game.getAWAY_ID():game.getHOME_ID()).toLowerCase(), "drawable", context.getPackageName())))
                   .setContentTitle(game.getAWAY_NM() + " vs " + game.getHOME_NM() + " " + game.getVS_GAME_CN() + "차전");

            if (game.getGAME_STATE_SC().equals("1")) { // 경기전
                Data.save(context, "home_pitcher", game.getB_PIT_P_NM());
                Data.save(context, "away_pitcher", game.getT_PIT_P_NM());

                builder.setContentText("경기전 "+game.getT_PIT_P_NM().trim()+" vs "+game.getB_PIT_P_NM().trim()+" ("+game.getG_TM()+" "+game.getS_NM()+")");
            } else if (game.getGAME_STATE_SC().equals("2")) { // 경기중
                if (game.getGAME_TB_SC().equals("T")) {
                    Data.save(context, "home_pitcher", game.getB_P_NM().trim());

                    String awayPitcher = Data.getString(context, "away_pitcher", game.getT_P_NM().trim());
                    builder.setContentText(game.getGAME_INN_NO()+game.getGAME_TB_SC_NM()+" "+awayPitcher+" vs "+game.getB_P_NM().trim()+" ("+game.getT_SCORE_CN()+":"+game.getB_SCORE_CN()+")");
                } else if (game.getGAME_TB_SC().equals("B")) {
                    Data.save(context, "away_pitcher", game.getT_P_NM().trim());

                    String homePitcher = Data.getString(context, "home_pitcher", game.getB_P_NM().trim());
                    builder.setContentText(game.getGAME_INN_NO()+game.getGAME_TB_SC_NM()+" "+game.getT_P_NM().trim()+" vs "+homePitcher+" ("+game.getT_SCORE_CN()+":"+game.getB_SCORE_CN()+")");
                } else {
                    builder.setContentText(game.getGAME_INN_NO()+game.getGAME_TB_SC_NM()+" "+game.getT_P_NM().trim()+" vs "+game.getB_P_NM().trim()+" ("+game.getT_SCORE_CN()+":"+game.getB_SCORE_CN()+")");
                }
            } else if (game.getGAME_STATE_SC().equals("3")) { // 경기종료
                builder.setContentText("경기종료 "+game.getT_SCORE_CN()+":"+game.getB_SCORE_CN()+" "+game.getW_PIT_P_NM()+"(승) "+game.getL_PIT_P_NM()+"(패)");
            } else if (game.getGAME_STATE_SC().equals("4")) { // 경기취소
                builder.setContentText(game.getCANCEL_SC_NM());
            } else if (game.getGAME_STATE_SC().equals("5")) { // 중지
                builder.setContentText("서스펜디드 "+game.getGAME_INN_NO()+game.getGAME_TB_SC_NM()+" "+game.getT_P_NM().trim()+" vs "+game.getB_P_NM().trim()+" ("+game.getT_SCORE_CN()+":"+game.getB_SCORE_CN()+")");
            }
        } else {
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier("emblem_l_"+teamCode.toLowerCase(), "drawable", context.getPackageName())))
                   .setContentTitle(":(")
                   .setContentText("오늘은 경기가 없는 날입니다.");
        }

        Intent intent = new Intent(context.getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(context.getApplicationContext(), 0,intent, 0);
        builder.setContentIntent(pIntent);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_NO_CLEAR;
        manager.notify(NOTIFICATION_CODE, notification);
    }

    public static void releaseAlarm(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(NOTIFICATION_CODE);
    }
}
