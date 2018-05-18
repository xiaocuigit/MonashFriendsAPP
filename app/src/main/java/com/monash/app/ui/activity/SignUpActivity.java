package com.monash.app.ui.activity;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.monash.app.R;
import com.monash.app.utils.ConfigUtil;
import com.monash.app.utils.EventUtil;
import com.monash.app.utils.HttpUtil;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends AppCompatActivity {


    @BindView(R.id.et_first_name) TextInputEditText user_first_name;

    @BindView(R.id.et_surname) TextInputEditText user_surname;

    @BindView(R.id.et_email) TextInputEditText user_email;

    @BindView(R.id.et_user_id) TextInputEditText user_id;

    @BindView(R.id.et_password) TextInputEditText user_password;

    @BindView(R.id.et_password_again) TextInputEditText user_password_again;

    private String firstName;
    private String surName;
    private Integer studID;
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        Logger.addLogAdapter(new AndroidLogAdapter());
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

    @OnClick(R.id.btn_sign_up)
    void signUp(){
        if (checkInput()){
            JSONObject userInfo = packageJSON();
            try {
                HttpUtil.getInstance().post(ConfigUtil.POST_USER_REGISTER, ConfigUtil.EVENT_SIGN_UP, userInfo.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    void submitUserInfo(EventUtil eventUtil){
        if (eventUtil.getEventType() == ConfigUtil.EVENT_SIGN_UP){
            Logger.d(eventUtil.getResult());
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    @OnClick(R.id.link_login)
    void backToLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean checkInput() {
        boolean flag = true;
        firstName = user_first_name.getText().toString();
        surName = user_surname.getText().toString();
        String studIDStr = user_id.getText().toString();
        email = user_email.getText().toString();
        password = user_password.getText().toString();
        try {
            studID = Integer.parseInt(studIDStr);
        } catch (NumberFormatException e){
            e.printStackTrace();
        }
        String passwordAgain = user_password_again.getText().toString();

        if (TextUtils.isEmpty(firstName)){
            user_first_name.setError("Please input first name");
            flag = false;
        } else if (TextUtils.isEmpty(surName)){
            user_surname.setError("Please input surname");
            flag = false;
        } else if (TextUtils.isEmpty(studIDStr)){
            user_id.setError("Please input student ID");
            flag = false;
        } else if (TextUtils.isEmpty(email)){
            user_email.setError("Please input monash email");
            flag = false;
        } else if (TextUtils.isEmpty(password)){
            user_password.setError("Please input password");
            flag = false;
        } else if (TextUtils.isEmpty(passwordAgain)){
            user_password_again.setError("Please input password again");
            flag = false;
        } else if (!passwordAgain.equals(password)){
            user_password_again.setError("Please input same password");
            user_password_again.setText("");
            flag = false;
        } else if (email.matches("w+([-+.]w+)*@w+([-.]w+)*.w+([-.]w+)*")){
            user_email.setError("Please input right email address");
            flag = false;
        }
        return flag;
    }

    private JSONObject packageJSON(){
        JSONObject object = new JSONObject();
        try {
            object.put("studID", studID);
            object.put("firstName", firstName);
            object.put("surName", surName);
            object.put("email", email);
            object.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }
}
