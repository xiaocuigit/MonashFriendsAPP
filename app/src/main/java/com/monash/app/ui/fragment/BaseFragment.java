package com.monash.app.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.monash.app.App;
import com.monash.app.bean.User;

/**
 * Created by abner on 2018/4/10.
 *
 */

public class BaseFragment extends Fragment {
    protected Activity mActivity;
    protected User user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        user = App.getUser();
    }

    @Override
    public void onDestroy() {
        mActivity = null;
        super.onDestroy();
    }
}
