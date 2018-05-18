package com.monash.app.bean.weather;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * Created by abner on 2018/4/6.
 */

public class PredictWeather {
    @SerializedName("location")
    public WeatherLocation weatherLocation = null;

    @SerializedName("daily")
    public List<WeatherDaily> weatherDailyList = null;

    @SerializedName("last_update")
    public Date lastUpdate = null;

    public WeatherLocation getWeatherLocation() {
        return weatherLocation;
    }

    public void setWeatherLocation(WeatherLocation weatherLocation) {
        this.weatherLocation = weatherLocation;
    }

    public List<WeatherDaily> getWeatherDailyList() {
        return weatherDailyList;
    }

    public void setWeatherDailyList(List<WeatherDaily> weatherDailyList) {
        this.weatherDailyList = weatherDailyList;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
