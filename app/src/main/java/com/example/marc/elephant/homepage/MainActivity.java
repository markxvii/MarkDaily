package com.example.marc.elephant.homepage;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.marc.elephant.R;
import com.example.marc.elephant.fragment.PreferenceActivity;
import com.example.marc.elephant.util.NetworkState;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private Toolbar titleToolBar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private NavigationView nav;
    private ActionBarDrawerToggle toggle;
    private LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkNetworkState();
        initView();
        initNavigation();
        initViewPager();
    }

    private void initNavigation() {
        toggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, titleToolBar, R.string.app_name, R.string.app_name);
        titleToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.END);
            }
        });
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_night:
                        int mode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                        if(mode == Configuration.UI_MODE_NIGHT_YES) {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        } else if(mode == Configuration.UI_MODE_NIGHT_NO) {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        }
                        recreate();
                        break;
                    case R.id.nav_setting:
                        Intent i=new Intent(MainActivity.this, PreferenceActivity.class);
                        i.putExtra("nav","setting");
                        startActivity(i);
                        break;
                    case R.id.nav_about:
                        Intent in=new Intent(MainActivity.this, PreferenceActivity.class);
                        in.putExtra("nav","about");
                        startActivity(in);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        drawerLayout.addDrawerListener(toggle);
    }

    private void checkNetworkState() {
        if (!NetworkState.networkConnected(this)){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("未连接网络").setMessage("大象暂未提供离线缓存功能，请打开网络后再使用！").setIcon(R.drawable.elephant).setCancelable(false)
                    .setPositiveButton("朕知道了", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            builder.show();
        }
        if (NetworkState.mobileDataConnected(this)){
            Toast.makeText(this, "您正在使用移动网络，请注意控制流量！", Toast.LENGTH_LONG).show();
        }
    }

    private void initViewPager() {
        ResumeAdaper adapter = new ResumeAdaper(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(4);
    }

    private void initView() {
        mainLayout = (LinearLayout) findViewById(R.id.main_layout);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        titleToolBar = (Toolbar) findViewById(R.id.title_tool_bar);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        nav = (NavigationView) findViewById(R.id.nav);
    }
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }
}
