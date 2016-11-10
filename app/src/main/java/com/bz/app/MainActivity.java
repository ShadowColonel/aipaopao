package com.bz.app;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.trace.LBSTraceClient;
import com.amap.api.trace.TraceListener;
import com.amap.api.trace.TraceLocation;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AMapLocationListener, View.OnClickListener, LocationSource {

    private TextureMapView mMapView = null;
    private AMap aMap;
    private UiSettings mUiSettings;
    private Marker mLocationMarker = null;

    public AMapLocationClient mLocationClient = null;
    public AMapLocationClientOption mOption = null;
    private OnLocationChangedListener mListener;

    private List<LatLng> latLngs = new ArrayList<>(); //经纬度集合
    private Polyline polyline;

    private static final String LOG_TAG = "MainActivity";
    private TextView mLogView;
    private ImageButton mStartRecord;
    private ImageButton mStopRecord;

    private LocationService.LocationBinder mLocationBinder;
    private float distance;


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


        mLogView = (TextView) findViewById(R.id.activity_main_tv_log);
        mStartRecord = (ImageButton) findViewById(R.id.start_record);
        mStartRecord.setOnClickListener(this);
        mStopRecord = (ImageButton) findViewById(R.id.stop_record);
        mStopRecord.setOnClickListener(this);

        mMapView = (TextureMapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        aMap = mMapView.getMap();
        mUiSettings = aMap.getUiSettings();
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setScaleControlsEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        aMap.setLocationSource(this);
        aMap.setMyLocationEnabled(true);

        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationClient.setLocationListener(this);
        mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mOption.setNeedAddress(true);
        mOption.setOnceLocation(true);
        mLocationClient.setLocationOption(mOption);
        mLocationClient.startLocation();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

        } else if (id == R.id.nav_setting) {

        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_about) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();

        if (mLocationClient != null) {
            mLocationClient.stopLocation();
        }
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
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null) mListener.onLocationChanged(aMapLocation);

        LatLng mLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
        latLngs.add(mLatLng);




        //轨迹
        aMap.addPolyline(new PolylineOptions().addAll(latLngs).width(10).color(Color.GREEN));

        //每次定位移动到地图中心
        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 18));
//        if (aMapLocation != null) {
//            if (aMapLocation.getErrorCode() == 0) {
//                LatLng mLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
//
//                if (mLocationMarker == null) {
//                    mLocationMarker = aMap.addMarker(new MarkerOptions()
//                    .position(mLatLng)
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker)));
//                } else {
//                    mLocationMarker.setPosition(mLatLng);
//                }
//
//                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 18));
//            }
//        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        this.mListener = onLocationChangedListener;

    }

    @Override
    public void deactivate() {
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_record:

                mOption.setOnceLocation(false);
                mOption.setInterval(5000);
                mLocationClient.setLocationOption(mOption);
                mLocationClient.startLocation();
                break;
            case R.id.stop_record:
                mLocationClient.stopLocation();
                break;
        }

    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mLocationBinder = (LocationService.LocationBinder) service;
            latLngs = mLocationBinder.getLocation();
            distance = mLocationBinder.getDistance();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, LocationService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
    }

        @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
