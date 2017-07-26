package com.example.xujia.fishweather.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by xujia on 2017/7/25.
 */

public class HttpUtil {

    public static void sendOKHttpRequest(String url,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
