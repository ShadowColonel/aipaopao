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
import java.util.List;

public class LocationService extends Service implements AMapLocationListener {

    private List<LatLng> latLngs = new ArrayList<>();  //跑步轨迹集合

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
    private IRunningCallback mCallback;

    private boolean runningFlag = false;  //跑步标志位


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
        mLocationClient.startLocation();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationClient.stopLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {

                Gson gson = new Gson();
                LatLng mLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());

                try {
                    //如果是跑步，则把经纬度加入轨迹集合，计算距离
                    if (runningFlag) {
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
                    }
                    //跑步时的轨迹集合
                    String latLngListStr = gson.toJson(latLngs);

                    //没有跑步，把当前定位回传给客户端
                    String nowLatLngStr = gson.toJson(mLatLng);

                    if (mCallback != null) mCallback.notifyData(distance, latLngListStr, nowLatLngStr);

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 开始跑步
     */
    private void startRunning() {
        //开始跑步时间
        mStartTime = System.currentTimeMillis();
        //跑步标志位置为true
        runningFlag = true;
    }

    /**
     * 结束跑步
     */
    private void stopRunning() {
        //跑步总时间
        runningTime = System.currentTimeMillis() - mStartTime;
        //跑步标志位置为false
        runningFlag = false;

        try {
            if (mCallback != null) mCallback.notifyTime(runningTime);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
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
        public void registCallback(IRunningCallback callback) throws RemoteException {
            mCallback = callback;
        }

        @Override
        public void unregistCallback(IRunningCallback callback) throws RemoteException {

        }
    };
}
