package com.bz.app.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.bz.app.IRunning;
import com.bz.app.IRunningCallback;
import com.bz.app.R;
import com.bz.app.service.LocationService;
import com.bz.app.utils.Utils;
import com.bz.app.view.AnimImageView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by ThinkPad User on 2016/12/28.
 */

public class AMapFragment extends Fragment {

    private Unbinder mUnbinder;
    private AMap map; //地图
    private MarkerOptions markerOptions;
    private Marker mLocationMarker = null;//标记点
    private PolylineOptions mPolyOptions; //轨迹
    private IRunning mRunning;//AIDL接口
    private int distance;

    @BindView(R.id.map_view)
    MapView mMapView;
    @BindView(R.id.fragment_map_distance_tv)
    TextView mDistanceTv;
    @BindView(R.id.fragment_map_speed_tv)
    TextView mSpeedTv;
    @BindView(R.id.fragment_map_time_tv)
    TextView mTimeTv;
    @BindView(R.id.fragment_map_pause_btn)
    Button mPauseBtn;
    @BindView(R.id.fragment_map_resume_btn)
    Button mResumeBtn;
    @BindView(R.id.fragment_map_stop_btn)
    Button mStopBtn;
    @BindView(R.id.running_start)
    AnimImageView mStartView;
    @BindView(R.id.fragment_map_lock_btn)
    ToggleButton mLockBtn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView.onCreate(savedInstanceState);
        initMap();

        mLockBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mLockBtn.setChecked(isChecked);
                mLockBtn.setBackgroundResource(isChecked ? R.drawable.lock_bg : R.drawable.unlock_bg);
                if (isChecked) {
                    mStopBtn.setBackgroundColor(Color.parseColor("#FEAE96"));
                    mPauseBtn.setBackgroundColor(Color.parseColor("#FFF7A9"));
                } else {
                    mStopBtn.setBackgroundColor(Color.parseColor("#FD460D"));
                    mPauseBtn.setBackgroundColor(Color.parseColor("#FFEC39"));
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(getActivity(), LocationService.class);
        getActivity().startService(intent);
        getActivity().bindService(intent, connection, Service.BIND_AUTO_CREATE);
    }

    public void isRunning() {
        mStartView.setVisibility(View.GONE);
        mStopBtn.setVisibility(View.VISIBLE);
        mLockBtn.setVisibility(View.VISIBLE);
        mLockBtn.setChecked(true);
        mPauseBtn.setVisibility(View.VISIBLE);
    }

    public void isPause() {
        mStopBtn.setVisibility(View.GONE);
        mPauseBtn.setVisibility(View.GONE);
        mLockBtn.setVisibility(View.GONE);
        mStartView.setVisibility(View.GONE);
        mResumeBtn.setVisibility(View.VISIBLE);
        mLockBtn.setChecked(true);
    }
    @OnClick(R.id.running_start) void startRunning() {
        mStartView.setVisibility(View.GONE);
        mStopBtn.setVisibility(View.VISIBLE);
        mLockBtn.setVisibility(View.VISIBLE);
        mLockBtn.setChecked(true);
        mPauseBtn.setVisibility(View.VISIBLE);

        try {
            mRunning.start();
            mRunning.openNotification();
            initPolyline();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.fragment_map_stop_btn) void stopRunning() {
        if (mLockBtn.isChecked()) {
            anim();
        } else {
            mStartView.setVisibility(View.VISIBLE);
            mStopBtn.setVisibility(View.GONE);
            mLockBtn.setVisibility(View.GONE);
            mPauseBtn.setVisibility(View.GONE);
            try {
                mRunning.stop();
                mRunning.closeNotification();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void anim() {
        ValueAnimator anim = ObjectAnimator.ofFloat(mLockBtn, "translationY", -mLockBtn.getHeight());
        anim.setDuration(200);
        anim.setRepeatCount(1);
        anim.setRepeatMode(ValueAnimator.REVERSE);
        anim.start();
    }

    @OnClick(R.id.fragment_map_pause_btn) void pause() {
        if (mLockBtn.isChecked()) {
            anim();
        } else {
            mStopBtn.setVisibility(View.GONE);
            mPauseBtn.setVisibility(View.GONE);
            mLockBtn.setVisibility(View.GONE);
            mResumeBtn.setVisibility(View.VISIBLE);
            mLockBtn.setChecked(true);
            try {
                mRunning.pause();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @OnClick(R.id.fragment_map_resume_btn) void resume() {
        mStopBtn.setVisibility(View.VISIBLE);
        mPauseBtn.setVisibility(View.VISIBLE);
        mLockBtn.setVisibility(View.VISIBLE);
        mResumeBtn.setVisibility(View.GONE);
        try {
            mRunning.resume();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    private void initMap() {
        if (map == null) {
            map = mMapView.getMap();
            map.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
            map.moveCamera(CameraUpdateFactory.zoomTo(16));
            markerOptions = new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker));
            mLocationMarker = map.addMarker(markerOptions);
        }
    }

    //初始化地图轨迹线
    private void initPolyline() {
        mPolyOptions = new PolylineOptions();
        mPolyOptions.color(Color.GREEN);
        mPolyOptions.width(10f);
    }
    

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mRunning = IRunning.Stub.asInterface(service);
            try {
                mRunning.registCallback(callback);
                if (mRunning.isRunning()) {
                    isRunning();
                } else if (mRunning.isPause()) {
                    isPause();
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
        public void notifyData(float distance, String latLngListStr, String nowLatLngStr) throws RemoteException {
            mDistanceHandler.sendEmptyMessage((int) distance);
            Gson gson = new Gson();
            //当前定位
            LatLng nowLatLng = gson.fromJson(nowLatLngStr, LatLng.class);

            //添加Marker显示定位
            mLocationMarker.setPosition(nowLatLng);
            //每次定位移动到地图中心
            map.moveCamera(CameraUpdateFactory.changeLatLng(nowLatLng));

            //跑步轨迹
            Type type = new TypeToken<ArrayList<LatLng>>() {}.getType();
            //location集合
            ArrayList<LatLng> locationList = gson.fromJson(latLngListStr, type);
            mPolyOptions.add(locationList.get(locationList.size() - 1));
            map.addPolyline(mPolyOptions);

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
            mDistanceTv.setText(msg.what + "m");
            distance = msg.what;
            return false;
        }
    });

    //更新时间
    private Handler mTimeHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            mTimeTv.setText(Utils.getTimeStr(msg.what));
            mSpeedTv.setText(distance / (float) (msg.what) + "m/s");
            return false;
        }
    });

    @Override
    public void onStop() {
        super.onStop();
        try {
            mRunning.unregistCallback(callback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        getActivity().unbindService(connection);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMapView.onDestroy();
        mUnbinder.unbind();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }
}
