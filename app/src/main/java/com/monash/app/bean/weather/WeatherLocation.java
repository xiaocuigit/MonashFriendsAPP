package com.monash.app.bean.weather;

import com.google.gson.annotations.SerializedName;

/**
 * Created by abner on 2018/4/6.
 */

public class WeatherLocation {
    @SerializedName("id")
    public String city_id;

    @SerializedName("name")
    public String city_name;

    public String country;

    @SerializedName("path")
    public String address;

    public String timezone;

    public String timezone_offset;

    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getTimezone_offset() {
        return timezone_offset;
    }

    public void setTimezone_offset(String timezone_offset) {
        this.timezone_offset = timezone_offset;
    }
}
