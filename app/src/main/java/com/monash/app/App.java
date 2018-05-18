package com.monash.app;

import android.app.Application;
import android.content.Context;

import com.github.mikephil.charting.formatter.IFillFormatter;
import com.monash.app.bean.Friend;
import com.monash.app.bean.User;

import org.litepal.LitePal;

import java.util.List;

/**
 * Created by abner on 2018/4/7.
 *
 */

public class App extends Application {

    private static User user;
    private static List<User> users;
    private static List<Friend> friends;

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
    }

    public static User getUser() {
        if (user != null)
            return user;
        else
            return null;
    }

    public static void setUser(User mUser) {
        user = mUser;
    }

    public static List<User> getUsers() {
        return users;
    }

    public static void setUsers(List<User> users) {
        App.users = users;
    }

    public static List<Friend> getFriends() {
        return friends;
    }

    public static void setFriends(List<Friend> friends) {
        App.friends = friends;
    }
}
