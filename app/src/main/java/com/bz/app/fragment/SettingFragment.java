package com.bz.app.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.bz.app.R;
import com.bz.app.activity.PersonalActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by ThinkPad User on 2016/12/29.
 */

public class SettingFragment extends Fragment {

    private Unbinder mUnbinder;

    @BindView(R.id.setting_personal)
    TextView mPersonalTv;
    @BindView(R.id.setting_alert)
    TextView mAlertTv;
    @BindView(R.id.setting_clear)
    TextView mClearTv;
    @BindView(R.id.setting_statistics)
    TextView mStatisticsTv;
    @BindView(R.id.setting_notification)
    Switch mNotificationSwitch;
    @BindView(R.id.setting_update)
    Switch mUpdateSwitch;

    @OnClick(R.id.setting_personal) void setPersonal() {
        Intent personalIntent = new Intent(getActivity(), PersonalActivity.class);
        startActivity(personalIntent);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
