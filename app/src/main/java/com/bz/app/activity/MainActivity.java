package com.bz.app.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.bz.app.IRunning;
import com.bz.app.IRunningCallback;
import com.bz.app.database.DBAdapter;
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
    private PolylineOptions mPolyOptions;  //在地图上画出轨迹
    private RunningRecord mRecord;  //一条跑步记录
    private Marker mLocationMarker = null;
    private TextView mTimeTV;  //时间
    private TextView mDistanceTV; //距离
    private ToggleButton mRunningRecordTBtn;  //记录跑步
    private IRunning mRunning;  //AIDL接口
    private long mStartTime;
    private int mDistance;  //多少米
    private int mDuration;  //多少秒
    private DBAdapter mDBAdapter;

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
    }

    private void init() {

        if (aMap == null) {
            aMap = mMapView.getMap();
            aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
            //地图初始缩放级别
            aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
            mLocationMarker = aMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker)));
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
                        saveRecord(mRecord.getPathLinePoints(), mRecord.getDate());
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //保存到数据库
    protected void saveRecord(List<LatLng> list, String time) {
        if (list.size() > 0) {
            mDBAdapter = new DBAdapter(this);
            mDBAdapter.open();
            LatLng firstLocation = list.get(0);
            LatLng lastLocation = list.get(list.size() - 1);

            String startPoint = latLngToString(firstLocation);
            String endPoint = latLngToString(lastLocation);
            String pathLinePoints = getPathLineString(list);
            String distance = String.valueOf(mDistance);
            String duration = Utils.getTimeStr(mDuration);
            String average_speed = String.valueOf(mDistance / (float) mDuration);
            mDBAdapter.insertRecord(startPoint, endPoint, pathLinePoints, distance,
                    duration, average_speed, time);
            mDBAdapter.close();
        } else {
            Toast.makeText(MainActivity.this, "没有记录到数据库", Toast.LENGTH_SHORT).show();
        }

    }

    //将经纬度集合，转换为string
    private String getPathLineString(List<LatLng> list) {
        StringBuffer pathLine = new StringBuffer();

        for (int i = 1; i < list.size() - 1; i++) {
            pathLine.append(latLngToString(list.get(i))).append(";");
        }
        return pathLine.toString();
    }

    //将经纬度对象，转换为string
    private String latLngToString(LatLng latLng) {
        StringBuffer sb = new StringBuffer();
        sb.append(latLng.latitude).append(",");
        sb.append(latLng.longitude).append(",");
        return sb.toString();
    }

    //初始化地图轨迹线
    private void initPolyline() {
        mPolyOptions = new PolylineOptions();
        mPolyOptions.color(Color.GREEN);
        mPolyOptions.width(10f);
    }

    //在地图上画线
    private void drawLine() {
        aMap.clear(true);
        aMap.addPolyline(mPolyOptions);
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
    int j = 0;
    private IRunningCallback callback = new IRunningCallback.Stub() {
        @Override
        public void notifyData(float distance, String latLngListStr, String nowLatLngStr) throws RemoteException {
            mDistanceHandler.sendEmptyMessage((int) distance);

            Type type = new TypeToken<ArrayList<LatLng>>(){}.getType();
            //location集合
            Gson gson = new Gson();
            ArrayList<LatLng> locationList = gson.fromJson(latLngListStr, type);

            if (locationList.size() > 0) {
//                Log.e(LOG_TAG, "i-->" + (j++));
//                mRecord.setPathLinePoints(locationList);

                //地图上的轨迹
                for (int i = 0; i < locationList.size(); i++) {
                    mPolyOptions.add(locationList.get(i));
                }
                drawLine();
            }

            //当前定位
            LatLng nowLatLng = gson.fromJson(nowLatLngStr, LatLng.class);
            Log.v(LOG_TAG, "nowLatLng--->" + nowLatLng.latitude + "," + nowLatLng.longitude);
            mLocationMarker.setPosition(nowLatLng);
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
            mDistance = msg.what;
            mDistanceTV.setText(msg.what + "m");
            return false;
        }
    });

    //更新时间
    private Handler mTimeHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            mDuration = msg.what;
            mTimeTV.setText(Utils.getTimeStr(msg.what));
            return false;
        }
    });

    //格式化当前日期
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
        try {
            mRunning.unregistCallback(callback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
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
