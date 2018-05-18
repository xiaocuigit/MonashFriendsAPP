package com.monash.app.utils;

import com.google.gson.Gson;
import com.monash.app.bean.weather.CurrentWeather;
import com.monash.app.bean.weather.PredictWeather;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by abner on 2018/4/6.
 */

public class WeatherUtil {
//    private static String LANGUAGE = "zh-Hans";
    private static String LANGUAGE = "en";
    private static String UNIT = "c";
    private static String KEY = "nf2fsvb0v3abpuww";

    /**
     * 获取访问心知天气服务器未来几天的天气信息的URL
     * @param lat   纬度
     * @param lon   经度
     * @param days  预测的天数
     * @return
     */
    public static String getPredictUrl(String lat, String lon, Integer days){
        return "https://api.seniverse.com/v3/weather/daily.json?key=" + KEY +
                "&language=" + LANGUAGE + "&unit=" + UNIT + "&location=" + lat + ":" + lon + "&start=0&days=" + days.toString();
    }

    /**
     * 获取访问心知天气服务器天气实时信息的URL
     * @param lat  纬度
     * @param lon  经度
     * @return
     */
    public static String getCurrentUrl(String lat, String lon){
        return "https://api.seniverse.com/v3/weather/now.json?key=" + KEY +
                "&language=" + LANGUAGE + "&unit=" + UNIT + "&location=" + lat + ":" + lon;
    }
}
