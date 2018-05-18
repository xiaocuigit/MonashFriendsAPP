package com.monash.app.utils;

import java.lang.reflect.Field;

/**
 * Created by abner on 2018/5/4.
 */

public class ClassUtil {
    /**
     * 通过反射获取动态字符串对应的资源id
     * @param variableName
     * @param c
     * @return
     */
    public static int getResId(String variableName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
