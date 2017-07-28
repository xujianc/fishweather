package com.example.xujia.fishweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by xujia on 2017/7/27.
 */

public class Weather {
    public String status;

    public Basic basic;

    public AQI aqi;

    public Now now;

    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;

    /**
     * Returns a string representation of the object. In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {

        Weather weather = this;
        if ("ok".equals(weather.status)) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(weather.basic.cityName + ":" + weather.basic.weatherId + ":" + weather.basic.update.updateTime + "\n");
            stringBuffer.append(weather.now.tmp + ":" + weather.now.more.txt + "\n");
            stringBuffer.append(weather.forecastList.size() + ":");
            for (Forecast forecast : weather.forecastList) {
                stringBuffer.append(forecast.date + ":" + forecast.more.txt_d + ":" + forecast.temperature.max + ":" + forecast.temperature.min + "\n");
            }
            stringBuffer.append(weather.aqi.city.aqi + ":pm25:" + weather.aqi.city.pm25 + "\n");
            stringBuffer.append(weather.suggestion.comfort.txt + "\n" + weather.suggestion.carWish.txt + "\n" + weather.suggestion.sport.txt + "\n");

            return stringBuffer.toString();
        } else {
            return weather.status;
        }
    }
}
