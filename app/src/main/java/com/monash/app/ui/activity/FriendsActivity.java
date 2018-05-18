package com.monash.app.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.monash.app.App;
import com.monash.app.R;
import com.monash.app.adapter.BaseRecyclerViewAdapter;
import com.monash.app.adapter.FriendsAdapter;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FriendsActivity extends BaseActivity {

    @BindView(R.id.recyclerView_user_friends) RecyclerView recyclerView;

    @BindView(R.id.no_friends_tip) TextView noFriendsTip;

    private FriendsAdapter recyclerAdapter;
    private List<Friend> friends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
        initData();
        initRecyclerView();
    }

    private void initData() {
        friends = App.getFriends();
    }

    private void init() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Friends");
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        Logger.addLogAdapter(new AndroidLogAdapter());
    }

    private void deleteFriend(final Friend friend) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tip");
        builder.setMessage("Delete this friend forever!!!");
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

    private void updateLists(Friend friend) {
        User user = App.getUser();
        if (user != null && friend != null) {
            JSONObject jsonObject = packageJSON(user, friend);
            try {
                // 向服务器请求函数该朋友
                HttpUtil.getInstance().post(ConfigUtil.POST_DELETE_FRIEND, ConfigUtil.EVENT_DELETE_FRIEND, jsonObject.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        friends.remove(friend);
        App.setFriends(friends);
        recyclerAdapter.notifyDataSetChanged();
    }

    private JSONObject packageJSON(User user, Friend friend) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("studID", user.getStudID());
            jsonObject.put("friendID", friend.getFriend().getStudID());
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DATE);
            String endDate = String.format(getResources().getString(R.string.date_format),
                    String.valueOf(year), String.valueOf(month), String.valueOf(day));
            jsonObject.put("endDate", endDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);

        if (friends != null) {
            noFriendsTip.setVisibility(View.GONE);
            recyclerAdapter = new FriendsAdapter(friends, this);
            recyclerAdapter.setOnInViewClickListener(R.id.friends_item_root,
                    new BaseRecyclerViewAdapter.onInternalClickListenerImpl<Friend>(){
                        @Override
                        public void OnClickListener(View parentV, View v, Integer position, Friend values) {
                            super.OnClickListener(parentV, v, position, values);
                            showDetailInfo(values);
                        }
                    });
            recyclerAdapter.setOnInViewClickListener(R.id.ib_friend_more,
                    new BaseRecyclerViewAdapter.onInternalClickListenerImpl<Friend>(){
                        @Override
                        public void OnClickListener(View parentV, View v, Integer position, Friend values) {
                            super.OnClickListener(parentV, v, position, values);
                            showPopupMenu(v, values);
                        }
                    });
            recyclerAdapter.setFirstOnly(false);
            recyclerAdapter.setDuration(300);
            recyclerView.setAdapter(recyclerAdapter);
        } else {
            noFriendsTip.setVisibility(View.VISIBLE);
        }
    }

    private void showDetailInfo(Friend values) {
        Intent intent = new Intent(this, FriendInfoActivity.class);
        intent.putExtra("tag", "friends");
        intent.putExtra("friend", values);
        startActivity(intent);
    }

    private void showPopupMenu(View view, final Friend friend) {
        PopupMenu popup = new PopupMenu(this, view);

        popup.getMenuInflater().inflate(R.menu.menu_friend, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.delete:
                        deleteFriend(friend);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    void deleteFriendInfo(EventUtil eventUtil){
        if (eventUtil.getEventType() == ConfigUtil.EVENT_DELETE_FRIEND){
            Logger.d(eventUtil.getResult());
        }
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

    private void getFriendLoc() throws IOException {
        String content;
        if(!App.getFriends().isEmpty()){
            List<User> friends = new ArrayList<>();
            for(Friend temp:App.getFriends()){
                friends.add(temp.getFriend());
            }
            if((content = packageJSON(friends)) != null){
                HttpUtil.getInstance().post(ConfigUtil.POST_SHOW_RESULT_STUDENT_IN_MAP,ConfigUtil.EVENT_GET_FRIEND_LOCATION ,content);
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    void showFriendLoc(EventUtil eventUtil){
        if(eventUtil.getEventType() == ConfigUtil.EVENT_GET_FRIEND_LOCATION){
            try{
                JSONArray jsonArray = new JSONArray(eventUtil.getResult());
                Intent intent = new Intent(FriendsActivity.this,MapActivity.class);
                intent.putExtra("result",jsonArray.toString());
                startActivity(intent);
                Logger.d(eventUtil.getResult());
            }catch(Exception e){
                e.printStackTrace();
                Logger.d(e.getMessage());
            }
        }
    }


    @Override
    protected int getLayoutView() {
        return R.layout.activity_friends;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_settings:
                //在这里启动地图界面
                try {
                    getFriendLoc();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_report, menu);
        return true;
    }
}
