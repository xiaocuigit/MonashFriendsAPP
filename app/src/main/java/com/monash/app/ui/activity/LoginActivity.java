package com.monash.app.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.gson.Gson;
import com.monash.app.App;
import com.monash.app.R;
import com.monash.app.bean.User;
import com.monash.app.bean.weather.CurrentWeather;
import com.monash.app.bean.weather.PredictWeather;
import com.monash.app.bean.weather.WeatherDaily;
import com.monash.app.utils.ConfigUtil;
import com.monash.app.utils.EventUtil;
import com.monash.app.utils.GsonUtil;
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

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.input_email) TextInputEditText user_email;
    @BindView(R.id.input_password) TextInputEditText user_password;
    @BindView(R.id.tv_signup_account) TextView sign_up_account;
    @BindView(R.id.cb_autoLogin) CheckBox auto_login;

    private String userEmail;
    private String userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        Logger.addLogAdapter(new AndroidLogAdapter());
        autoLogin();
    }

    private void autoLogin() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean isLogin = preferences.getBoolean("isLogin", false);
        if (isLogin){
            userEmail = preferences.getString("userEmail", "");
            userPassword = preferences.getString("userPassword", "");
            HttpUtil.getInstance().get(ConfigUtil.GET_USER_BY_EMAIL + userEmail, ConfigUtil.EVENT_LOGIN);
        }
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

    @OnClick(R.id.btn_login)
    void login(){
        if(validateInput()){
            // 检测用户输入无误后，向服务器验证该用户的信息
            HttpUtil.getInstance().get(ConfigUtil.GET_USER_BY_EMAIL + userEmail, ConfigUtil.EVENT_LOGIN);
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            if (auto_login.isChecked()){
                editor.putBoolean("isLogin", true);
                editor.putString("userEmail", userEmail);
                editor.putString("userPassword", userPassword);
            } else {
                editor.putBoolean("isLogin", false);
                editor.putString("userEmail", "");
                editor.putString("userPassword", "");
            }
            editor.apply();
        }
    }

    @OnClick(R.id.tv_signup_account)
    void createAccount(){
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    void checkUserInfo(EventUtil eventUtil){
        if (eventUtil.getEventType() == ConfigUtil.EVENT_LOGIN){
            String userInfo = eventUtil.getResult();
            if (userInfo.equals("")){
                Logger.d("error");
                return;
            }
            List<User> users = GsonUtil.getInstance().getUsers(userInfo);
            if (!users.isEmpty()){
                User user = users.get(0);
                if(user.getPassword().equals(userPassword)){
                    Logger.d(user.getPassword() + "\n" + user.getEmail());
                    Logger.d(user.getFirstName() + " " + user.getSurName());

                    App.setUser(user);
                    Intent intent = new Intent(this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    user_password.setError("Password Incorrect");
                    user_password.setText("");
                }
            }
        }
    }

    private boolean validateInput() {
        userEmail = user_email.getText().toString();
        userPassword = user_password.getText().toString();
//        userEmail = "jtao0001@student.monash.edu";
//        userEmail = "myao0001@student.monash.edu";
//        userPassword = "123456";

        boolean flag = true;

        if(TextUtils.isEmpty(userEmail)){
            user_email.setError("Please input right email");
            flag = false;
        }
        if(TextUtils.isEmpty(userPassword)){
            user_password.setError("Please input right password");
            flag = false;
        }
        return flag;
    }
}
