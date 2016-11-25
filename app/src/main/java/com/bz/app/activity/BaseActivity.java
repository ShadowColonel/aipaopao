package com.bz.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bz.app.R;

public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected DrawerLayout drawer;
    protected Toolbar toolbar;

    private TextView cityTx;
    private TextView pmTx;
    private TextView airQualityTx;
    private TextView weatherTx;
    private TextView temperatureTx;
    private ImageView weaImg;
    private TextView indexTx;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public void setContentView(@LayoutRes int layoutResID, String title) {
        super.setContentView(R.layout.activity_base);
        FrameLayout container = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(layoutResID, container, true);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initHeader(navigationView);

    }

    private void initHeader(NavigationView navigationView) {
        View headView = navigationView.getHeaderView(0);
        cityTx = (TextView) headView.findViewById(R.id.header_weather_city_tx);
        pmTx = (TextView) headView.findViewById(R.id.header_weather_pm2_5_tx);
        airQualityTx = (TextView) headView.findViewById(R.id.header_weather_air_tx);
        weatherTx = (TextView) headView.findViewById(R.id.header_weather_wea_tx);
        temperatureTx = (TextView) headView.findViewById(R.id.header_weather_temperature_tx);
        indexTx = (TextView) headView.findViewById(R.id.header_weather_index_tx);
        weaImg = (ImageView) headView.findViewById(R.id.header_weather_img);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_start) {
            Intent dataIntent = new Intent(this, MainActivity.class);
            startActivity(dataIntent);

        } else if (id == R.id.nav_cloud) {
            Intent weatherIntent = new Intent(this, WeatherActivity.class);
            startActivity(weatherIntent);

        } else if (id == R.id.nav_data) {
            Intent dataIntent = new Intent(this, StatisticsActivity.class);
            startActivity(dataIntent);

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
}
