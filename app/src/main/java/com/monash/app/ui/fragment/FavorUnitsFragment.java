package com.monash.app.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.monash.app.R;
import com.monash.app.bean.FavorUnits;
import com.monash.app.utils.ConfigUtil;
import com.monash.app.utils.EventUtil;
import com.monash.app.utils.HttpUtil;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by abner on 2018/4/10.
 *
 */

public class FavorUnitsFragment extends BaseFragment {

    @BindView(R.id.tv_title_units) TextView unitsTitle;

    @BindView(R.id.pie_show_favUnits) PieChart pieChart;

    public static FavorUnitsFragment newInstance(){
        return new FavorUnitsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        Logger.addLogAdapter(new AndroidLogAdapter());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_units_report, container, false);
        ButterKnife.bind(this, view);
        // 向服务器请求关于units的数据
        HttpUtil.getInstance().get(ConfigUtil.GET_UNITS_REPORTS, ConfigUtil.EVENT_GET_UNITS_REPORT);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    void getUnitsFromService(EventUtil eventUtil){
        if (eventUtil.getEventType() == ConfigUtil.EVENT_GET_UNITS_REPORT){
            Logger.d(eventUtil.getResult());
            Gson gson = new Gson();
            List<FavorUnits> favorUnits = gson.fromJson(eventUtil.getResult(), new TypeToken<List<FavorUnits>>()
            {}.getType());

            List<PieEntry> entries = new ArrayList<>();

            for (FavorUnits unit : favorUnits){
                Integer y = Integer.decode(unit.getFrequency());
                entries.add(new PieEntry((float)y, unit.getFavUnit()));
            }
            PieDataSet dataSet = new PieDataSet(entries, "favor units report");
            dataSet.setSliceSpace(3f);
            dataSet.setDrawValues(false);
            dataSet.setColors(ColorTemplate.PASTEL_COLORS);
            PieData data = new PieData(dataSet);
            data.setDrawValues(true);

            pieChart.setData(data);
//            pieChart.setUsePercentValues(true);
            pieChart.setCenterText("Favor Units");
            pieChart.setClickable(true);
            pieChart.setDrawEntryLabels(true);
            pieChart.animateY(1000);
            pieChart.invalidate();
        }
    }
}
