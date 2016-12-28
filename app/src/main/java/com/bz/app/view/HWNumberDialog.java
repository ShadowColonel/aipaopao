package com.bz.app.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.bz.app.R;

/**
 * Created by ThinkPad User on 2016/11/30.
 */

public class HWNumberDialog extends Dialog {

    private Button cancel;
    private Button save;
    private TextView titleTx;  //标题
    private String titleStr;   //从外界设置的标题
    private NumberPicker num1Picker;
    private NumberPicker num2Picker;
    private int num1Max;
    private int num1Min;
    private int num1Value;
    private OnCancelListener cancelListener;  //取消和保存按钮的监听器
    private OnSaveListener saveListener;

    public void setOnCancelListener(OnCancelListener onCancelOnclickListener) {
        this.cancelListener = onCancelOnclickListener;
    }

    public void setOnSaveclickListener(OnSaveListener onSaveOnclickListener) {
        this.saveListener = onSaveOnclickListener;
    }


    public HWNumberDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.number_dialog);
        setCanceledOnTouchOutside(false);

        initView();
        initDate();
        initEvent();
    }

    private void initEvent() {

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancelListener != null) {
                    cancelListener.onCancelClick();
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saveListener != null) {
                    saveListener.onSaveClick();
                }
            }
        });
    }

    private void initDate() {
        if (titleStr != null) {
            titleTx.setText(titleStr);
        }
        num2Picker.setValue(5);
        num2Picker.setMaxValue(9);
        num2Picker.setMinValue(0);

        num1Picker.setMaxValue(num1Max);
        num1Picker.setMinValue(num1Min);
        num1Picker.setValue(num1Value);

    }

    private void initView() {
        cancel = (Button) findViewById(R.id.cancel);
        save = (Button) findViewById(R.id.save);
        titleTx = (TextView) findViewById(R.id.dialog_title);
        num1Picker = (NumberPicker) findViewById(R.id.num1);
        num2Picker = (NumberPicker) findViewById(R.id.num2);
    }

    public void setTitle(String title) {
        titleStr = title;
    }

    public void setNum1Max(int num1Max) {
        this.num1Max = num1Max;
    }

    public void setNum1Min(int num1Min) {
        this.num1Min = num1Min;
    }

    public void setNum1Value(int num1Value) {
        this.num1Value = num1Value;
    }

    public int getNum1Value() {
        return num1Picker.getValue();
    }

    public int getNum2Value() {
        return num2Picker.getValue();
    }

    public interface OnCancelListener {
        void onCancelClick();
    }

    public interface OnSaveListener {
        void onSaveClick();
    }

}
