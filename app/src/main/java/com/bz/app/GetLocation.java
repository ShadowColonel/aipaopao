package com.bz.app;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

/**
 * Created by ThinkPad User on 2016/11/9.
 */

public class GetLocation implements AMapLocationListener {

    public AMapLocationClient mLocationClient = null;
    public Context mContext = GlobalContext.getInstance();

    public AMapLocation getLocation(AMapLocationClientOption option) {

        mLocationClient = new AMapLocationClient(mContext);
        mLocationClient.setLocationListener(this);
        mLocationClient.setLocationOption(option);
        mLocationClient.startLocation();
        return null;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {

    }
}
