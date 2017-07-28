package com.example.xujia.fishweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xujia on 2017/7/27.
 */

public class Forecast {

    public String date;

    @SerializedName("tmp")
    public Temperature temperature;

    @SerializedName("cond")
    public More more;

    public class Temperature {
        public String max;

        public String min;
    }

    public class More {
        public String txt_d;
    }
}
