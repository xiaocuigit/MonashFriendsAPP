package com.monash.app.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.monash.app.R;

/**
 * Created by abner on 2018/4/17.
 *
 */

public class FriendsItemViewHolder extends RecyclerView.ViewHolder{

    private final TextView tvFriendName;
    private final TextView tvFriendEmail;
    private final TextView tvFriendCourse;
    private final TextView tvFriendNational;
    private final TextView tvFriendGender;
    private final TextView tvFriendFavMovie;

    FriendsItemViewHolder(View parent) {
        super(parent);
        tvFriendName = parent.findViewById(R.id.tv_friend_name);
        tvFriendEmail = parent.findViewById(R.id.tv_friend_email);
        tvFriendCourse = parent.findViewById(R.id.tv_friend_course);
        tvFriendGender = parent.findViewById(R.id.tv_friend_gender);
        tvFriendNational = parent.findViewById(R.id.tv_friend_nation);
        tvFriendFavMovie = parent.findViewById(R.id.tv_friend_favMovie);
    }
    void setNameText(CharSequence text){
        setTextView(tvFriendName, text);
    }

    void setEmailText(CharSequence text){
        setTextView(tvFriendEmail, text);
    }

    void setCourseText(CharSequence text){
        setTextView(tvFriendCourse, text);
    }

    void setGenderText(CharSequence text){
        setTextView(tvFriendGender, text);
    }

    void setNationalText(CharSequence text){
        setTextView(tvFriendNational, text);
    }

    void setFavMovieText(CharSequence text){
        setTextView(tvFriendFavMovie, text);
    }

    void setTextView(TextView view, CharSequence text) {
        // 判断view是否存在 以及 输入的内容是否为空
        if (view == null || TextUtils.isEmpty(text))
            return;
        view.setText(text);
    }
}