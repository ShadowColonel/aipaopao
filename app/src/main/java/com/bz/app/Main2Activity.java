package com.bz.app;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.bz.app.fragment.AMapFragment;
import com.bz.app.fragment.AboutFragment;
import com.bz.app.fragment.HelpFragment;
import com.bz.app.fragment.HistoryFragment;
import com.bz.app.fragment.SettingFragment;
import com.bz.app.fragment.StatisticsFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("开始跑步");
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        addMapFragment();
    }

    private long tempTime;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (System.currentTimeMillis() - tempTime > 2000) {
                Toast.makeText(Main2Activity.this, "再点一次，退出", Toast.LENGTH_SHORT).show();
                tempTime = System.currentTimeMillis();
            } else {
                super.onBackPressed();
            }
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.action_record, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_share) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_start) {
            toolbar.setTitle("开始跑步");
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Log.d(TAG, "startrunning1--->" + mFragmentList.size());
            if (mFragmentList.size() == 2) {
                ft.remove(mFragmentList.get(1));
                mFragmentList.remove(1);
            }
            Log.d(TAG, "startrunning2--->" + mFragmentList.size());
            ft.show(mapFragment);
            ft.commitAllowingStateLoss();
        } else if (id == R.id.nav_data) {
            toolbar.setTitle("数据统计");
            if (mStatisticsFragment == null) {
                mStatisticsFragment = new StatisticsFragment();
            }
            replaceFragment(mStatisticsFragment);
        } else if (id == R.id.nav_history) {
            toolbar.setTitle("历史记录");
            if (mHistoryFragment == null) {
                mHistoryFragment = new HistoryFragment();
            }
            replaceFragment(mHistoryFragment);
        } else if (id == R.id.nav_setting) {
            toolbar.setTitle("设置");
            if (mSettingFragment == null) {
                mSettingFragment = new SettingFragment();
            }
            replaceFragment(mSettingFragment);
        } else if (id == R.id.nav_help) {
            toolbar.setTitle("帮助");
            if (mHelpFragment == null) {
                mHelpFragment = new HelpFragment();
            }
            replaceFragment(mHelpFragment);
        } else if (id == R.id.nav_about) {
            toolbar.setTitle("关于");
            if (mAboutFragment == null) {
                mAboutFragment = new AboutFragment();
            }
            replaceFragment(mAboutFragment);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private AMapFragment mapFragment;
    private AboutFragment mAboutFragment;
    private HelpFragment mHelpFragment;
    private HistoryFragment mHistoryFragment;
    private SettingFragment mSettingFragment;
    private StatisticsFragment mStatisticsFragment;
    private List<Fragment> mFragmentList = new ArrayList<>();

    private void addMapFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (mapFragment == null) {
            mapFragment = new AMapFragment();
        }
        ft.add(R.id.activity_main_container, mapFragment);
        mFragmentList.add(mapFragment);
        ft.commitAllowingStateLoss();
        Log.d(TAG, "addMapFragment--->" + mFragmentList.size());

    }
    public static final String TAG = "Main2Activity";
    private void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.hide(mapFragment);
        if (mFragmentList.size() > 1) {
            if (fragment != mFragmentList.get(1)) {
                ft.add(R.id.activity_main_container, fragment);
                mFragmentList.add(fragment);
            }
        } else {
            ft.add(R.id.activity_main_container, fragment);
            mFragmentList.add(fragment);
        }

        Log.d(TAG, "replacefragment1--->" + mFragmentList.size());
        if (mFragmentList.size() == 3) {
            ft.remove(mFragmentList.get(1));
            mFragmentList.remove(1);
        }
        ft.commitAllowingStateLoss();
        Log.d(TAG, "replacefragment2--->" + mFragmentList.size());

    }
}
