package com.bz.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.bz.app.R;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private TextView mPersonalTx;
    private TextView mAlertTx;
    private TextView mClearTx;
    private TextView mStatisticsTx;
    private Switch mNotificationSwitch;
    private Switch mUpdateSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting, "个人设置");

        init();
    }

    private void init() {
        mPersonalTx = (TextView) findViewById(R.id.setting_personal);
        mAlertTx = (TextView) findViewById(R.id.setting_alert);
        mClearTx = (TextView) findViewById(R.id.setting_clear);
        mStatisticsTx = (TextView) findViewById(R.id.setting_statistics);
        mNotificationSwitch = (Switch) findViewById(R.id.setting_notification);
        mUpdateSwitch = (Switch) findViewById(R.id.setting_update);
        mPersonalTx.setOnClickListener(this);
        mAlertTx.setOnClickListener(this);
        mClearTx.setOnClickListener(this);
        mStatisticsTx.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_personal:
                Intent personalIntent = new Intent(this, PersonalActivity.class);
                startActivity(personalIntent);
                break;
        }
    }
}
