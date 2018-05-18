package com.monash.app.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.monash.app.R;
import com.monash.app.bean.LocationVisited;
import com.monash.app.utils.ConfigUtil;
import com.monash.app.utils.EventUtil;
import com.monash.app.utils.HttpUtil;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by abner on 2018/4/10.
 *
 */

public class LocationFragment extends BaseFragment{

    @BindView(R.id.bc_location_report) BarChart bcLocationReport;
    @BindView(R.id.ll_set_date) LinearLayout llSetDate;
    @BindView(R.id.tv_start_date) TextView tvStartDate;
    @BindView(R.id.tv_end_date) TextView tvEndDate;

    private static String GET_START_DATE = "GET_START_DATE";
    private static String GET_END_DATE = "GET_END_DATE";

    private String startDate;
    private String endDate;

    private CalendarDatePickerDialogFragment.OnDateSetListener listener;

    public static LocationFragment newInstance(){
        return new LocationFragment();
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
        View view = inflater.inflate(R.layout.fragment_locations_report, container, false);
        ButterKnife.bind(this, view);
        initDateDialog();
        return view;
    }

    private void initDateDialog() {
         listener = new CalendarDatePickerDialogFragment.OnDateSetListener() {
            @Override
            public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                Date date = calendar.getTime();
                String tag = dialog.getTag();
                if (tag.equals(GET_START_DATE)) {
                    tvStartDate.setText(handleDate(date));
                    startDate = handleDateString(year, monthOfYear + 1, dayOfMonth);
                    Logger.d(startDate);
                } else {
                    tvEndDate.setText(handleDate(date));
                    endDate = handleDateString(year, monthOfYear + 1, dayOfMonth);
                    Logger.d(endDate);
                }
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @OnClick(R.id.btn_ok)
    void getLocationReport(){
        if (TextUtils.isEmpty(startDate)){
            Snackbar.make(getView(), "Please set start date", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else if (TextUtils.isEmpty(endDate)) {
            Snackbar.make(getView(), "Please set end date", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {
            if (user != null) {
                String url = ConfigUtil.GET_LOCATION_REPORT + user.getStudID() + "/" + startDate + "/" + endDate;
                HttpUtil.getInstance().get(url, ConfigUtil.EVENT_GET_LOCATION_REPORT);
            }
            llSetDate.setVisibility(View.GONE);
            bcLocationReport.setVisibility(View.VISIBLE);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    void getLocationFromService(EventUtil eventUtil){
        if (eventUtil.getEventType() == ConfigUtil.EVENT_GET_LOCATION_REPORT){
            Logger.d(eventUtil.getResult());
            Gson gson = new Gson();
            List<LocationVisited> locationVisiteds = gson.fromJson(eventUtil.getResult(), new TypeToken<List<LocationVisited>>()
            {}.getType());

            ArrayList<BarEntry> entries = new ArrayList<>();    //显示条目
            ArrayList<String> labels = new ArrayList<>();

            float position = 0;
            for (LocationVisited location : locationVisiteds){
                Logger.d(location.getPlace() + " : " + location.getFrequency());
                labels.add(location.getPlace());
                Integer frequency = Integer.decode(location.getFrequency());
                entries.add(new BarEntry(position, (float)frequency));
                position += 1;
            }
            BarDataSet dataSet = new BarDataSet(entries, "frequency");
            dataSet.setHighlightEnabled(true);
            dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            BarData barData = new BarData(dataSet);
            bcLocationReport.setData(barData);

            bcLocationReport.getLegend().setForm(Legend.LegendForm.CIRCLE);

            bcLocationReport.getXAxis().setDrawGridLines(true);
            bcLocationReport.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            bcLocationReport.getXAxis().setAxisLineWidth(1.0f);
            bcLocationReport.getXAxis().setEnabled(false);

            bcLocationReport.getAxisRight().setEnabled(false);

            bcLocationReport.getAxisLeft().setAxisMinimum(0.0f);
            bcLocationReport.getAxisLeft().setDrawGridLines(false);
            bcLocationReport.getAxisLeft().setAxisMaximum(4.0f);

            bcLocationReport.setFitBars(true);
            bcLocationReport.animateY(1000);
            bcLocationReport.invalidate();
        }
    }

    @OnClick(R.id.tv_start_date)
    void getStartDate(){
        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setOnDateSetListener(listener)
                .setDoneText("OK")
                .setCancelText("Cancel");
        cdp.show(getChildFragmentManager(), GET_START_DATE);
    }

    @OnClick(R.id.tv_end_date)
    void getEndDate(){

        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setOnDateSetListener(listener)
                .setDoneText("OK")
                .setCancelText("Cancel");
        cdp.show(getChildFragmentManager(), GET_END_DATE);
    }

    private String handleDate(Date date){
        if (date != null)
            return DateFormat.getDateInstance(DateFormat.MEDIUM).format(date);
        else
            return null;
    }

    private String handleDateString(int year, int month, int day){
        String date;
        if (month < 10){
            date = String.format(getResources().getString(R.string.date_format),
                    String.valueOf(year), "0" + String.valueOf(month), String.valueOf(day));
        } else if (day < 10){
            date = String.format(getResources().getString(R.string.date_format),
                    String.valueOf(year), String.valueOf(month), "0" + String.valueOf(day));
        } else {
            date = String.format(getResources().getString(R.string.date_format),
                    String.valueOf(year), String.valueOf(month), String.valueOf(day));
        }
        return date;
    }
}
