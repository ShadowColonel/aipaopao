package com.bz.app.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.bz.app.R;
import com.bz.app.view.HWNumberDialog;

import java.util.Calendar;

public class PersonalActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mPortraitImg;
    private TextView mNameTx;
    private ImageView mEditNameImg;
    private TextView mGenderTx;
    private TextView mAgeTx;
    private TextView mHeightTx;
    private TextView mWeightTx;
    private String mPortraitPath;
    private static final String LOG_TAG = "PersonalActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("个人信息");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
    }

    private void init() {
        mPortraitImg = (ImageView) findViewById(R.id.personal_portrait);
        mNameTx = (TextView) findViewById(R.id.personal_name);
        mEditNameImg = (ImageView) findViewById(R.id.personal_edit_name);
        mGenderTx = (TextView) findViewById(R.id.personal_gender);
        mAgeTx = (TextView) findViewById(R.id.personal_age);
        mHeightTx = (TextView) findViewById(R.id.personal_height);
        mWeightTx = (TextView) findViewById(R.id.personal_weight);
        mPortraitImg.setOnClickListener(this);
        mEditNameImg.setOnClickListener(this);
        mGenderTx.setOnClickListener(this);
        mAgeTx.setOnClickListener(this);
        mHeightTx.setOnClickListener(this);
        mWeightTx.setOnClickListener(this);

        SharedPreferences pref = getSharedPreferences("user", MODE_PRIVATE);
        mGenderTx.setText(pref.getString("gender", "男"));
        mAgeTx.setText(pref.getString("age", "18"));
        mHeightTx.setText(pref.getString("height", "170"));
        mWeightTx.setText(pref.getString("weight", "65"));
        mNameTx.setText(pref.getString("name", "username"));
        mPortraitPath = pref.getString("path", "");
        mPortraitImg.setImageURI(Uri.parse(mPortraitPath));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.personal_portrait:
                choosePortrait();
                break;
            case R.id.personal_edit_name:
                editName();
                break;
            case R.id.personal_gender:
                chooseGender();
                break;
            case R.id.personal_age:
                chooseAge();
                break;
            case R.id.personal_height:
                chooseHeight();
                break;
            case R.id.personal_weight:
                chooseWeight();
                break;
        }
    }

    private void choosePortrait() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String[] gender = {"图库", "相机"};
        builder.setItems(gender, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 0);
                } else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 1);
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            mPortraitPath = uri.toString();
            mPortraitImg.setImageURI(uri);
        } else if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            mPortraitImg.setImageBitmap(bitmap);
        }
    }

    private void editName() {
        final EditText et = new EditText(this);
        new AlertDialog.Builder(this).setTitle("修改用户昵称")
                .setView(et)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if (input.equals("")) {
                            return;
                        } else {
                            mNameTx.setText(input);
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void chooseWeight() {
        final HWNumberDialog weightDialog = new HWNumberDialog(this);
        weightDialog.setTitle("体重：kg");
        weightDialog.setNum1Max(150);
        weightDialog.setNum1Min(35);
        weightDialog.setNum1Value(60);
        weightDialog.setOnCancelListener(new HWNumberDialog.OnCancelListener() {
            @Override
            public void onCancelClick() {
                weightDialog.dismiss();
            }
        });

        weightDialog.setOnSaveclickListener(new HWNumberDialog.OnSaveListener() {
            @Override
            public void onSaveClick() {
                mWeightTx.setText(weightDialog.getNum1Value() + "." + weightDialog.getNum2Value());
                weightDialog.dismiss();
            }
        });

        weightDialog.show();
    }

    private void chooseHeight() {
        final HWNumberDialog heightDialog = new HWNumberDialog(this);
        heightDialog.setTitle("身高：cm");
        heightDialog.setNum1Max(220);
        heightDialog.setNum1Min(140);
        heightDialog.setNum1Value(165);
        heightDialog.setOnCancelListener(new HWNumberDialog.OnCancelListener() {
            @Override
            public void onCancelClick() {
                heightDialog.dismiss();
            }
        });

        heightDialog.setOnSaveclickListener(new HWNumberDialog.OnSaveListener() {
            @Override
            public void onSaveClick() {
                mHeightTx.setText(heightDialog.getNum1Value() + "." + heightDialog.getNum2Value());
                heightDialog.dismiss();
            }
        });

        heightDialog.show();

    }

    private void chooseAge() {
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mAgeTx.setText(Calendar.getInstance().get(Calendar.YEAR) - year + "");
            }
        }, 2000, 0, 1).show();
    }

    private void chooseGender() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String[] gender = {"男", "女"};
        builder.setItems(gender, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mGenderTx.setText(gender[which]);
            }
        });
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = getSharedPreferences("user", MODE_PRIVATE).edit();
        editor.putString("gender", mGenderTx.getText().toString());
        editor.putString("age", mAgeTx.getText().toString());
        editor.putString("height", mHeightTx.getText().toString());
        editor.putString("weight", mWeightTx.getText().toString());
        editor.putString("name", mNameTx.getText().toString());
        editor.putString("path", mPortraitPath);
        editor.commit();
    }
}
