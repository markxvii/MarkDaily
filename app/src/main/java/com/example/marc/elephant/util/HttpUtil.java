package com.example.marc.elephant.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by marc on 17-4-12.
 */

public class HttpUtil {
    public static void SendOkHttpReuqest(String address,okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
