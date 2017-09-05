package tei.com.hchl;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;

import tei.com.hchl.notification.AlarmUtil;
import tei.com.hchl.data.Data;
import tei.com.hchl.notification.NotificationUtil;

public class SettingActivity extends Activity {
    private ImageButton mSettingMainButton;

    private Spinner mSettingTeam;
    private Spinner mSettingSyncTerm;
    private Switch mSettingAlarmYn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mSettingTeam = (Spinner) findViewById(R.id.setting_team);
        mSettingSyncTerm = (Spinner) findViewById(R.id.setting_sync_term);
        mSettingAlarmYn = (Switch) findViewById(R.id.setting_alarm_yn);

        mSettingTeam.setSelection(Data.getInt(getApplicationContext(), "team", 0));
        mSettingSyncTerm.setSelection(Data.getInt(getApplicationContext(), "sync_term", 0));
        mSettingAlarmYn.setChecked(Data.getBoolean(getApplicationContext(), "alarm_yn", false));

        mSettingMainButton = (ImageButton) findViewById(R.id.setting_main_button);
        mSettingMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSettingTeam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Data.save(getApplicationContext(), "team", position);
                setNotification();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        mSettingSyncTerm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Data.save(getApplicationContext(), "sync_term", position);
                setNotification();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        mSettingAlarmYn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Data.save(getApplicationContext(), "alarm_yn", mSettingAlarmYn.isChecked());
                setNotification();
            }
        });
    }

    public void setNotification() {
        if (mSettingAlarmYn.isChecked()) {
            int syncTerm = getApplicationContext().getResources().getIntArray(R.array.sync_term_code)[Data.getInt(getApplicationContext(), "sync_term", 0)];

            NotificationUtil.setNotification(getApplicationContext());
            AlarmUtil.setAlarm(getApplicationContext(), syncTerm);
        } else {
            NotificationUtil.releaseAlarm(getApplicationContext());
            AlarmUtil.cancelAlarm(getApplicationContext());
        }
    }
}
