package com.monash.app.bean;

/**
 * Created by Jack on 2018/4/17.
 */

public class SearchResult {
    private User user;
    private double latitude;
    private double longitude;

    public User getUser() {
        return user;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public SearchResult(User user, double latitude, double longitude) {
        this.user = user;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public SearchResult(){

    }
}
