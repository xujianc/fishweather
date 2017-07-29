package com.example.xujia.fishweather.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by xujia on 2017/7/29.
 */

public class CalendarUtils {

    /**
     * 获取当前年月日
     * @return
     */
    public static String getCurrentDateShort() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 根据一个日期，返回是星期几的字符串
     *
     * @return
     */
    /**
     * 获取当前日期是星期几<br>
     *
     * @param dateString
     * @return 当前日期是星期几
     */
    public static String getWeekOfDate(String dateString) {

        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dt = null;
        try {
            dt = sdf.parse(dateString);
            Calendar cal = Calendar.getInstance();
            cal.setTime(dt);
            int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
            if (w < 0) w = 0;
            return weekDays[w];
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return weekDays[0];
    }

    /**
     * 两个时间之间的天数
     *
     * @param firstDate
     * @param secondDate
     * @return
     */
    public static long getDays(String firstDate, String secondDate) {
        if (firstDate == null || firstDate.equals(""))
            return 0;
        if (secondDate == null || secondDate.equals(""))
            return 0;
        // 转换为标准时间
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = myFormatter.parse(firstDate);
            date2 = myFormatter.parse(secondDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long day = (date1.getTime() - date2.getTime()) / (24 * 60 * 60 * 1000);
        return day;
    }
}
