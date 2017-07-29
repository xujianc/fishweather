package com.example.xujia.fishweather;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.example.xujia.fishweather.gson.Weather;
import com.example.xujia.fishweather.utils.HttpUtil;
import com.example.xujia.fishweather.utils.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class AutoUpdateService extends Service {

    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        updateWeather();
        updateBingPic();

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        int anHour  = 60*60*1000;  //一个小时的毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour*2;

        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this,0,i,0);
        alarmManager.cancel(pi);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);

        return super.onStartCommand(intent, flags, startId);
    }
    /**
     * 更新bing每日一图
     */
    private void updateBingPic() {
        String picUrl = Utility.PIC_URL;
        HttpUtil.sendOKHttpRequest(picUrl, new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingUrl = response.body().string();
                if (!bingUrl.isEmpty()){
                    SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(getApplication()).edit();
                    edit.putString("bingPic",bingUrl);
                    edit.apply();
                }
            }
        });
    }
    /**
     * 更新天气信息
     */
    private void updateWeather() {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = pref.getString("weather",null);
        if (weatherString != null){
            Weather weather = Utility.handleWeatherResponse(weatherString);
            String weatherUrl = Utility.URL + "weather?cityid=" + weather.basic.weatherId + "&key=" + Utility.KEY;

            HttpUtil.sendOKHttpRequest(weatherUrl, new okhttp3.Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    //Toast.makeText(AutoUpdateService.this, "获取天气信息失败！", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseData = response.body().string();
                    Weather weather = Utility.handleWeatherResponse(responseData);
                    if (weather.status != null && "ok".equals(weather.status)){
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weather",responseData);
                        editor.apply();
                    }
                }
            });
        }
    }
}
