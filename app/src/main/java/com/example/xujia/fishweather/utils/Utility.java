package com.example.xujia.fishweather.utils;

import android.text.TextUtils;
import android.util.Log;

import com.example.xujia.fishweather.ChooseAreaFragment;
import com.example.xujia.fishweather.R;
import com.example.xujia.fishweather.db.City;
import com.example.xujia.fishweather.db.County;
import com.example.xujia.fishweather.db.Province;
import com.example.xujia.fishweather.gson.Weather;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xujia on 2017/7/25.
 */

public class Utility  {
    /**
     * KEY:和风天气的API
     */
    public static final String KEY = "bc0418b57b2d4918819d3974ac1285d9";

    /**
     * 获取天气信息的网址
     */
    public static final String URL = "http://guolin.tech/api/";

    /**
     * 获取天气信息的网址
     */
    public static final String PIC_URL = "http://guolin.tech/api/bing_pic";

    /**
     * 解析跟处理服务器返回的province数据
     */
    public static boolean handleProvinceResponse(String response){

        if (!TextUtils.isEmpty(response)){
            JSONArray allProvinces = null;
            try {
                allProvinces = new JSONArray(response);
                for (int i=0; i<allProvinces.length(); i++){
                    JSONObject object = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(object.getString("name"));
                    province.setProvinceCode(object.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析跟处理服务器返回的city数据
     */
    public static boolean handleCityResponse(String response,int provinceId){

        if (!TextUtils.isEmpty(response)){
            JSONArray allCities = null;
            try {
                allCities = new JSONArray(response);
                for (int i=0;i<allCities.length();i++){
                    JSONObject object = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(object.getString("name"));
                    city.setCityCode(object.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析跟处理服务器返回的county数据
     */
    public static boolean handleCountyResponse(String response,int cityId){

        if (!TextUtils.isEmpty(response)){
            Log.d(ChooseAreaFragment.TAG,"!TextUtils.isEmpty(response)");
            JSONArray allCounties = null;
            try {
                allCounties = new JSONArray(response);
                for (int i=0;i<allCounties.length();i++){
                    JSONObject object = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(object.getString("name"));
                    county.setWeatherId(object.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static Weather handleWeatherResponse(String jsonString){
        try {
            JSONObject object = new JSONObject(jsonString);
            JSONArray jsonArray = object.getJSONArray("HeWeather");
            String weatherContent = jsonArray.get(0).toString();
            Weather weather = new Gson().fromJson(weatherContent,Weather.class);
            return weather;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据天气信息选择天气图标
     * @param weatherInfo  天气信息
     */
    public static int selectWeatherIcon(String text) {
        //default ResId is icon N/A
        int ResId = R.drawable.unknown;
        if (text.isEmpty()) return ResId;
        String weatherInfo = text.trim();

        if (weatherInfo.equals("晴")) return R.drawable.sunny;
        else if (weatherInfo.equals("多云")) return R.drawable.cloudy;
        else if (weatherInfo.equals("少云")) return R.drawable.few_clouds;
        else if (weatherInfo.equals("晴间多云")) return R.drawable.sartly_cloudy;
        else if (weatherInfo.equals("阴")) return R.drawable.svercast;
        else if (weatherInfo.equals("有风")) return R.drawable.windy;
        else if (weatherInfo.equals("平静")) return R.drawable.calm;
        else if (weatherInfo.equals("微风")) return R.drawable.light_breeze;
        else if (weatherInfo.equals("和风")) return R.drawable.light_breeze;
        else if (weatherInfo.equals("清风")) return R.drawable.light_breeze;
        else if (weatherInfo.equals("强风/劲风")) return R.drawable.strong_breeze;
        else if (weatherInfo.equals("强风")) return R.drawable.strong_breeze;
        else if (weatherInfo.equals("劲风")) return R.drawable.strong_breeze;
        else if (weatherInfo.equals("疾风")) return R.drawable.strong_breeze;
        else if (weatherInfo.equals("大风")) return R.drawable.strong_breeze;
        else if (weatherInfo.equals("烈风")) return R.drawable.storm;
        else if (weatherInfo.equals("风暴")) return R.drawable.storm;
        else if (weatherInfo.equals("狂爆风")) return R.drawable.storm;
        else if (weatherInfo.equals("飓风")) return R.drawable.storm;
        else if (weatherInfo.equals("龙卷风")) return R.drawable.storm;
        else if (weatherInfo.equals("热带风暴")) return R.drawable.storm;
        else if (weatherInfo.equals("阵雨")) return R.drawable.shower_rain;
        else if (weatherInfo.equals("强阵雨")) return R.drawable.heavy_shower_rain;
        else if (weatherInfo.equals("雷阵雨")) return R.drawable.thunder_shower;
        else if (weatherInfo.equals("强雷阵雨")) return R.drawable.heavy_thunderstorm;
        else if (weatherInfo.equals("雷阵雨伴有冰雹")) return R.drawable.hail;
        else if (weatherInfo.equals("小雨")) return R.drawable.light_rain;
        else if (weatherInfo.equals("中雨")) return R.drawable.moderate_rain;
        else if (weatherInfo.equals("大雨")) return R.drawable.heavy_rain;
        else if (weatherInfo.equals("极端降雨")) return R.drawable.extreme_rain;
        else if (weatherInfo.equals("毛毛雨/细雨")) return R.drawable.drizzle_rain;
        else if (weatherInfo.equals("细雨")) return R.drawable.drizzle_rain;
        else if (weatherInfo.equals("毛毛雨")) return R.drawable.drizzle_rain;
        else if (weatherInfo.equals("暴雨")) return R.drawable.heavy_rain;
        else if (weatherInfo.equals("大暴雨")) return R.drawable.heavy_storm;
        else if (weatherInfo.equals("特大暴雨")) return R.drawable.extreme_rain;
        else if (weatherInfo.equals("冻雨")) return R.drawable.freezing_rain;
        else if (weatherInfo.equals("小雪")) return R.drawable.light_snow;
        else if (weatherInfo.equals("中雪")) return R.drawable.moderate_snow;
        else if (weatherInfo.equals("大雪")) return R.drawable.heavy_rnow;
        else if (weatherInfo.equals("暴雪")) return R.drawable.snow_storm;
        else if (weatherInfo.equals("雨夹雪")) return R.drawable.sleet;
        else if (weatherInfo.equals("雨雪天气")) return R.drawable.sain_and_snow;
        else if (weatherInfo.equals("阵雨夹雪")) return R.drawable.shower_snow;
        else if (weatherInfo.equals("阵雪")) return R.drawable.snow_flurry;
        else if (weatherInfo.equals("薄雾")) return R.drawable.mist;
        else if (weatherInfo.equals("雾")) return R.drawable.foggy;
        else if (weatherInfo.equals("霾")) return R.drawable.haze;
        else if (weatherInfo.equals("扬沙")) return R.drawable.sand;
        else if (weatherInfo.equals("浮尘")) return R.drawable.dust;
        else if (weatherInfo.equals("沙尘暴")) return R.drawable.dust_storm;
        else if (weatherInfo.equals("强沙尘暴")) return R.drawable.dust_storm;
        else if (weatherInfo.equals("热")) return R.drawable.hot;
        else if (weatherInfo.equals("冷")) return R.drawable.cold;
        else if (weatherInfo.equals("未知")) return R.drawable.unknown;
        else return ResId;
    }
}
