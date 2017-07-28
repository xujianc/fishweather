package com.example.xujia.fishweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xujia on 2017/7/27.
 */

public class Suggestion {

    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public CarWish carWish;

    public Sport sport;

    public class Comfort {
        public String txt;
    }

    public class CarWish {
        public String txt;
    }

    public class Sport {
        public String txt;
    }
}
