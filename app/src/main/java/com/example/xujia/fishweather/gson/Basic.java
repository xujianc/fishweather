package com.example.xujia.fishweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xujia on 2017/7/27.
 */

public class Basic {

    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update {
        @SerializedName("loc")
        public String updateTime;
    }
}
