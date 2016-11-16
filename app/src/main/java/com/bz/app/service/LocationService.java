package com.bz.app.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.NotificationCompat;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.bz.app.R;
import com.bz.app.activity.MainActivity;
import com.bz.app.utils.GlobalContext;
import com.bz.app.IRunning;
import com.bz.app.IRunningCallback;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class LocationService extends Service implements AMapLocationListener {

    private List<LatLng> latLngs = new ArrayList<>();  //跑步轨迹集合
    public AMapLocationClient mLocationClient = null;
    public Context mContext = GlobalContext.getInstance();
    public AMapLocationClientOption mOption;
    private float distance; //跑步距离
    private LatLng startLatLng = null;
    private long mStartTime = -1;
    private IRunningCallback mCallback;
    private boolean mIsRunning = false;  //跑步标志位

    private static final String LOG_TAG = "LocationService";


    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        builder.setSmallIcon(R.drawable.location_marker);
        builder.setContentTitle("正在跑步...");
        builder.setContentText("时间：  距离：  ");
        builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();
        startForeground(1, notification);
    }

    private void init() {
        mLocationClient = new AMapLocationClient(mContext);
        mLocationClient.setLocationListener(this);

        //定位参数
        mOption = new AMapLocationClientOption();
        mOption.setInterval(2000);
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
                    if (mIsRunning) {
                        latLngs.add(mLatLng);
                        if (startLatLng != null) {
                            float dis = AMapUtils.calculateLineDistance(startLatLng, mLatLng);
                            distance += dis;
                        }
                        startLatLng = mLatLng;
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
        mIsRunning = true;
        mTimeHandler.sendEmptyMessage(0);
    }


    private Handler mTimeHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (mIsRunning){
                long time = System.currentTimeMillis() - mStartTime;
                if (mCallback != null) try {
                    mCallback.timeUpdate(time);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                mTimeHandler.sendEmptyMessageDelayed(0,1000);
            }
            return false;
        }
    });

    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
        if (!mIsRunning) stopSelf();
    }

    /**
     * 结束跑步
     */
    private void stopRunning() {
        //跑步标志位置为false
        mIsRunning = false;
        //将前台进程取消
        stopForeground(true);
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
            mCallback = null;
        }

        @Override
        public boolean isRunning() throws RemoteException {
            return mIsRunning;
        }
    };
}
