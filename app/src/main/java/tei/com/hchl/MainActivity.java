package tei.com.hchl;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import tei.com.hchl.domain.KboGameDate;
import tei.com.hchl.util.DataUtil;
import tei.com.hchl.domain.KboGame;
import tei.com.hchl.util.GameUtil;
import tei.com.hchl.util.AlarmUtil;
import tei.com.hchl.util.NotificationUtil;

public class MainActivity extends Activity {
    private LinearLayout mMainContentLayout;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    LinearLayout mSideLayout;

    private TextView mBeforeGameInfo;
    private TextView mBeforeGameResult;
    private TextView mBeforeBeforeGameInfo;
    private TextView mBeforeBeforeGameResult;

    private TextView mMyTeamVsText;
    private TextView mMyTeamVsGameCount;
    private TextView mMyTeamTimePlace;
    private TextView mMyTeamAwayNm;
    private TextView mMyTeamAwayPitcherNm;
    private TextView mMyTeamHomeNm;
    private TextView mMyTeamHomePitcherNm;
    private TextView mMyTeamStatus;

    private TextView[] mOtherTeamArray;

    private Spinner mSettingTeam;
    private Spinner mSettingSyncTerm;
    private Switch mSettingAlarmYn;
    private ImageView mSettingMyTeamEmblem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMainContentLayout = (LinearLayout) findViewById(R.id.main);
        mSideLayout = (LinearLayout) findViewById(R.id.side);

        mBeforeGameInfo = (TextView) findViewById(R.id.before_game_info);
        mBeforeGameResult = (TextView) findViewById(R.id.before_game_result);
        mBeforeBeforeGameInfo = (TextView) findViewById(R.id.before_before_game_info);
        mBeforeBeforeGameResult = (TextView) findViewById(R.id.before_before_game_result);

        mMyTeamVsText = (TextView) findViewById(R.id.my_team_vs_text);
        mMyTeamVsGameCount = (TextView) findViewById(R.id.my_team_vs_game_count);
        mMyTeamTimePlace = (TextView) findViewById(R.id.my_team_time_place);
        mMyTeamAwayNm = (TextView) findViewById(R.id.my_team_away_nm);
        mMyTeamAwayPitcherNm = (TextView) findViewById(R.id.my_team_away_pitcher_nm);
        mMyTeamHomeNm = (TextView) findViewById(R.id.my_team_home_nm);
        mMyTeamHomePitcherNm = (TextView) findViewById(R.id.my_team_home_pitcher_nm);
        mMyTeamStatus = (TextView) findViewById(R.id.my_team_status);

        mOtherTeamArray = new TextView[4];
        mOtherTeamArray[0] = (TextView) findViewById(R.id.other_team_1);
        mOtherTeamArray[1] = (TextView) findViewById(R.id.other_team_2);
        mOtherTeamArray[2] = (TextView) findViewById(R.id.other_team_3);
        mOtherTeamArray[3] = (TextView) findViewById(R.id.other_team_4);

        mSettingTeam = (Spinner) findViewById(R.id.setting_team);
        mSettingSyncTerm = (Spinner) findViewById(R.id.setting_sync_term);
        mSettingAlarmYn = (Switch) findViewById(R.id.setting_alarm_yn);
        mSettingMyTeamEmblem = (ImageView) findViewById(R.id.setting_my_team_emblem);

        setBeforeGameResult();
        setTodayGame();

        mMainContentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTodayGame();
            }
        });

        mSettingTeam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DataUtil.save(getApplicationContext(), "team", position);
                changeSideBarColor();
                setNotification();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        mSettingSyncTerm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DataUtil.save(getApplicationContext(), "sync_term", position);
                setNotification();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        mSettingAlarmYn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataUtil.save(getApplicationContext(), "alarm_yn", mSettingAlarmYn.isChecked());
                setNotification();
            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, null, R.string.drawer_open, R.string.drawer_close) {


            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);

                setBeforeGameResult();
                setTodayGame();

                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                changeSideBarColor();

                mSettingTeam.setSelection(DataUtil.getInt(getApplicationContext(), "team", 0));
                mSettingSyncTerm.setSelection(DataUtil.getInt(getApplicationContext(), "sync_term", 0));
                mSettingAlarmYn.setChecked(DataUtil.getBoolean(getApplicationContext(), "alarm_yn", false));

                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    private void changeSideBarColor() {
        String teamCode = getResources().getStringArray(R.array.team_code)[DataUtil.getInt(getApplicationContext(), "team", 0)];
        mSideLayout.setBackgroundResource(getResources().getIdentifier(teamCode.toLowerCase(), "color", getPackageName()));
        mSettingMyTeamEmblem.setImageResource(getResources().getIdentifier("emblem_l_"+teamCode.toLowerCase(), "drawable", getPackageName()));
    }

    private void setBeforeGameResult() {
        String teamCode = getResources().getStringArray(R.array.team_code)[DataUtil.getInt(getApplicationContext(), "team", 0)];

        GameUtil gameUtil = new GameUtil();
        KboGame beforeKboGame = gameUtil.getBeforeKboGame(teamCode);
        KboGame beforeBeforeKboGame = gameUtil.getBeforeBeforeKboGame(teamCode);

        int beforeHomeScore = Integer.parseInt(beforeKboGame.getB_SCORE_CN());
        int beforeAwayScore = Integer.parseInt(beforeKboGame.getT_SCORE_CN());

        int beforeBeforeHomeScore = Integer.parseInt(beforeBeforeKboGame.getB_SCORE_CN());
        int beforeBeforeAwayScore = Integer.parseInt(beforeBeforeKboGame.getT_SCORE_CN());

        if (teamCode.equals(beforeKboGame.getHOME_ID())) {
            mBeforeGameInfo.setText(beforeKboGame.getG_DT_TXT() + "\n" + "vs " + beforeKboGame.getAWAY_NM());

            if (beforeKboGame.getGAME_STATE_SC().equals("4")) {
                mBeforeGameResult.setText(beforeKboGame.getCANCEL_SC_NM());
            } else {
                if (beforeHomeScore == beforeAwayScore) {
                    mBeforeGameResult.setText("무승부");
                } else if (beforeHomeScore > beforeAwayScore) {
                    mBeforeGameResult.setText("승" + "\n" + beforeKboGame.getW_PIT_P_NM() + "(승)" + beforeKboGame.getL_PIT_P_NM() + "(패)");
                } else {
                    mBeforeGameResult.setText("패" + "\n" + beforeKboGame.getW_PIT_P_NM() + "(승)" + beforeKboGame.getL_PIT_P_NM() + "(패)");
                }
            }
        } else {
            mBeforeGameInfo.setText(beforeKboGame.getG_DT_TXT() + "\n" + "vs " + beforeKboGame.getHOME_NM());

            if (beforeKboGame.getGAME_STATE_SC().equals("4")) {
                mBeforeGameResult.setText(beforeKboGame.getCANCEL_SC_NM());
            } else {
                if (beforeHomeScore == beforeAwayScore) {
                    mBeforeGameResult.setText("무승부");
                } else if (beforeHomeScore > beforeAwayScore) {
                    mBeforeGameResult.setText("패" + "\n" + beforeKboGame.getW_PIT_P_NM() + "(승)" + beforeKboGame.getL_PIT_P_NM() + "(패)");
                } else {
                    mBeforeGameResult.setText("승" + "\n" + beforeKboGame.getW_PIT_P_NM() + "(승)" + beforeKboGame.getL_PIT_P_NM() + "(패)");
                }
            }
        }

        if (teamCode.equals(beforeBeforeKboGame.getHOME_ID())) {
            mBeforeBeforeGameInfo.setText(beforeBeforeKboGame.getG_DT_TXT() + "\n" + "vs " + beforeBeforeKboGame.getAWAY_NM());

            if (beforeBeforeKboGame.getGAME_STATE_SC().equals("4")) {
                mBeforeBeforeGameResult.setText(beforeBeforeKboGame.getCANCEL_SC_NM());
            } else {
                if (beforeBeforeHomeScore == beforeBeforeAwayScore) {
                    mBeforeBeforeGameResult.setText("무승부");
                } else if (beforeBeforeHomeScore > beforeBeforeAwayScore) {
                    mBeforeBeforeGameResult.setText("승" + "\n" + beforeBeforeKboGame.getW_PIT_P_NM() + "(승)" + beforeBeforeKboGame.getL_PIT_P_NM() + "(패)");
                } else {
                    mBeforeBeforeGameResult.setText("패" + "\n" + beforeBeforeKboGame.getW_PIT_P_NM() + "(승)" + beforeBeforeKboGame.getL_PIT_P_NM() + "(패)");
                }
            }
        } else {
            mBeforeBeforeGameInfo.setText(beforeBeforeKboGame.getG_DT_TXT() + "\n" + "vs " + beforeBeforeKboGame.getHOME_NM());

            if (beforeBeforeKboGame.getGAME_STATE_SC().equals("4")) {
                mBeforeBeforeGameResult.setText(beforeBeforeKboGame.getCANCEL_SC_NM());
            } else {
                if (beforeBeforeHomeScore == beforeBeforeAwayScore) {
                    mBeforeBeforeGameResult.setText("무승부");
                } else if (beforeBeforeHomeScore > beforeBeforeAwayScore) {
                    mBeforeBeforeGameResult.setText("패" + "\n" + beforeBeforeKboGame.getW_PIT_P_NM() + "(승)" + beforeBeforeKboGame.getL_PIT_P_NM() + "(패)");
                } else {
                    mBeforeBeforeGameResult.setText("승" + "\n" + beforeBeforeKboGame.getW_PIT_P_NM() + "(승)" + beforeBeforeKboGame.getL_PIT_P_NM() + "(패)");
                }
            }
        }
    }

    private void setTodayGame() {
        String teamCode = getResources().getStringArray(R.array.team_code)[DataUtil.getInt(getApplicationContext(), "team", 0)];

        GameUtil gameUtil = new GameUtil();
        KboGameDate kboGameDate = gameUtil.getKboGameDate(gameUtil.getDate());

        if (kboGameDate.getNOW_G_DT() != null) {
            List<KboGame> kboGameList = gameUtil.getKboGameList(gameUtil.getDate());

            int otherTeamIndex = 0;
            for (KboGame game : kboGameList) {
                if (game.getHOME_ID().equals(teamCode) || game.getAWAY_ID().equals(teamCode)) {
                    mMyTeamVsText.setText("vs");
                    mMyTeamVsGameCount.setText(game.getVS_GAME_CN()+"차전");
                    mMyTeamTimePlace.setText(" | " + game.getG_TM() + " " + game.getS_NM());
                    mMyTeamAwayNm.setText(game.getAWAY_NM());
                    mMyTeamAwayPitcherNm.setText(game.getT_PIT_P_NM());
                    mMyTeamHomeNm.setText(game.getHOME_NM());
                    mMyTeamHomePitcherNm.setText(game.getB_PIT_P_NM());

                    if (game.getGAME_STATE_SC().equals("1")) {
                        mMyTeamStatus.setText("경기 시작 전");
                    } else if (game.getGAME_STATE_SC().equals("2")) {
                        mMyTeamStatus.setText(game.getT_SCORE_CN()+":"+game.getB_SCORE_CN());
                    } else if (game.getGAME_STATE_SC().equals("3")) {
                        mMyTeamStatus.setText(game.getT_SCORE_CN()+":"+game.getB_SCORE_CN()+"\n"+game.getW_PIT_P_NM()+"(승) "+game.getL_PIT_P_NM()+"(패)");
                    } else if (game.getGAME_STATE_SC().equals("4")) {
                        mMyTeamStatus.setText(game.getCANCEL_SC_NM());
                    } else if (game.getGAME_STATE_SC().equals("5")) {
                        mMyTeamStatus.setText("경기 중단");
                    }
                } else {
                    String statusStr = "";
                    if (game.getGAME_STATE_SC().equals("1")) {
                        statusStr = "-";
                    } else if (game.getGAME_STATE_SC().equals("2")) {
                        statusStr = game.getGAME_INN_NO() + game.getGAME_TB_SC_NM() + " " + game.getOUT_CN() + "사" + "\n" + game.getT_SCORE_CN()+":"+game.getB_SCORE_CN();
                    } else if (game.getGAME_STATE_SC().equals("3")) {
                        statusStr = game.getT_SCORE_CN()+":"+game.getB_SCORE_CN();
                    } else if (game.getGAME_STATE_SC().equals("4")) {
                        statusStr = game.getCANCEL_SC_NM();
                    } else if (game.getGAME_STATE_SC().equals("5")) {
                        statusStr = "중단" + "\n" + game.getT_SCORE_CN()+":"+game.getB_SCORE_CN();
                    }

                    mOtherTeamArray[otherTeamIndex].setText(game.getAWAY_NM() + " vs " + game.getHOME_NM() + "\n" + statusStr);

                    otherTeamIndex++;
                }
            }
        } else {
            mMyTeamVsText.setText("오늘은 경기가 없습니다.");
            mMyTeamVsGameCount.setText("");
            mMyTeamTimePlace.setText("");
            mMyTeamAwayNm.setText("");
            mMyTeamAwayPitcherNm.setText("");
            mMyTeamHomeNm.setText("");
            mMyTeamHomePitcherNm.setText("");
            mMyTeamStatus.setText("");

            mOtherTeamArray[0].setText("-");
            mOtherTeamArray[1].setText("-");
            mOtherTeamArray[2].setText("-");
            mOtherTeamArray[3].setText("-");
        }
    }

    private void updateGameInfo() {
        String teamCode = getResources().getStringArray(R.array.team_code)[DataUtil.getInt(getApplicationContext(), "team", 0)];

        GameUtil gameUtil = new GameUtil();
        KboGame nowKboGame = gameUtil.getKboGame(teamCode, gameUtil.getDate());

        /*if (game != null) {
            mMainSeriesNo.setText(game.getVS_GAME_CN()+"차전");
            mMainAwayEmblem.setImageResource(getResources().getIdentifier("emblem_l_"+game.getAWAY_ID().toLowerCase(), "drawable", getPackageName()));
            mMainAwayPitcher.setText(game.getT_PIT_P_NM().trim());
            mMainVS.setText("vs");
            mMainHomeEmblem.setImageResource(getResources().getIdentifier("emblem_l_"+game.getHOME_ID().toLowerCase(), "drawable", getPackageName()));
            mMainHomePitcher.setText(game.getB_PIT_P_NM().trim());
            mMainGameTime.setText(game.getG_TM().trim() + "\n" + game.getS_NM().trim());

            if (game.getGAME_STATE_SC().equals("4")) { // 경기취소
                mMainGameTime.setText(game.getCANCEL_SC_NM());
            }
        } else {
            mMainSeriesNo.setText("");
            mMainAwayEmblem.setImageResource(android.R.color.transparent);
            mMainVS.setText(":(");
            mMainAwayPitcher.setText("");
            mMainHomeEmblem.setImageResource(android.R.color.transparent);
            mMainHomePitcher.setText("");
            mMainGameTime.setText("오늘은 경기가 없는 날입니다.");
        }*/
    }

    public void setNotification() {
        if (mSettingAlarmYn.isChecked()) {
            int syncTerm = getApplicationContext().getResources().getIntArray(R.array.sync_term_code)[DataUtil.getInt(getApplicationContext(), "sync_term", 0)];

            NotificationUtil.setNotification(getApplicationContext());
            AlarmUtil.setAlarm(getApplicationContext(), syncTerm);
        } else {
            NotificationUtil.releaseAlarm(getApplicationContext());
            AlarmUtil.cancelAlarm(getApplicationContext());
        }
    }
}
