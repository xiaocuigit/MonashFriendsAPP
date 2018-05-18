package com.monash.app.adapter;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.monash.app.R;
import com.monash.app.bean.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abner on 2018/4/14.
 *
 */

public class FriendsSearchedAdapter extends BaseRecyclerViewAdapter<User> {

    private final List<User> originalList;

    public FriendsSearchedAdapter(List<User> list) {
        super(list);
        originalList = new ArrayList<>(list);
    }

    public FriendsSearchedAdapter(List<User> list, Context context) {
        super(list, context);
        originalList = new ArrayList<>(list);
    }

    @Override
    protected Animator[] getAnimators(View view) {
        if (view.getMeasuredHeight() <=0){
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
    public void setList(List<User> list) {
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

        User friend = list.get(position);

        if (friend == null)
            return;
        holder.setNameText(friend.getSurName() + " " + friend.getFirstName());
        holder.setEmailText(friend.getEmail());
        holder.setGenderText(friend.getGender());
        holder.setCourseText(friend.getCourse());
        holder.setFavMovieText(friend.getFavMovie());
        holder.setNationalText(friend.getNationality());
        // 为该位置的view绑定动画
        animate(holder, position);
    }
}
