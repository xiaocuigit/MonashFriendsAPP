package com.monash.app.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.monash.app.App;
import com.monash.app.R;
import com.monash.app.adapter.BaseRecyclerViewAdapter;
import com.monash.app.adapter.FriendsSearchedAdapter;
import com.monash.app.bean.Friend;
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

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchResultsActivity extends BaseActivity {

    private List<User> users;

    @BindView(R.id.recyclerView_friends) RecyclerView recyclerView;

    private FriendsSearchedAdapter recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
        initRecyclerView();
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_search_results;
    }

    private void init() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Matched Friends");
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        Logger.addLogAdapter(new AndroidLogAdapter());
        users = App.getUsers();
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);

        if (users != null)
            recyclerAdapter = new FriendsSearchedAdapter(users, this);

        recyclerAdapter.setOnInViewClickListener(R.id.friends_item_root,
                new BaseRecyclerViewAdapter.onInternalClickListenerImpl<User>(){
                    @Override
                    public void OnClickListener(View parentV, View v, Integer position, User values) {
                        super.OnClickListener(parentV, v, position, values);
                        showDetailInfo(values);
                    }
                });

        recyclerAdapter.setOnInViewClickListener(R.id.ib_friend_more,
                new BaseRecyclerViewAdapter.onInternalClickListenerImpl<User>(){
                    @Override
                    public void OnClickListener(View parentV, View v, Integer position, User values) {
                        super.OnClickListener(parentV, v, position, values);
                        showPopupMenu(v, values);
                    }
                });
        recyclerAdapter.setFirstOnly(false);
        recyclerAdapter.setDuration(300);
        recyclerView.setAdapter(recyclerAdapter);
    }

    private void showPopupMenu(View view, final User user) {
        PopupMenu popup = new PopupMenu(this, view);

        popup.getMenuInflater().inflate(R.menu.menu_friend_more, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.detail:
                        showDetailInfo(user);
                        break;
                    case R.id.addFriend:
                        addFriend(user);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    private void showDetailInfo(User user){
        Intent intent = new Intent(this, FriendInfoActivity.class);
        intent.putExtra("tag", "search");
        intent.putExtra("search", user);
        startActivity(intent);
    }

    private void addFriend(final User friend){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tip");
        builder.setMessage("Add this friend to your friends list???");
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        updateLists(friend);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        builder.setPositiveButton("OK", listener);
        builder.setNegativeButton("Cancel", listener);
        builder.show();
    }

    private void updateLists(User friend) {
        User user = App.getUser();
        if (user != null && friend != null) {
            JSONObject jsonObject = packageJSON(user, friend);
            try {
                // 向服务器请求函数该朋友
                HttpUtil.getInstance().post(ConfigUtil.POST_ADD_FRIEND, ConfigUtil.EVENT_ADD_FRIEND, jsonObject.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private JSONObject packageJSON(User user, User friend){
        JSONObject object = new JSONObject();
        try {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DATE);
            String startDate = String.format(getResources().getString(R.string.date_format),
                    String.valueOf(year), String.valueOf(month), String.valueOf(day));

            object.put("studID", user.getStudID());
            object.put("friendID", friend.getStudID());
            object.put("startDate", startDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    private String packageJSON(List<User> users) {
        try{
            JSONArray jsonArray = new JSONArray();
            for(User user:users){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("studID",user.getStudID());
                jsonArray.put(jsonObject);
            }
            return jsonArray.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    void showAddFriendInfo(EventUtil eventUtil){
        if (eventUtil.getEventType() == ConfigUtil.EVENT_ADD_FRIEND){
            String result = eventUtil.getResult();
            Logger.d(result);
        }
    }

    private void getResultLoc() throws IOException {
        String content;
        if(!App.getUsers().isEmpty()){
            if((content = packageJSON(App.getUsers())) != null){
                HttpUtil.getInstance().post(ConfigUtil.POST_SHOW_RESULT_STUDENT_IN_MAP,ConfigUtil.EVENT_GET_RESULT_LOCATION ,content);
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    void showResultLoc(EventUtil eventUtil){
        if(eventUtil.getEventType() == ConfigUtil.EVENT_GET_RESULT_LOCATION){
            try{
                JSONArray jsonArray = new JSONArray(eventUtil.getResult());
                Intent intent = new Intent(SearchResultsActivity.this,MapActivity.class);
                intent.putExtra("result",jsonArray.toString());
                startActivity(intent);
                Logger.d(eventUtil.getResult());
            }catch(Exception e){
                e.printStackTrace();
                Logger.d(e.getMessage());
            }
        }
    }

    private void showTipDialog(String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(text);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                EventBus.getDefault().unregister(this);
                finish();
                break;
            case R.id.action_settings:
                //在这里启动地图界面
                try {
                    getResultLoc();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_report, menu);
        return true;
    }

}
