package com.example.xujia.fishweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ProviderInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Trace;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.xujia.fishweather.gson.Forecast;
import com.example.xujia.fishweather.gson.Weather;
import com.example.xujia.fishweather.utils.CalendarUtils;
import com.example.xujia.fishweather.utils.HttpUtil;
import com.example.xujia.fishweather.utils.Utility;

import java.io.IOException;
import java.io.StringReader;

import okhttp3.Call;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView nowTemperature;
    private TextView nowInfo;
    private LinearLayout forecastLayout;
    private TextView aqiAqi;
    private TextView aqiPm25;
    private TextView suggestionComfort;
    private TextView suggestionCarWash;
    private TextView suggestionSport;

    private ImageView bingPic;
    private ImageView selectCity;

    public DrawerLayout drawerLayout;

    public SwipeRefreshLayout refresh;
    protected String currentWeatherID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        //初始化各控件
        initWidget();
        //解析JSON数据
        parseJson();
    }

    private void parseJson() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherDate = pref.getString("weather",null);
        /**
         * 判断是否有缓存数据，有缓存数据则直接解析天气数据
         * 没有缓存，则从服务器上查询天气信息
         */
        if (weatherDate != null){
            Weather weather = Utility.handleWeatherResponse(weatherDate);
            currentWeatherID = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            currentWeatherID = getIntent().getStringExtra("weather_id");
            requestWeather(currentWeatherID);
        }
    }

    /**
     * 根据天气ID获取城市的天气信息
     * @param weatherId
     */
    public void requestWeather(String weatherId) {
        String weatherUrl = Utility.URL + "weather?cityid=" + weatherId + "&key=" + Utility.KEY;
        HttpUtil.sendOKHttpRequest(weatherUrl, new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                refresh.setRefreshing(false);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败！", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseData);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)){
                            Log.d("Info","weather.status:"+weather.status);
                            SharedPreferences.Editor editor =
                                    PreferenceManager.getDefaultSharedPreferences(getApplication()).edit();
                            editor.putString("weather",responseData);
                            editor.apply();
                            showWeatherInfo(weather);
                            refresh.setRefreshing(false);
                        }
                    }
                });
            }
        });
        loadServicePicture();
    }

    private void showWeatherInfo(Weather weather) {

        if (weather.basic != null) {
            String cityName = weather.basic.cityName;
            String updateTime = weather.basic.update.updateTime.split(" ")[1];
            titleCity.setText(cityName);
            titleUpdateTime.setText(updateTime);
        }

        if (weather.now != null) {
            String temperature = weather.now.tmp + "℃";
            String weatherInfo = weather.now.more.txt;
            Log.d("Info",weatherInfo);
            nowTemperature.setText(temperature);
            nowInfo.setText(weatherInfo);
        }

        forecastLayout.removeAllViews();
        if (weather.forecastList.size() > 0) {
            for (Forecast forecast : weather.forecastList) {
                View v = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
                TextView date = (TextView) v.findViewById(R.id.forecast_item_date);
                ImageView weatherIcon = (ImageView) v.findViewById(R.id.weather_icon);
                TextView info = (TextView) v.findViewById(R.id.forecast_item_info);
                TextView maxTmp = (TextView) v.findViewById(R.id.forecast_item_tmp_max);
                TextView minTmp = (TextView) v.findViewById(R.id.forecast_item_tmp_min);

                String currentDate = CalendarUtils.getCurrentDateShort();
                if (currentDate != null && currentDate.equals(forecast.date)) {
                    date.setText("今天");
                } else if (currentDate != null && CalendarUtils.getDays(forecast.date,currentDate) == 1) {
                    date.setText("明天");
                } else if (currentDate != null && CalendarUtils.getDays(forecast.date,currentDate) == 2) {
                    date.setText("后天");
                } else {
                    String dayOfWeek = CalendarUtils.getWeekOfDate(forecast.date);
                    date.setText(dayOfWeek);
                }
                int ResId = Utility.selectWeatherIcon(forecast.more.txt_d);
                Glide.with(getApplication()).load(ResId).into(weatherIcon);
                info.setText(forecast.more.txt_d+"");
                maxTmp.setText(forecast.temperature.max+"");
                minTmp.setText(forecast.temperature.min+"");

                forecastLayout.addView(v);
            }
        }

        if (weather.aqi != null){
            aqiAqi.setText(weather.aqi.city.aqi);
            aqiPm25.setText(weather.aqi.city.pm25);
        }

        if (weather.suggestion != null){
            suggestionComfort.setText("舒适度：" + weather.suggestion.comfort.txt);
            suggestionCarWash.setText("洗车指数：" + weather.suggestion.carWish.txt);
            suggestionSport.setText("运动建议：" + weather.suggestion.sport.txt);
        }

        Intent intent = new Intent(this,AutoUpdateService.class);
        startService(intent);

    }

    private void initWidget() {
        //title
        titleCity = (TextView) findViewById(R.id.title_cityName);
        titleUpdateTime = (TextView) findViewById(R.id.title_updateTime);
        //now
        nowTemperature = (TextView) findViewById(R.id.now_temperature);
        nowInfo = (TextView) findViewById(R.id.now_info);
        //forecast
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        //AQI
        aqiAqi = (TextView) findViewById(R.id.aqi_aqi);
        aqiPm25 = (TextView) findViewById(R.id.aqi_pm25);
        //suggestion
        suggestionComfort = (TextView) findViewById(R.id.suggestion_comfort);
        suggestionCarWash = (TextView) findViewById(R.id.suggestion_wash);
        suggestionSport = (TextView) findViewById(R.id.suggestion_sport);

        bingPic = (ImageView) findViewById(R.id.back_image);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String loadPicture = pref.getString("bingPic",null);

        Glide.with(this).load(R.drawable.default_background).into(bingPic);

        if (loadPicture != null){
            Glide.with(this).load(loadPicture).into(bingPic);
        } else {
            loadServicePicture();
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        selectCity = (ImageView) findViewById(R.id.select_city);
        selectCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        refresh = (SwipeRefreshLayout) findViewById(R.id.refresh);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(currentWeatherID);
            }
        });
    }

    /**
     * 加载bing每日一图
     */
    private void loadServicePicture() {
        String picUrl = Utility.PIC_URL;
        HttpUtil.sendOKHttpRequest(picUrl, new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(getApplication()).load(R.drawable.default_background).into(bingPic);
                    }
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingUrl = response.body().string();
                SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(getApplication()).edit();
                edit.putString("bingPic",bingUrl);
                edit.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(getApplication()).load(bingUrl).into(bingPic);
                    }
                });
            }
        });
    }
}
