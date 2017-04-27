package com.example.marc.elephant.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.example.marc.elephant.R;
import com.example.marc.elephant.gson.Welcome;
import com.example.marc.elephant.homepage.MainActivity;
import com.example.marc.elephant.util.HttpUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WelcomeActivity extends AppCompatActivity {
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                finish();
            }
        }
    };
    private ImageView welcomeImage;
    private ImageView elephant;
    private TextView eleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_welcome);
        initView();
        initImage();
        initAnimate();
    }

    private void initAnimate() {
        AnimationSet as=new AnimationSet(true);
        as.setDuration(1000);
        RotateAnimation ra=new RotateAnimation(0,360,RotateAnimation.RELATIVE_TO_SELF,0.5F,RotateAnimation.RELATIVE_TO_SELF,0.5F);
        ra.setDuration(1000);
        as.addAnimation(ra);
        ScaleAnimation sa=new ScaleAnimation(0,1,0,1, Animation.RELATIVE_TO_SELF,0.5F,Animation.RELATIVE_TO_SELF,0.5F);
        sa.setDuration(1000);
        as.addAnimation(sa);
        elephant.startAnimation(as);
        eleText.startAnimation(as);
    }

    private void initImage() {
        handler.sendEmptyMessageDelayed(0, 3000);
        HttpUtil.SendOkHttpReuqest("http://news-at.zhihu.com/api/7/prefetch-launch-images/1080*1920", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                return;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText=response.body().string();
                Gson gson=new Gson();
                final Welcome w=gson.fromJson(responseText,Welcome.class);
                if (w!=null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(WelcomeActivity.this).load(w.getCreatives().get(0).getUrl()).asBitmap().centerCrop().into(welcomeImage);
                        }
                    });
                }
            }
        });
    }

    private void initView() {
        eleText = (TextView) findViewById(R.id.ele_text);
        elephant = (ImageView) findViewById(R.id.elephant);
        welcomeImage = (ImageView) findViewById(R.id.welcome_image);
    }
}
