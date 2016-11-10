package com.bz.app;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class LocationService extends Service implements AMapLocationListener {

    private List<LatLng> latLngs = new ArrayList<>();

    public AMapLocationClient mLocationClient = null;
    public Context mContext = GlobalContext.getInstance();
    public AMapLocationClientOption mOption;

    private float distance;
    private LatLng startLatLng;
    private LatLng endLatLng;
    private boolean isFirstLocation = true;

//    private LocationBinder mBinder = new LocationBinder();
    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mLocationClient = new AMapLocationClient(mContext);
        mLocationClient.setLocationListener(this);
        initOption();
        mLocationClient.setLocationOption(mOption);
        mLocationClient.startLocation();
    }

    private void initOption() {
        mOption = new AMapLocationClientOption();
        mOption.setInterval(5000);
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
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
            }
        }
    }

    class LocationBinder extends Binder{
        public List<LatLng> getLocation() {
            return latLngs;
        }

        public float getDistance() {
            return distance;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationClient.stopLocation();
    }

    private IRunning.Stub stub = new IRunning.Stub() {
        @Override
        public void start(int type) throws RemoteException {
            Toast.makeText(LocationService.this,"t"+type,Toast.LENGTH_SHORT).show();
        }

        @Override
        public void stop() throws RemoteException {

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
