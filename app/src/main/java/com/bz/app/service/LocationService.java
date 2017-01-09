package com.bz.app.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.bz.app.IRunning;
import com.bz.app.IRunningCallback;
import com.bz.app.activity.MainActivity;
import com.bz.app.R;
import com.bz.app.database.DBAdapter;
import com.bz.app.entity.RunningRecord;
import com.bz.app.utils.GlobalContext;
import com.bz.app.utils.Utils;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class LocationService extends Service implements AMapLocationListener {

    private List<LatLng> list = new ArrayList<>();  //跑步轨迹集合
    public AMapLocationClient mLocationClient = null;
    public Context mContext = GlobalContext.getInstance();
    public AMapLocationClientOption mOption;
    private float distance; //跑步距离
    private LatLng startLatLng = null;
    private long mStartTime = -1;  //开始跑步时间
    private IRunningCallback mCallback;
    private boolean mIsRunning = false;  //跑步标志位
    private boolean mIsPause = false;
    private RunningRecord mRecord;  //一条跑步记录
    private DBAdapter mDBAdapter;  //数据库操作
    private Notification notification; //通知
    private NotificationManager notificationManager; //管理通知
    private NotificationCompat.Builder builder;  //创建通知
    private boolean mIsShowNotification = false;  //是否显示通知
    private DecimalFormat df = new DecimalFormat("0.0");

    private static final String LOG_TAG = "LocationService";


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
        mLocationClient.setLocationOption(getOption());
        mLocationClient.startLocation();
    }

    private AMapLocationClientOption getOption() {
        mOption = new AMapLocationClientOption();
        mOption.setInterval(2000);
        mOption.setLocationMode(!mIsRunning ? AMapLocationClientOption.AMapLocationMode.Hight_Accuracy :
                AMapLocationClientOption.AMapLocationMode.Device_Sensors);
        return mOption;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationClient.stopLocation();
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
        if (!mIsRunning) stopSelf();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                Gson gson = new Gson();
                LatLng mLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                try {
                    //如果是跑步，则把location加入集合，计算距离
                    if (mIsRunning) {
                        list.add(mLatLng);
                        if (startLatLng != null) {
                            float dis = AMapUtils.calculateLineDistance(startLatLng, mLatLng);
                            distance += dis;
                        }
                        startLatLng = mLatLng;

                        if (mIsShowNotification) {
                            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            builder.setContentText(getNotificationContent());
                            notification = builder.build();
                            notificationManager.notify(1, notification);
                        }
                    }
                    //跑步时的轨迹集合
                    String latLngListStr = gson.toJson(list);
                    //未跑步时的定位
                    String nowLatLngStr = gson.toJson(mLatLng);
                    //城市
                    if (mCallback != null) mCallback.notifyData(distance, latLngListStr, nowLatLngStr);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getNotificationContent() {
        String durationStr = Utils.getTimeStr((int) (mDisplayTime / 1000));
        return "时间： " + durationStr + "   距离： " + distance + "m";
    }

    //保存到数据库
    protected void saveRecord(String time) {
        if (list.size() > 0) {
            mDBAdapter = new DBAdapter(this);
            mDBAdapter.open();
            LatLng firstLocation = list.get(0);
            LatLng lastLocation = list.get(list.size() - 1);
            String startPoint = latLngToString(firstLocation);
            String endPoint = latLngToString(lastLocation);
            String pathLinePoints = getPathLineString(list);
            //公里
            String distanceStr = df.format(distance / 1000.0);
            String durationStr = Utils.getTimeStr((int) (mDisplayTime / 1000));
            //速度:km/min
            String average_speed = df.format((distance / 1000.0) / (mDisplayTime / 60000));
            mDBAdapter.insertRecord(startPoint, endPoint, pathLinePoints, distanceStr,
                    durationStr, average_speed, time);
            mDBAdapter.close();
        } else {
            Log.e(LOG_TAG, "没有记录到数据库");
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

    private long mDisplayTime;
    private Handler mTimeHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (mIsRunning){
                if (mCallback != null) try {
                    mDisplayTime = System.currentTimeMillis() - mStartTime - mDelayTime;
                    mCallback.timeUpdate(mDisplayTime);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                mTimeHandler.sendEmptyMessageDelayed(0,1000);
            }
            return false;
        }
    });

    /**
     * 开始跑步
     */
    private void startRunning() {
        mStartTime = System.currentTimeMillis(); //开始跑步时间
        list.clear();
        distance = 0f;
        mDelayTime = 0;
        mIsRunning = true; //跑步标志位置为true
        if (mRecord != null) mRecord = null; //开始跑步  new一个新的跑步记录
        mRecord = new RunningRecord();
        mRecord.setDate(String.valueOf(mStartTime));
        mTimeHandler.sendEmptyMessage(0);
    }

    /**
     * 结束跑步
     */
    private void stopRunning() {
        mIsRunning = false;
        mIsPause = false;
        saveRecord(mRecord.getDate());
    }

    private long mPauseTime;  //暫停跑步的時間戳
    /**
     * 暂停跑步
     */
    private void onPauseRunning() {
        mIsRunning = false;
        mIsPause = true;
        mPauseTime = System.currentTimeMillis();
    }

    private long mResumeTime;  //繼續跑步的時間戳

    private long mDelayTime;  //延遲時間
    /**
     * 继续跑步
     */
    private void onResumeRunning() {
        mIsRunning = true;
        mTimeHandler.sendEmptyMessage(0);
        mResumeTime = System.currentTimeMillis();
        mDelayTime += mResumeTime - mPauseTime;
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
        public void pause() throws RemoteException {
            onPauseRunning();
        }

        @Override
        public void resume() throws RemoteException {
            onResumeRunning();
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

        @Override
        public boolean isPause() throws RemoteException {
            return mIsPause;
        }

        @Override
        public void openNotification() throws RemoteException {

            builder = new NotificationCompat.Builder(mContext);
            Intent notificationIntent = new Intent(mContext, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);
            builder.setSmallIcon(R.drawable.icon);
            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
            builder.setLargeIcon(largeIcon);
            builder.setContentTitle("正在跑步...");
            builder.setContentText(getNotificationContent());
            builder.setContentIntent(pendingIntent);
            notification = builder.build();
            startForeground(1, notification);
            mIsShowNotification = true;

//            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
//            builder.setSmallIcon(R.drawable.location_marker);
//            builder.setContentTitle("正在跑步...");
//            builder.setContentText("时间：  距离：  ");
//
//            //打开MainActivity
//            Intent notificationIntent = new Intent(mContext, MainActivity.class);
//            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0,
//                    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//            //打开HistoryActivity
//            Intent hisIntent = new Intent(mContext, HistoryActivity.class);
//            PendingIntent openHistory = PendingIntent.getActivity(mContext, 0,
//                    hisIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//            //自定义的Notification视图
//            RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.service_notification);
//            remoteViews.setTextViewText(R.id.service_notification_msg, "正在跑步");
//            remoteViews.setImageViewResource(R.id.service_notification_icon, R.drawable.running);
//            remoteViews.setOnClickPendingIntent(R.id.open_history_activity, openHistory);
//
//            Log.e(LOG_TAG, "remoteViews---->" + remoteViews.toString());
//
//            builder.setContent(remoteViews);
//            builder.setContentIntent(pendingIntent);
//            Notification notification = builder.build();
//            startForeground(1, notification);
        }

        @Override
        public void closeNotification() throws RemoteException {
            stopForeground(true);
            mIsShowNotification = false;
        }
    };

}
