package com.bz.app;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LocationService extends Service implements AMapLocationListener {

    private List<LatLng> latLngs = new ArrayList<>();  //跑步轨迹集合
    private LatLng locationLatlng;  //定位de经纬度

    public AMapLocationClient mLocationClient = null;
    public Context mContext = GlobalContext.getInstance();
    public AMapLocationClientOption mOption;

    private long runningTime; //跑步时间
    private float distance; //跑步距离
    private boolean isFirstLocation = true;
    private LatLng startLatLng;
    private LatLng endLatLng;

    private static final String LOG_TAG = "LocationService";
    private long mStartTime = -1;

    private int locatioType = 1;  //定位方式，1:跑步，2:定位

    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        init();
    }

    private void init() {
        mLocationClient = new AMapLocationClient(mContext);
        mLocationClient.setLocationListener(this);

        mOption = new AMapLocationClientOption();
        mOption.setInterval(3000);
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationClient.setLocationOption(mOption);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                switch (locatioType) {
                    case 1:
                        LatLng mLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                        latLngs.add(mLatLng);

                        if (isFirstLocation) {
                            startLatLng = mLatLng;
                            endLatLng = mLatLng;
                            isFirstLocation = false;
                        } else {
                            startLatLng = endLatLng;
                            endLatLng = mLatLng;
                            float dis = AMapUtils.calculateLineDistance(startLatLng, endLatLng);
                            distance += dis;
                        }

                        Gson gson1 = new Gson();
                        String latLngsStr = gson1.toJson(latLngs);
                        try {
                            if (mCallback !=null) mCallback.notifyData(distance, latLngsStr);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        break;

                    case 2:
                        locationLatlng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                        Gson gson2 = new Gson();
                        String nowLatLngStr = gson2.toJson(latLngs);

                        try {
                            if (mCallback != null)
                                mCallback.notifyNowLatLng(nowLatLngStr);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        break;
                }

            }
        }
    }

    /**
     * 开始跑步
     */
    private void startRunning(){
        //开始跑步时间
        mStartTime = System.currentTimeMillis();
        mLocationClient.startLocation();
    }

    /**
     * 结束跑步
     */
    private void stopRunning(){
        //跑步总时间
        runningTime = System.currentTimeMillis() - mStartTime;
        mLocationClient.stopLocation();
        try {
            if (mCallback !=null) mCallback.notifyTime(runningTime);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 定位
     */
    private void getLocation() {
        locatioType = 2;
        mLocationClient.startLocation();
    }

    private IRunning.Stub stub = new IRunning.Stub() {
        @Override
        public void start() throws RemoteException {
            startRunning();
        }

        @Override
        public void stop() throws RemoteException {
            stopRunning();
        }

        @Override
        public void location() throws RemoteException {
            getLocation();
        }

        @Override
        public void registCallback(IRunningCallback callback) throws RemoteException {
            mCallback = callback;
        }

        @Override
        public void unregistCallback(IRunningCallback callback) throws RemoteException {

        }
    };
    private IRunningCallback mCallback;

}
