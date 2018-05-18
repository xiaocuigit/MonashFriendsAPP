package com.monash.app.utils;

/**
 * Created by abner on 2018/4/9.
 *
 */

public class ConfigUtil {
    // GET PUT POST DELETE URL
    private static String BASE_PROFILE_URL = "http://47.106.170.136:8080/friendfinder/webresources/app.profile/";
    private static String BASE_LOCATION_URL = "http://47.106.170.136:8080/friendfinder/webresources/app.location/";
    private static String BASE_FRIEND_URL = "http://47.106.170.136:8080/friendfinder/webresources/app.friendship/";

    public static String GET_MOVIE_URL = "http://api.douban.com/v2/movie/search?q=";
    public static String GET_MOVIE_SUMMARY = "http://api.douban.com/v2/movie/subject/";

    public static String POST_USER_REGISTER = BASE_PROFILE_URL + "register/";
    public static String POST_USER_UPDATE = BASE_PROFILE_URL + "completeInfo/";
    public static String POST_SHOW_RESULT_STUDENT_IN_MAP = BASE_LOCATION_URL + "showStudentsInMap/";
    public static String POST_USER_LOCATION = BASE_LOCATION_URL +"upLoadLocation/";

    public static String GET_SEARCH_FRIENDS = BASE_PROFILE_URL + "findByAnyAttribute/";
    public static String GET_USER_BY_EMAIL = BASE_PROFILE_URL + "findByEmail/";
    public static String GET_UNITS_REPORTS = BASE_PROFILE_URL + "countFavUnit";
    public static String GET_USER_FRIENDS = BASE_FRIEND_URL + "searchFriends/";
    public static String GET_LOCATION_REPORT = BASE_LOCATION_URL + "countFrequency/";

    public static String POST_ADD_FRIEND = BASE_FRIEND_URL + "addFriend";
    public static String POST_DELETE_FRIEND = BASE_FRIEND_URL + "deleteFriend";



    // EVENT TYPE
    public static  int EVENT_LOGIN = 0;
    public static int EVENT_SIGN_UP = 1;
    public static int EVENT_EDIT_USER_INFO = 2;

    public static int EVENT_LOAD_CURRENT_WEATHER = 3;
    public static int EVENT_LOAD_PREDICT_WEATHER = 4;
    public static int EVENT_GET_MOVIE_SUMMARY = 5;
    public static int EVENT_GET_UNITS_REPORT = 6;
    public static int EVENT_GET_LOCATION_REPORT = 7;
    public static int EVENT_ADD_FRIEND = 8;
    public static int EVENT_DELETE_FRIEND = 9;
    public static int EVENT_SEARCH_FRIENDS = 10;
    public static int EVENT_GET_USER_FRIENDS = 11;
    public static int EVENT_GET_MOVIE_INFO = 12;
    public static int EVENT_GET_RESULT_LOCATION = 13;
    public static int EVENT_GET_FRIEND_LOCATION = 14;
}
