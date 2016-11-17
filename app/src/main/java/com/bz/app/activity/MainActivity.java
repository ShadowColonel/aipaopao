package com.bz.app.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.bz.app.IRunning;
import com.bz.app.IRunningCallback;
import com.bz.app.database.DatabaseHelper;
import com.bz.app.entity.RunningRecord;
import com.bz.app.service.LocationService;
import com.bz.app.R;
import com.bz.app.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextureMapView mMapView = null;  //地图view
    private AMap aMap;  //地图对象
    private UiSettings mUiSettings;
    private PolylineOptions mPolyOptions;  //在地图上画出轨迹
    private RunningRecord mRecord;  //一条跑步记录
    private Marker mLocationMarker = null;
    private ArrayList<LatLng> latLngs = new ArrayList<>(); //经纬度集合
    private TextView mTimeTV;  //时间
    private TextView mDistanceTV; //距离
    private ToggleButton mRunningRecordTBtn;  //记录跑步
    private IRunning mRunning;  //AIDL接口
    private long mStartTime;
    private long mEndTime;

    private static final String LOG_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mMapView = (TextureMapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        init();
        initPolyline();


        DatabaseHelper here = new DatabaseHelper(this);
        SQLiteDatabase db = here.getWritableDatabase();

    }

    private void init() {

        if (aMap == null) {
            aMap = mMapView.getMap();
            mUiSettings = aMap.getUiSettings();
            aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
            //地图初始缩放级别
            aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
        }

        //显示跑步计时
        mTimeTV = (TextView) findViewById(R.id.activity_main_time_tv);
        //显示跑步距离
        mDistanceTV = (TextView) findViewById(R.id.activity_main_distance_tv);
        //记录跑步按钮
        mRunningRecordTBtn = (ToggleButton) findViewById(R.id.main_running_record);
        mRunningRecordTBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (mRunningRecordTBtn.isChecked()) {
                        aMap.clear(true);
                        if (mRecord != null) mRecord = null;
                        mRecord = new RunningRecord();
                        mStartTime = System.currentTimeMillis();
                        mRecord.setDate(getCurrentDate(mStartTime));
                        mRunning.start();

                    } else {
                        mRunning.stop();
                        mEndTime = System.currentTimeMillis();
                        saveRecord(mRecord.getPathLinePoints(), mRecord.getDate());
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //保存到数据库
    private void saveRecord(List<AMapLocation> aMapLocations, String s) {
    }

    private void initPolyline() {
        mPolyOptions = new PolylineOptions();
        mPolyOptions.color(Color.GREEN);
        mPolyOptions.width(10f);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_start) {
            // Handle the camera action
        } else if (id == R.id.nav_data) {

        } else if (id == R.id.nav_history) {

            Intent historyIntent = new Intent(this, HistoryActivity.class);
            startActivity(historyIntent);

        } else if (id == R.id.nav_setting) {

        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_about) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mRunning = IRunning.Stub.asInterface(service);
            try {
                mRunning.registCallback(callback);
                if (mRunning.isRunning()){
                    mRunningRecordTBtn.setChecked(true);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    private IRunningCallback callback = new IRunningCallback.Stub() {
        @Override
        public void notifyData(float distance, String latLngsListStr, String nowLatLngStr) throws RemoteException {
            mDistanceHandler.sendEmptyMessage((int) distance);
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<LatLng>>(){}.getType();
            //经纬度集合，
            latLngs = gson.fromJson(latLngsListStr, type);

            if (latLngs != null && latLngs.size() > 0) {
                //轨迹
                aMap.addPolyline(new PolylineOptions().addAll(latLngs).width(10).color(Color.GREEN));
            }
            //当前位置
            LatLng nowLatLng = gson.fromJson(nowLatLngStr, LatLng.class);
            if (mLocationMarker == null) {
                mLocationMarker = aMap.addMarker(new MarkerOptions()
                        .position(nowLatLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker)));
            } else {
                mLocationMarker.setPosition(nowLatLng);
            }
            //每次定位移动到地图中心
            aMap.moveCamera(CameraUpdateFactory.changeLatLng(nowLatLng));
        }

        @Override
        public void timeUpdate(long time) throws RemoteException {
            mTimeHandler.sendEmptyMessage((int)(time / 1000));
        }
    };

    //更新距离
    private Handler mDistanceHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            mDistanceTV.setText(msg.what + "m");
            return false;
        }
    });

    //更新时间
    private Handler mTimeHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            mTimeTV.setText(Utils.getTimeStr(msg.what));
            return false;
        }
    });

    private String getCurrentDate(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
        Date curDate = new Date(time);
        String date = format.format(curDate);
        return date;
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

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, LocationService.class);
        startService(intent);
        bindService(intent, connection, BIND_AUTO_CREATE);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }

}
