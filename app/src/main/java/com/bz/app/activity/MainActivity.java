package com.bz.app.activity;

import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.bz.app.service.LocationService;
import com.bz.app.R;
import com.bz.app.utils.Utils;
import com.bz.app.view.AnimImageView;
import com.bz.app.view.UnlockView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private TextureMapView mMapView = null;  //地图view
    private AMap aMap;  //地图对象
    private PolylineOptions mPolyOptions;  //在地图上画出轨迹
    private Marker mLocationMarker = null;
    private TextView mTimeTV;  //时间
    private TextView mDistanceTV; //距离
    private IRunning mRunning;  //AIDL接口
    private MarkerOptions markerOptions;
    private AnimImageView mAnimImg;
    private LinearLayout mSecLinear;
    private LinearLayout mSecLinear1;
    private LinearLayout mSecLinear2;
    private LinearLayout mRunLinear;
    private LinearLayout mRunLinear1;
    private LinearLayout mRunLinear2;
    private LinearLayout mPauseLinear;
    private LinearLayout mPauseLinear1;
    private LinearLayout mPauseLinear2;
    private LinearLayout mUnlockLinear;
    private TextView mSkipTx;
    private ProgressBar mProgressBar;
    private UnlockView mUnlockView;

    private static final String LOG_TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main, "开始跑步");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(onMenuItemClick);

        mMapView = (TextureMapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);


        init();
        initPolyline();

        mUnlockView = (UnlockView)findViewById(R.id.main_unlock_view);
        mUnlockView.setOnLockListener(new UnlockView.OnLockListener() {
            @Override
            public void unLock() {
                setLinearsGone();
                mRunLinear.setVisibility(View.VISIBLE);
            }

            @Override
            public void lock() {

            }
        });

    }


    private void init() {

        if (aMap == null) {
            aMap = mMapView.getMap();
            aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
            //地图初始缩放级别
            aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
            markerOptions = new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker));
            mLocationMarker = aMap.addMarker(markerOptions);
        }

        //显示跑步计时
        mTimeTV = (TextView) findViewById(R.id.activity_main_time_tv);
        //显示跑步距离
        mDistanceTV = (TextView) findViewById(R.id.activity_main_distance_tv);


        mProgressBar = (ProgressBar) findViewById(R.id.main_progress_bar);
        mProgressBar.setPadding(0, 0, 0, 0);
        mProgressBar.setMax(progressMax);

        mSecLinear = (LinearLayout) findViewById(R.id.main_sec_linear);
        mSecLinear1 = (LinearLayout) findViewById(R.id.main_sec_linear1);
        mSecLinear2 = (LinearLayout) findViewById(R.id.main_sec_linear2);
        mSkipTx = (TextView) findViewById(R.id.main_sec_tx1);

        mRunLinear = (LinearLayout) findViewById(R.id.main_run_linear);
        mRunLinear1 = (LinearLayout) findViewById(R.id.main_run_linear1);
        mRunLinear2 = (LinearLayout) findViewById(R.id.main_run_linear2);

        mPauseLinear = (LinearLayout) findViewById(R.id.main_pause_linear);
        mPauseLinear1 = (LinearLayout) findViewById(R.id.main_pause_linear1);
        mPauseLinear2 = (LinearLayout) findViewById(R.id.main_pause_linear2);

        mUnlockLinear = (LinearLayout) findViewById(R.id.main_unlock_linear);

        mAnimImg = (AnimImageView)findViewById(R.id.main_running_start);
        mAnimImg.setOnClickListener(this);
        mSecLinear1.setOnClickListener(this);
        mSecLinear2.setOnClickListener(this);
        mRunLinear1.setOnClickListener(this);
        mRunLinear2.setOnClickListener(this);
        mPauseLinear1.setOnClickListener(this);
        mPauseLinear2.setOnClickListener(this);

    }


    private int num = 100;  //十秒準備時間
    private int progressMax = 100;  //progressbar最大值
    private Handler mCountDownHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (num > 0) {
                if (num % 10 == 0) {
                    mSkipTx.setText(num / 10 + "s 后開始");
                }
                mProgressBar.setProgress(progressMax - num);
                num--;
                Log.v(LOG_TAG, "progress---->" + (100 - num));
                mCountDownHandler.sendEmptyMessageDelayed(0, 100);
            } else if (num == 0) {
                try {
                    mRunning.start();
                    initPolyline();
                    mSecLinear.setVisibility(View.GONE);
                    mRunLinear.setVisibility(View.VISIBLE);
                    num = 100;
                    mProgressBar.setProgress(0);
                    progressMax = 100;
                    mProgressBar.setMax(progressMax);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
    });

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.main_running_start:
                    setLinearsGone();
                    mSecLinear.setVisibility(View.VISIBLE);
                    mCountDownHandler.sendEmptyMessageDelayed(0, 100);
                    break;
                //马上开始
                case R.id.main_sec_linear1:
                    mRunning.start();
                    initPolyline();
                    setLinearsGone();
                    mRunLinear.setVisibility(View.VISIBLE);
                    num = 100;
                    mProgressBar.setProgress(0);
                    progressMax = 100;
                    mProgressBar.setMax(progressMax);
                    mCountDownHandler.removeMessages(0);
                    break;
                //加10s
                case R.id.main_sec_linear2:
                    num += 100;
                    progressMax += 100;
                    mProgressBar.setMax(progressMax);
                    break;
                //锁屏
                case R.id.main_run_linear1:
                    setLinearsGone();
                    mUnlockLinear.setVisibility(View.VISIBLE);
                    break;
                //暂停跑步
                case R.id.main_run_linear2:
                    mRunning.pause();
                    setLinearsGone();
                    mPauseLinear.setVisibility(View.VISIBLE);
                    break;
                //停止跑步
                case R.id.main_pause_linear1:
                    mRunning.stop();
                    setLinearsGone();
                    mAnimImg.setVisibility(View.VISIBLE);
                    break;
                //继续跑步
                case R.id.main_pause_linear2:
                    mRunning.resume();
                    setLinearsGone();
                    mRunLinear.setVisibility(View.VISIBLE);
                    Log.v(LOG_TAG, "resume--->被点击");
                    break;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private void setLinearsGone() {
        mAnimImg.setVisibility(View.GONE);
        mSecLinear.setVisibility(View.GONE);
        mRunLinear.setVisibility(View.GONE);
        mUnlockLinear.setVisibility(View.GONE);
        mPauseLinear.setVisibility(View.GONE);
    }


    //初始化地图轨迹线
    private void initPolyline() {
        mPolyOptions = new PolylineOptions();
        mPolyOptions.color(Color.GREEN);
        mPolyOptions.width(10f);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_location_mode, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            try {
                switch (item.getItemId()) {
                    case R.id.high_accuracy:
                        mRunning.chooseLocationMode(1);
                        break;
                    case R.id.battery_saving:
                        mRunning.chooseLocationMode(2);
                        break;
                    case R.id.device_sensors:
                        mRunning.chooseLocationMode(3);
                        break;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return false;
        }
    };

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mRunning = IRunning.Stub.asInterface(service);
            try {
                mRunning.registCallback(callback);
                if (mRunning.isRunning()) {
                    mRunning.closeNotification();
                    setLinearsGone();
                    mRunLinear.setVisibility(View.VISIBLE);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    private boolean latLngFlag = false;  //是否已发送经纬信息
    private IRunningCallback callback = new IRunningCallback.Stub() {
        @Override
        public void notifyData(float distance, String latLngListStr, String nowLatLngStr) throws RemoteException {
            mDistanceHandler.sendEmptyMessage((int) distance);
            Gson gson = new Gson();
            //当前定位
            LatLng nowLatLng = gson.fromJson(nowLatLngStr, LatLng.class);

            //添加Marker显示定位
            mLocationMarker.setPosition(nowLatLng);
            //每次定位移动到地图中心
            aMap.moveCamera(CameraUpdateFactory.changeLatLng(nowLatLng));

            //跑步轨迹
            Type type = new TypeToken<ArrayList<LatLng>>() {
            }.getType();
            //location集合
            ArrayList<LatLng> locationList = gson.fromJson(latLngListStr, type);
            mPolyOptions.add(locationList.get(locationList.size() - 1));
            aMap.addPolyline(mPolyOptions);


            if (!latLngFlag) {
                SharedPreferences.Editor editor = getSharedPreferences("latlng", MODE_PRIVATE).edit();
                editor.clear();
                editor.putFloat("lat", (float) nowLatLng.latitude);
                editor.putFloat("lng", (float) nowLatLng.longitude);
                editor.commit();
                latLngFlag = true;
            }
        }

        @Override
        public void timeUpdate(long time) throws RemoteException {
            mTimeHandler.sendEmptyMessage((int) (time / 1000));
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
        mMapView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            //正在跑步，开启通知
            if (mRunning.isRunning()) {
                mRunning.openNotification();
            }
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

}
