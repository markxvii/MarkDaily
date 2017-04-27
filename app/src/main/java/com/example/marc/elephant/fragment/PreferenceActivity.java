package com.example.marc.elephant.fragment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.marc.elephant.R;

public class PreferenceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        if (getIntent().getStringExtra("nav").equals("setting")){
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new SettingFrag())
                    .commit();
            getSupportActionBar().setTitle("设置");
        }else if(getIntent().getStringExtra("nav").equals("about")){
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new AboutFrag())
                    .commit();
            getSupportActionBar().setTitle("关于");
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
