package com.monash.app.adapter;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.monash.app.R;
import com.monash.app.bean.Friend;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abner on 2018/4/17.
 *
 */

public class FriendsAdapter extends BaseRecyclerViewAdapter<Friend> {

    private final List<Friend> originalList;

    public FriendsAdapter(List<Friend> list) {
        super(list);
        originalList = new ArrayList<>(list);
    }

    public FriendsAdapter(List<Friend> list, Context context) {
        super(list, context);
        originalList = new ArrayList<>(list);
    }

    @Override
    protected Animator[] getAnimators(View view) {
        if (view.getMeasuredHeight() <= 0) {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1.05f, 1.0f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1.05f, 1.0f);
            return new ObjectAnimator[]{scaleX, scaleY};
        }
        return new Animator[]{
                ObjectAnimator.ofFloat(view, "scaleX", 1.05f, 1.0f),
                ObjectAnimator.ofFloat(view, "scaleY", 1.05f, 1.0f),
        };
    }

    @Override
    public void setList(List<Friend> list) {
        super.setList(list);
        this.originalList.clear();
        originalList.addAll(list);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.friends_item_layout, parent, false);
        return new FriendsItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);

        FriendsItemViewHolder holder = (FriendsItemViewHolder) viewHolder;

        Friend friend = list.get(position);

        if (friend == null)
            return;
        holder.setNameText(friend.getFriend().getSurName() + " " + friend.getFriend().getFirstName());
        holder.setEmailText(friend.getFriend().getEmail());
        holder.setGenderText(friend.getFriend().getGender());
        holder.setCourseText(friend.getFriend().getCourse());
        holder.setFavMovieText(friend.getFriend().getFavMovie());
        holder.setNationalText(friend.getFriend().getNationality());
        // 为该位置的view绑定动画
        animate(holder, position);
    }
}