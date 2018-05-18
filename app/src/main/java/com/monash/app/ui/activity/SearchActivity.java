package com.monash.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import com.monash.app.App;
import com.monash.app.R;
import com.monash.app.bean.User;
import com.monash.app.utils.ConfigUtil;
import com.monash.app.utils.EventUtil;
import com.monash.app.utils.GsonUtil;
import com.monash.app.utils.HttpUtil;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends BaseActivity {

    @BindView(R.id.cb_birthday) CheckBox cbBirthDay;
    @BindView(R.id.cb_gender) CheckBox cbGender;
    @BindView(R.id.cb_course) CheckBox cbCourse;
    @BindView(R.id.cb_study_mode) CheckBox cbStudyMode;
    @BindView(R.id.cb_nationality) CheckBox cbNationality;
    @BindView(R.id.cb_language) CheckBox cbLanguage;
    @BindView(R.id.cb_suburb) CheckBox cbSuburb;
    @BindView(R.id.cb_favMovie) CheckBox cbFavMovie;
    @BindView(R.id.cb_favUnit) CheckBox cbFavUnit;
    @BindView(R.id.cb_favSport) CheckBox cbFavSport;
    @BindView(R.id.tv_search_hint) TextView tvSearchHint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_search;
    }

    @OnClick(R.id.btn_search)
    void searchFinds(){
        String searchAttributes = "";

        if (cbBirthDay.isChecked()){
            searchAttributes += "birthDate ";
        }
        if (cbGender.isChecked()){
            searchAttributes += "gender ";
        }
        if (cbCourse.isChecked()){
            searchAttributes += "course ";
        }
        if (cbStudyMode.isChecked()){
            searchAttributes += "studyMode ";
        }
        if (cbNationality.isChecked()){
            searchAttributes += "nationality ";
        }
        if (cbLanguage.isChecked()){
            searchAttributes += "language ";
        }
        if (cbSuburb.isChecked()){
            searchAttributes += "suburb ";
        }
        if (cbFavMovie.isChecked()){
            searchAttributes += "favMovie ";
        }
        if (cbFavUnit.isChecked()){
            searchAttributes += "favMovie ";
        }
        if (cbFavSport.isChecked()){
            searchAttributes += "favSport";
        }
        if (TextUtils.isEmpty(searchAttributes)){
            tvSearchHint.setText("Please select at least one attributes");
            tvSearchHint.setVisibility(View.VISIBLE);
        } else {
            tvSearchHint.setVisibility(View.GONE);
            if (user != null){
                Integer studID = user.getStudID();
                String searchUrl = ConfigUtil.GET_SEARCH_FRIENDS + String.valueOf(studID) + "/" + searchAttributes;
                HttpUtil.getInstance().get(searchUrl, ConfigUtil.EVENT_SEARCH_FRIENDS);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    void getMatchingFriends(EventUtil eventUtil){
        if (eventUtil.getEventType() == ConfigUtil.EVENT_SEARCH_FRIENDS){
            String jsonResults = eventUtil.getResult();
            List<User> users = GsonUtil.getInstance().getUsers(jsonResults);
            if (users.isEmpty()){
                tvSearchHint.setText("There no friends matched, please choose another attributes to try.");
                tvSearchHint.setVisibility(View.VISIBLE);
            } else {
                tvSearchHint.setVisibility(View.GONE);
                App.setUsers(users);
                startActivity(new Intent(this, SearchResultsActivity.class));
                finish();
            }
        }
    }

    private void init() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Search Friends");

        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        Logger.addLogAdapter(new AndroidLogAdapter());

        tvSearchHint.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
