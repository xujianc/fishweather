package com.example.xujia.fishweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xujia on 2017/7/27.
 */

public class AQI {

    public AqiCity city;

    public class AqiCity {

        public String aqi;

        public String pm25;
    }
}
