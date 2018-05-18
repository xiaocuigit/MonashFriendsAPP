package com.monash.app.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by abner on 2018/4/14.
 *
 */

public class Friend implements Serializable {
    private String startDate;

    @SerializedName("student")
    private User friend;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public User getFriend() {
        return friend;
    }

    public void setFriend(User friend) {
        this.friend = friend;
    }
}
