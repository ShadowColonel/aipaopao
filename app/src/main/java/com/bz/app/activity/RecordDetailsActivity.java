package com.bz.app.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.bz.app.R;
import com.bz.app.database.DBAdapter;
import com.bz.app.entity.RunningRecord;
import com.bz.app.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecordDetailsActivity extends AppCompatActivity {

    private AMap aMap;
    private PolylineOptions mPolyOptions;
    private MarkerOptions mStartMarker;
    private MarkerOptions mEndMarker;
    private RunningRecord record;
    private MapView mMapView;

    @BindView(R.id.details_duration) TextView mDuration;
    @BindView(R.id.details_distance) TextView mDistance;
    @BindView(R.id.details_avg_speed) TextView mAvgSpeed;
    @BindView(R.id.details_calories) TextView mCalories;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_details);
        ButterKnife.bind(this);
        mMapView = (MapView) findViewById(R.id.details_map);
        mMapView.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        record = bundle.getParcelable("record");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getCurrentDate(record.getDate()));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setOnMenuItemClickListener(onMenuItemClick);

        initData();
        initMap();
    }

    private void initMap() {
        aMap = mMapView.getMap();
        aMap.moveCamera(CameraUpdateFactory.zoomTo(16));

        //初始化线
        mPolyOptions = new PolylineOptions();
        mPolyOptions.color(Color.parseColor("#E25910"));
        mPolyOptions.width(10f);
        mPolyOptions.addAll(record.getPathLinePoints());
        LatLng startLatLng = record.getPathLinePoints().get(0);
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(startLatLng));
        //划线
        aMap.addPolyline(mPolyOptions);

        mStartMarker = new MarkerOptions();
        mStartMarker.position(record.getStartPoint());
        mStartMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_st));
        aMap.addMarker(mStartMarker);

        mEndMarker = new MarkerOptions();
        mEndMarker.position(record.getEndPoint());
        mEndMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_en));
        aMap.addMarker(mEndMarker);
    }

    private static final String LOG = "RecordDetailsActivity";
    private void initData() {
        mDistance.setText(record.getDistance());
        mDuration.setText(Utils.getTimeStr(Integer.parseInt(record.getDuration())));
        mAvgSpeed.setText(record.getAverageSpeed());
        SharedPreferences preferences = getSharedPreferences("user", MODE_PRIVATE);
        String weight = preferences.getString("weight", "65");
        float cal = (float) (Float.parseFloat(weight) * Float.parseFloat(record.getDistance()) * 1.036);
        mCalories.setText(cal + "kcal");
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_record, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    deleteRecord();
                    break;
                case R.id.action_share:

                    break;
            }
            return false;
        }
    };

    private void deleteRecord() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setTitle("提示")
                .setMessage("确定删除本条记录？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DBAdapter mAdapter = new DBAdapter(RecordDetailsActivity.this);
                        mAdapter.open();
                        mAdapter.deleteRecord(record.getId());
                        Toast.makeText(RecordDetailsActivity.this, "已删除记录", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent("com.bz.broadcast.updatelist");
                        LocalBroadcastManager.getInstance(RecordDetailsActivity.this).sendBroadcast(intent);
                        finish();
                    }
                })
                .setNegativeButton("否", null)
                .create()
                .show();
    }


    //格式化当前日期
    private String getCurrentDate(String date) {
        long time = Long.parseLong(date);
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Date curDate = new Date(time);
        String d = format.format(curDate);
        return d;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }
}
