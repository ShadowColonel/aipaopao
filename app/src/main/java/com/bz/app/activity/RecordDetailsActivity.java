package com.bz.app.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.bz.app.R;
import com.bz.app.entity.RunningRecord;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordDetailsActivity extends AppCompatActivity {

    private AMap aMap;
    private TextureMapView mMapView;
    private PolylineOptions mPolyOptions;
    private MarkerOptions mStartMarker;
    private MarkerOptions mEndMarker;


    private TextView mDuration;
    private TextView mDistance;
    private TextView mAvgSpeed;
    private TextView mCalories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_details);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        RunningRecord record = bundle.getParcelable("record");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getCurrentDate(record.getDate()));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setOnMenuItemClickListener(onMenuItemClick);

        mDuration = (TextView) findViewById(R.id.details_duration);
        mDistance = (TextView) findViewById(R.id.details_distance);
        mAvgSpeed = (TextView) findViewById(R.id.details_avg_speed);
        mCalories = (TextView) findViewById(R.id.details_calories);
        mDistance.setText(record.getDistance());
        mDuration.setText(record.getDuration());
        mAvgSpeed.setText(record.getAverageSpeed());
        mCalories.setText("暂无数据");

        mMapView = (TextureMapView) findViewById(R.id.details_map);
        mMapView.onCreate(savedInstanceState);

        aMap = mMapView.getMap();
        aMap.moveCamera(CameraUpdateFactory.zoomTo(16));

        //初始化线
        mPolyOptions = new PolylineOptions();
        mPolyOptions.color(Color.GREEN);
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
