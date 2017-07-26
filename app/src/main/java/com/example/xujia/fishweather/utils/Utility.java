package com.example.xujia.fishweather.utils;

import android.text.TextUtils;
import android.util.Log;

import com.example.xujia.fishweather.ChooseAreaFragment;
import com.example.xujia.fishweather.db.City;
import com.example.xujia.fishweather.db.County;
import com.example.xujia.fishweather.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xujia on 2017/7/25.
 */

public class Utility  {

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
}
