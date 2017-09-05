package tei.com.hchl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import tei.com.hchl.data.Data;
import tei.com.hchl.game.Game;
import tei.com.hchl.game.GameUtil;

public class MainActivity extends Activity {
    private LinearLayout mMainContentLayout;

    private TextView mMainVS;
    private TextView mMainSeriesNo;
    private ImageView mMainAwayEmblem;
    private TextView mMainAwayPitcher;
    private ImageView mMainHomeEmblem;
    private TextView mMainHomePitcher;
    private TextView mMainGameTime;

    private ImageButton mMainSettingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMainContentLayout = (LinearLayout) findViewById(R.id.main_content_layout);

        mMainVS = (TextView) findViewById(R.id.main_vs);
        mMainSeriesNo = (TextView) findViewById(R.id.main_series_no);
        mMainAwayEmblem = (ImageView) findViewById(R.id.main_away_emblem);
        mMainAwayPitcher = (TextView) findViewById(R.id.main_away_pitcher);
        mMainHomeEmblem = (ImageView) findViewById(R.id.main_home_emblem);
        mMainHomePitcher = (TextView) findViewById(R.id.main_home_pitcher);
        mMainGameTime = (TextView) findViewById(R.id.main_game_time);

        mMainSettingButton = (ImageButton)  findViewById(R.id.main_setting_button);

        updateGameInfo();
        mMainContentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateGameInfo();
            }
        });

        mMainSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
    }

    private void updateGameInfo() {
        String teamCode = getResources().getStringArray(R.array.team_code)[Data.getInt(getApplicationContext(), "team", 0)];
        Game game = GameUtil.getGame(teamCode);

        if (game != null) {
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
        }
    }
}
