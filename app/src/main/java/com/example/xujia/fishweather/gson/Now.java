package com.example.xujia.fishweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xujia on 2017/7/27.
 */

public class Now {

    public String tmp;

    @SerializedName("cond")
    public More more;

    public class More {
        public String txt;
    }
}
