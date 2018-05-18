package com.monash.app.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.monash.app.App;
import com.monash.app.R;
import com.monash.app.bean.Friend;
import com.monash.app.bean.weather.CurrentWeather;
import com.monash.app.bean.weather.PredictWeather;
import com.monash.app.bean.weather.WeatherDaily;
import com.monash.app.utils.ClassUtil;
import com.monash.app.utils.ConfigUtil;
import com.monash.app.utils.EventUtil;
import com.monash.app.utils.HttpUtil;
import com.monash.app.utils.WeatherUtil;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.cur_temp_num) TextView tvCurrentTemp;
    @BindView(R.id.cur_time) TextClock tvCurrentTime;
    @BindView(R.id.city_name) TextView tvCityName;
    @BindView(R.id.weather_type) TextView tvWeatherType;
    @BindView(R.id.humidity) TextView tvHumidity;
    @BindView(R.id.visibility) TextView tvVisibility;
    @BindView(R.id.wind_direction) TextView tvWindDirection;
    @BindView(R.id.current_weather_img) ImageView ivCurrentWeather;

    @BindView(R.id.predict_weather_type_img1) ImageView ivPredictImage1;
    @BindView(R.id.predict_weather_type_img2) ImageView ivPredictImage2;
    @BindView(R.id.predict_weather_type_img3) ImageView ivPredictImage3;
    @BindView(R.id.predict_weather_type_img4) ImageView ivPredictImage4;
    @BindView(R.id.predict_lower_higher_temp_1) TextView tvLowerHigherTemp1;
    @BindView(R.id.predict_lower_higher_temp_2) TextView tvLowerHigherTemp2;
    @BindView(R.id.predict_lower_higher_temp_3) TextView tvLowerHigherTemp3;
    @BindView(R.id.predict_lower_higher_temp_4) TextView tvLowerHigherTemp4;
    @BindView(R.id.predict_wind_power_1) TextView tvPredictWind1;
    @BindView(R.id.predict_wind_power_2) TextView tvPredictWind2;
    @BindView(R.id.predict_wind_power_3) TextView tvPredictWind3;
    @BindView(R.id.predict_wind_power_4) TextView tvPredictWind4;
    @BindView(R.id.predict_weather_type_1) TextView tvPredictWeatherType1;
    @BindView(R.id.predict_weather_type_2) TextView tvPredictWeatherType2;
    @BindView(R.id.predict_weather_type_3) TextView tvPredictWeatherType3;
    @BindView(R.id.predict_weather_type_4) TextView tvPredictWeatherType4;
    @BindView(R.id.predict_one) TextView tvPredict1;
    @BindView(R.id.predict_two) TextView tvPredict2;
    @BindView(R.id.predict_three) TextView tvPredict3;
    @BindView(R.id.predict_four) TextView tvPredict4;

    private String latitude = "31.27";
    private String longitude = "120.73";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        Logger.addLogAdapter(new AndroidLogAdapter());

        initDrawer();
        initHeader();

        initWeatherInfo();
        tvCurrentTime.setFormat24Hour("HH:mm");
    }

    private void initMap() {
        AMapLocationClient mapLocationClient = new AMapLocationClient(getApplicationContext());
        AMapLocationListener mapLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null){
                    if (aMapLocation.getErrorCode() == 0){
                        Logger.d(aMapLocation.getLatitude() + ", " + aMapLocation.getLongitude());

                        latitude = String.valueOf(aMapLocation.getLatitude());
                        longitude = String.valueOf(aMapLocation.getLongitude());
                    }
                }
            }
        };
        mapLocationClient.setLocationListener(mapLocationListener);
        mapLocationClient.startLocation();
    }

    private void initWeatherInfo() {
        if (TextUtils.isEmpty(latitude) || TextUtils.isEmpty(longitude)){
            Logger.d("Get current location failed.");
            return;
        }
        // 向服务器请求天气信息
        HttpUtil.getInstance().get(WeatherUtil.getCurrentUrl(latitude, longitude),
                ConfigUtil.EVENT_LOAD_CURRENT_WEATHER);
        HttpUtil.getInstance().get(WeatherUtil.getPredictUrl(latitude, longitude, 4),
                ConfigUtil.EVENT_LOAD_PREDICT_WEATHER);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    void getCurrentWeather(EventUtil eventUtil){
        if(eventUtil.getEventType() == ConfigUtil.EVENT_LOAD_CURRENT_WEATHER) {
            try {
                JSONObject jsonObject = new JSONObject(eventUtil.getResult());
                JSONArray jsonArray = jsonObject.getJSONArray("results");
                String jsonObjStr = jsonArray.getJSONObject(0).toString();
                Gson gson = new Gson();
                CurrentWeather currentWeather = gson.fromJson(jsonObjStr, CurrentWeather.class);

                String cityName = currentWeather.getWeatherLocation().getCity_name();
                String temperature = currentWeather.getWeatherNow().getTemperature();
                String visibility = currentWeather.getWeatherNow().getVisibility();
                String humidity = currentWeather.getWeatherNow().getHumidity();
                String weatherType = currentWeather.getWeatherNow().getWeather_now();
                String windDirection = currentWeather.getWeatherNow().getWind_direction();

                if (cityName != null && temperature != null && weatherType != null) {
                    tvCityName.setText(cityName);
                    tvCurrentTemp.setText(temperature);
                    tvWeatherType.setText(weatherType);
                    tvHumidity.setText(humidity + "%");
                    tvVisibility.setText(visibility + "km");
                    tvWindDirection.setText(windDirection);
                    ivCurrentWeather.setImageResource(ClassUtil.getResId("ic_" +
                            currentWeather.getWeatherNow().getCode(), R.drawable.class));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getPredictWeather(EventUtil eventUtil){
        if(eventUtil.getEventType() == ConfigUtil.EVENT_LOAD_PREDICT_WEATHER) {
            try {
                JSONObject jsonObject = new JSONObject(eventUtil.getResult());
                JSONArray jsonArray = jsonObject.getJSONArray("results");
                String jsonObjStr = jsonArray.getJSONObject(0).toString();
                Gson gson = new Gson();
                PredictWeather predictWeather = gson.fromJson(jsonObjStr, PredictWeather.class);

                List<WeatherDaily> weatherDailies = predictWeather.getWeatherDailyList();
                initPredictInfo(weatherDailies);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void initPredictInfo(List<WeatherDaily> weatherDailies) {
        if (weatherDailies != null){
            if (weatherDailies.size() == 4){
                WeatherDaily one = weatherDailies.get(0);
                String code = one.getCode_day();
                ivPredictImage1.setImageResource(ClassUtil.getResId("ic_" + code, R.drawable.class));

                tvPredict1.setText(one.getDate());
                tvPredictWeatherType1.setText(one.getText_day());
                tvPredictWind1.setText(one.getWind_direction());
                String weather = String.format(getResources().getString(R.string.lower_higher_format),
                        one.getLow(), one.getHigh());
                tvLowerHigherTemp1.setText(weather);

                WeatherDaily two = weatherDailies.get(1);
                code = two.getCode_day();
                ivPredictImage2.setImageResource(ClassUtil.getResId("ic_" + code, R.drawable.class));
                tvPredict2.setText(two.getDate());
                tvPredictWeatherType2.setText(two.getText_day());
                tvPredictWind2.setText(two.getWind_direction());
                weather = String.format(getResources().getString(R.string.lower_higher_format),
                        two.getLow(), two.getHigh());
                tvLowerHigherTemp2.setText(weather);

                WeatherDaily three = weatherDailies.get(2);
                code = three.getCode_day();
                ivPredictImage3.setImageResource(ClassUtil.getResId("ic_" + code, R.drawable.class));
                tvPredict3.setText(three.getDate());
                tvPredictWeatherType3.setText(three.getText_day());
                tvPredictWind3.setText(three.getWind_direction());
                weather = String.format(getResources().getString(R.string.lower_higher_format),
                        three.getLow(), three.getHigh());
                tvLowerHigherTemp3.setText(weather);

                WeatherDaily four = weatherDailies.get(3);
                code = four.getCode_day();
                ivPredictImage4.setImageResource(ClassUtil.getResId("ic_" + code, R.drawable.class));
                tvPredict4.setText(four.getDate());
                tvPredictWeatherType4.setText(four.getText_day());
                tvPredictWind4.setText(four.getWind_direction());
                weather = String.format(getResources().getString(R.string.lower_higher_format),
                        four.getLow(), four.getHigh());
                tvLowerHigherTemp4.setText(weather);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    void getFriends(EventUtil eventUtil){
        if (eventUtil.getEventType() == ConfigUtil.EVENT_GET_USER_FRIENDS){
            Gson gson = new Gson();
            List<Friend> friends = gson.fromJson(eventUtil.getResult(), new TypeToken<List<Friend> >(){}.getType());
            App.setFriends(friends);
            Logger.d(friends);
            startActivity(new Intent(this, FriendsActivity.class));
        }
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_home;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            initWeatherInfo();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_friends) {
            if (user != null){
                String url = ConfigUtil.GET_USER_FRIENDS + String.valueOf(user.getStudID());
                HttpUtil.getInstance().get(url, ConfigUtil.EVENT_GET_USER_FRIENDS);
            }
        } else if (id == R.id.nav_search) {
            startActivity(new Intent(this, SearchActivity.class));
        } else if (id == R.id.nav_report) {
            startActivity(new Intent(this, ReportActivity.class));
        } else if (id == R.id.nav_logout) {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.putBoolean("isLogin", false);
            editor.putString("userEmail", "");
            editor.putString("userPassword", "");
            editor.apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else if (id == R.id.nav_about_us) {

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initDrawer() {
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initHeader() {
        View drawerView = navigationView.inflateHeaderView(R.layout.nav_header_home);

        ImageView userNameImage = drawerView.findViewById(R.id.header_image);
        TextView userName = drawerView.findViewById(R.id.header_user_name);
        TextView userEmail = drawerView.findViewById(R.id.header_user_email);
        if(user != null){
            String userFullName = user.getFirstName() + " " + user.getSurName();
            userName.setText(userFullName);
            userEmail.setText(user.getEmail());
        } else {
            Logger.d("user is null");
        }
    }
}
