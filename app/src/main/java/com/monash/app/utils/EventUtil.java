package com.monash.app.utils;

/**
 * Created by abner on 2018/4/9.
 */

public class EventUtil {
    private int eventType;
    private String result;
    private int responseCode;

    EventUtil(int eventType, String result, int code) {
        this.eventType = eventType;
        this.result = result;
        this.responseCode = code;
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

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }
}
