package com.monash.app.bean.weather;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by abner on 2018/4/6.
 */

public class CurrentWeather {
    @SerializedName("location")
    public WeatherLocation weatherLocation;

    @SerializedName("now")
    public WeatherNow weatherNow;

    @SerializedName("last_update")
    public Date lastUpdate;

    public WeatherLocation getWeatherLocation() {
        return weatherLocation;
    }

    public void setWeatherLocation(WeatherLocation weatherLocation) {
        this.weatherLocation = weatherLocation;
    }

    public WeatherNow getWeatherNow() {
        return weatherNow;
    }

    public void setWeatherNow(WeatherNow weatherNow) {
        this.weatherNow = weatherNow;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
