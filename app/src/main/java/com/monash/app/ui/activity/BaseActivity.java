package com.monash.app.ui.activity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;

import com.monash.app.App;
import com.monash.app.R;
import com.monash.app.bean.User;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import org.litepal.LitePal;

/**
 * Created by abner on 2018/4/5.
 *
 */

public abstract class BaseActivity extends AppCompatActivity {

    public User user;
    public SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutView());
        user = App.getUser();
        db = LitePal.getDatabase();
    }

    protected abstract
    @LayoutRes
    int getLayoutView();

    protected void initTabLayout(TabLayout tabLayout) {
        if (tabLayout != null)
            tabLayout.setBackgroundColor(getColorPrimary());
    }

    public int getColorPrimary() {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }
}
