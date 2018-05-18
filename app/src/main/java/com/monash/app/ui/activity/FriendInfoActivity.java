package com.monash.app.ui.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.monash.app.R;
import com.monash.app.bean.Friend;
import com.monash.app.bean.MovieInfo;
import com.monash.app.bean.User;
import com.monash.app.utils.ConfigUtil;
import com.monash.app.utils.EventUtil;
import com.monash.app.utils.HttpUtil;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FriendInfoActivity extends BaseActivity {

    @BindView(R.id.tv_friend_info_gender) TextView tvGender;
    @BindView(R.id.tv_friend_info_name) TextView tvName;
    @BindView(R.id.tv_friend_info_email) TextView tvEmail;
    @BindView(R.id.tv_friend_info_studyMode) TextView tvStudyMode;
    @BindView(R.id.tv_friend_info_nation) TextView tvNation;
    @BindView(R.id.tv_friend_info_language) TextView tvLanguage;
    @BindView(R.id.tv_friend_info_course) TextView tvCourse;
    @BindView(R.id.tv_friend_info_address) TextView tvAddress;
    @BindView(R.id.tv_friend_info_suburb) TextView tvSuburb;
    @BindView(R.id.tv_friend_info_favUnit) TextView tvFavUnit;
    @BindView(R.id.tv_friend_info_favMovie) TextView tvFavMovie;
    @BindView(R.id.tv_friend_info_favSport) TextView tvFavSport;
    @BindView(R.id.tv_friend_info_startDate) TextView tvStartDate;
    @BindView(R.id.ll_friend_startDate) LinearLayout llFriendStartDate;

    private String movieName;
    private MovieInfo movieInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initData();
    }

    private void initData() {
        String tag = getIntent().getStringExtra("tag");
        if (tag.equals("search")){
            User user = (User) getIntent().getSerializableExtra("search");
            movieName = user.getFavMovie();
            showSearchedFriendInfo(user);
        } else {
            Friend friend = (Friend) getIntent().getSerializableExtra("friend");
            movieName = friend.getFriend().getFavMovie();
            showFriendInfo(friend);
        }
    }

    private void init() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Detail Information");

        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        Logger.addLogAdapter(new AndroidLogAdapter());
    }

    void showSearchedFriendInfo(User user){
        llFriendStartDate.setVisibility(View.GONE);
        tvName.setText(user.getSurName() + " " + user.getFirstName());
        tvGender.setText(user.getGender());
        tvEmail.setText(user.getEmail());
        tvStudyMode.setText(user.getStudyMode());
        tvNation.setText(user.getNationality());
        tvLanguage.setText(user.getLanguage());
        tvCourse.setText(user.getCourse());
        tvAddress.setText(user.getAddress());
        tvSuburb.setText(user.getSuburb());
        tvFavMovie.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        tvFavMovie.getPaint().setAntiAlias(true);//抗锯齿
        tvFavMovie.setText(user.getFavMovie());
        tvFavSport.setText(user.getFavSport());
        tvFavUnit.setText(user.getFavUnit());
    }

    void showFriendInfo(Friend friend){
        llFriendStartDate.setVisibility(View.VISIBLE);
        tvName.setText(friend.getFriend().getSurName() + " " + friend.getFriend().getFirstName());
        tvGender.setText(friend.getFriend().getGender());
        tvEmail.setText(friend.getFriend().getEmail());
        tvStudyMode.setText(friend.getFriend().getStudyMode());
        tvNation.setText(friend.getFriend().getNationality());
        tvLanguage.setText(friend.getFriend().getLanguage());
        tvCourse.setText(friend.getFriend().getCourse());
        tvAddress.setText(friend.getFriend().getAddress());
        tvSuburb.setText(friend.getFriend().getSuburb());
        tvFavMovie.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        tvFavMovie.getPaint().setAntiAlias(true);//抗锯齿
        tvFavMovie.setText(friend.getFriend().getFavMovie());
        tvFavSport.setText(friend.getFriend().getFavSport());
        tvFavUnit.setText(friend.getFriend().getFavUnit());
        tvStartDate.setText(friend.getStartDate());

    }

    @OnClick(R.id.tv_friend_info_favMovie)
    void getMovieInfo(){
        if (movieName != null) {
            HttpUtil.getInstance().get(ConfigUtil.GET_MOVIE_URL + movieName, ConfigUtil.EVENT_GET_MOVIE_INFO);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    void showMovieInfo(EventUtil eventUtil){
        if (eventUtil.getEventType() == ConfigUtil.EVENT_GET_MOVIE_INFO){
            movieInfo = new MovieInfo();
            parseJsonResult(eventUtil.getResult());
        }
    }

    private void parseJsonResult(String result) {
        JSONObject jsonObject = null;
        if (movieInfo != null){
            try {
                jsonObject = new JSONObject(result);
                JSONArray subjects = jsonObject.getJSONArray("subjects");
                JSONObject subject = subjects.getJSONObject(0);

                // 获取电影名称
                String title = subject.getString("title");
                movieInfo.setMovieName(title);
                // 获取电影平均评分
                JSONObject rating = subject.getJSONObject("rating");
                String score = rating.getString("average");
                movieInfo.setScore(score);
                // 获取电影演员姓名
                StringBuilder starSb = new StringBuilder();
                JSONArray casts = subject.getJSONArray("casts");
                for (int j = 0; j < casts.length() - 1; j++) {
                    JSONObject cast = casts.getJSONObject(j);
                    starSb.append(cast.getString("name")).append(",");
                }
                if (casts.length() > 0) {
                    starSb.append(casts.getJSONObject(casts.length() - 1).getString("name"));
                }
                movieInfo.setActors(starSb.toString());
                // 获取电影导演姓名
                StringBuilder directorsSb = new StringBuilder();
                JSONArray directors = subject.getJSONArray("directors");
                for (int k = 0; k < directors.length() - 1; k++) {
                    JSONObject director = directors.getJSONObject(k);
                    directorsSb.append(director.getString("name")).append(",");
                }
                if (directors.length() > 0) {
                    directorsSb.append(directors.getJSONObject(directors.length() - 1).getString("name"));
                }
                movieInfo.setDirectors(directorsSb.toString());
                // 获取电影出版年份
                String year = subject.getString("year");
                movieInfo.setYear(year);
                // 获取电影英文名称
                String originalName = subject.getString("original_title");
                movieInfo.setOriginalName(originalName);
                // 获取电影海报URL
                String imageURL = subject.getJSONObject("images").getString("small");
                movieInfo.setImageUrl(imageURL);
                // 获取电影ID
                int id = subject.getInt("id");
                // 获取电影简介
                HttpUtil.getInstance().get(ConfigUtil.GET_MOVIE_SUMMARY + String.valueOf(id), ConfigUtil.EVENT_GET_MOVIE_SUMMARY);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    void getMovieSummary(EventUtil eventUtil){
        if (eventUtil.getEventType() == ConfigUtil.EVENT_GET_MOVIE_SUMMARY){
            JSONObject summaryJson = null;
            try {
                summaryJson = new JSONObject(eventUtil.getResult());
                String summary = summaryJson.getString("summary");
                movieInfo.setSummary(summary);
                // 表示电影的所有信息都已经获得完毕
                Intent intent = new Intent(this, MovieInfoActivity.class);
                intent.putExtra("movie", movieInfo);
                startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_friend_info;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                EventBus.getDefault().unregister(this);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
