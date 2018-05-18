package com.monash.app.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.monash.app.bean.User;

import java.util.List;

/**
 * Created by abner on 2018/4/5.
 */

public class GsonUtil {
    private static GsonUtil instance;
    private Gson gson;

    private GsonUtil(){
        gson = new Gson();
    }

    public static GsonUtil getInstance(){
        return instance == null ? instance = new GsonUtil() : instance;
    }

    public List<User> getUsers(String userInfo){
        List<User> users = gson.fromJson(userInfo, new TypeToken<List<User>>()
        {}.getType());
        return users;
    }

}
