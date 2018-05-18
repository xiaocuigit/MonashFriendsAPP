package com.monash.app.bean.weather;

import com.google.gson.annotations.SerializedName;

/**
 * Created by abner on 2018/4/6.
 */

public class WeatherNow {
    @SerializedName("text")
    public String weather_now;
    public String code;
    public String temperature;
    public String pressure;
    public String humidity;
    public String visibility;
    public String wind_direction;
    public String wind_direction_degree;
    public String wind_speed;
    public String wind_scale;

    public String getWeather_now() {
        return weather_now;
    }

    public void setWeather_now(String weather_now) {
        this.weather_now = weather_now;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getWind_direction() {
        return wind_direction;
    }

    public void setWind_direction(String wind_direction) {
        this.wind_direction = wind_direction;
    }

    public String getWind_direction_degree() {
        return wind_direction_degree;
    }

    public void setWind_direction_degree(String wind_direction_degree) {
        this.wind_direction_degree = wind_direction_degree;
    }

    public String getWind_speed() {
        return wind_speed;
    }

    public void setWind_speed(String wind_speed) {
        this.wind_speed = wind_speed;
    }

    public String getWind_scale() {
        return wind_scale;
    }

    public void setWind_scale(String wind_scale) {
        this.wind_scale = wind_scale;
    }
}
