package com.bz.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ButtonActivity extends AppCompatActivity {

    @BindView(R.id.toggleButton)
    ToggleButton mButton1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button);
        ButterKnife.bind(this);

        mButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mButton1.setChecked(isChecked);
                mButton1.setBackgroundResource(isChecked?R.drawable.pass_button_pressed:R.drawable.pass_button_normal);
            }
        });
    }

}
