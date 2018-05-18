package com.monash.app.utils;

/**
 * Created by abner on 2018/4/9.
 */

public class EventUtil {
    private int eventType;
    private String result;

    EventUtil(int eventType, String result) {
        this.eventType = eventType;
        this.result = result;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
