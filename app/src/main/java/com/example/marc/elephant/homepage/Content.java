package com.example.marc.elephant.homepage;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.marc.elephant.R;
import com.example.marc.elephant.api.Api;
import com.example.marc.elephant.util.GsonUtil;
import com.example.marc.elephant.util.HttpUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Content extends AppCompatActivity {

    private CollapsingToolbarLayout toolbarLayout;
    private ImageView zhihuContentImage;
    public static WebView webView;
    private com.example.marc.elephant.gson.Content content;
    private SharedPreferences preferences;
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
        setContentView(R.layout.content);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initView();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean pref = preferences.getBoolean("is_load_img", false);
        if (pref) {
            webView.getSettings().setBlockNetworkImage(true);
        } else {
            webView.getSettings().setBlockNetworkImage(false);
        }
        if (getIntent().getIntExtra("id", 0) != 0) {
            resolveZhihuData();
        } else if (getIntent().getStringExtra("id") != null) {
            resoveGuokrData();
        } else if (getIntent().getStringExtra("yigeId") != null) {
            resolveYigeData();
        }else if (getIntent().getIntExtra("id2",0)!=0){
            resloveZhihu2Data();
        }
    }
    private void resloveZhihu2Data(){
        int id = getIntent().getIntExtra("id2", 0);
        if (id != 0) {
            HttpUtil.SendOkHttpReuqest(Api.ZHIHU_NEWS + id, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Content.this, "获取内容失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response != null) {
                        String responseText = response.body().string();
                        content = GsonUtil.ZhihuContentUtil(responseText);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                toolbarLayout.setTitle(content.getTitle());
                                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                                //显示具体文章内容
                                String result = handleZhihuContent(content.getBody());
                                webView.loadDataWithBaseURL("x-data://base", result, "text/html", "utf-8", null);
                            }
                        });
                    }
                }
            });
        }
    }

    private void resolveYigeData() {
        Glide.with(Content.this).load(getIntent().getStringExtra("yigeImg")).asBitmap().centerCrop().into(zhihuContentImage);
        toolbarLayout.setTitle(getIntent().getStringExtra("yigeTitle"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        webView.loadUrl(getIntent().getStringExtra("yigeId"));
    }

    private void resoveGuokrData() {
        Glide.with(Content.this).load(getIntent().getStringExtra("img")).asBitmap().centerCrop().into(zhihuContentImage);
        toolbarLayout.setTitle(getIntent().getStringExtra("title"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        webView.loadUrl(getIntent().getStringExtra("id"));
    }

    private void resolveZhihuData() {
        int id = getIntent().getIntExtra("id", 0);
        if (id != 0) {
            HttpUtil.SendOkHttpReuqest(Api.ZHIHU_NEWS + id, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Content.this, "获取内容失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response != null) {
                        String responseText = response.body().string();
                        content = GsonUtil.ZhihuContentUtil(responseText);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Glide.with(Content.this).load(content.getImage()).asBitmap().centerCrop().into(zhihuContentImage);
                                toolbarLayout.setTitle(content.getTitle());
                                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                                //显示具体文章内容
                                String result = handleZhihuContent(content.getBody());
                                webView.loadDataWithBaseURL("x-data://base", result, "text/html", "utf-8", null);
                            }
                        });
                    }
                }
            });
        }
    }


    private void initView() {
        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        zhihuContentImage = (ImageView) findViewById(R.id.zhihu_content_image);
        webView = (WebView) findViewById(R.id.web_view);
    }

    private String handleZhihuContent(String preBody) {
        preBody = preBody.replace("<div class=\"img-place-holder\">", "");
        preBody = preBody.replace("<div class=\"headline\">", "");
        String css = "<link rel=\"stylesheet\" href=\"http://news-at.zhihu.com//css//news_qa.auto.css?v=4b3e3\" type=\"text/css\">";
        String theme = "<body className=\"\" onload=\"onLoaded()\">";
        return new StringBuilder()
                .append("<!DOCTYPE html>\n")
                .append("<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\">\n")
                .append("<head>\n")
                .append("\t<meta charset=\"utf-8\" />")
                .append(css)
                .append("\n</head>\n")
                .append(theme)
                .append(preBody)
                .append("</body></html>").toString();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
