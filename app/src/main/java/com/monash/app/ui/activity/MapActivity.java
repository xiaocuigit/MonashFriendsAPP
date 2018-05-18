package com.monash.app.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.monash.app.App;
import com.monash.app.R;
import com.monash.app.adapter.InfoWindowAdapter;
import com.monash.app.bean.SearchResult;
import com.monash.app.bean.User;
import com.monash.app.utils.ConfigUtil;
import com.monash.app.utils.HttpUtil;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class MapActivity extends CheckPermissionsActivity implements AMapLocationListener {

    private AMap mMap = null;
    private MapView mMapView;
    private AMap.InfoWindowAdapter adapter;

    private boolean isFirst = true;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = this;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //    askForPermission();
        //      setUpMapIfNeeded();
        initView();
        mMapView.onCreate(savedInstanceState);
        initOperation();

    }

    void showResultStuLoc() throws JSONException {
        Intent intent = getIntent();
        String content = intent.getStringExtra("result");
        Gson gson = new Gson();
        List<SearchResult> searchResults = gson.fromJson(content, new TypeToken<List<SearchResult> >(){}.getType());
        for(SearchResult result:searchResults) {
            User user = result.getUser();
            double longitude = result.getLongitude();
            double latitude = result.getLatitude();
            int studID = user.getStudID();

            //show marker
            LatLng latLng = new LatLng(latitude,longitude);
            String title = "StudID: " + String.valueOf(studID);
            String stuName = user.getSurName()+user.getFirstName();
            String male = user.getGender();
            String favMovie = user.getFavMovie();
            String favSport = user.getFavSport();
            String favUnit = user.getFavUnit();

            String snippet = "stuName: " + stuName + "\n" +
                             "gender: " + male + "\n" +
                             "favMovie: " + favMovie + "\n" +
                             "favSport: " + favSport + "\n" +
                             "favUnit: " + favUnit;
            Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(title).snippet(snippet).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
        }
    }

    private void initView(){
        mMapView = (MapView) findViewById(R.id.map);
    }

    private void initOperation(){
        if(mMap == null){
            mMap = mMapView.getMap();
        }
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLo5cationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.mylocation));// 设置小蓝点的图标
        myLocationStyle.strokeColor(getResources().getColor(R.color.blue));// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(100, 29, 161, 242));// 设置圆形的填充颜色
        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
        myLocationStyle.interval(1000000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        mMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        //aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
        mMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        mMap.moveCamera(CameraUpdateFactory.zoomTo(17));
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE) ;//定位一次，且将视角移动到地图中心点
        try {
            showResultStuLoc();
        } catch (Exception e) {
            e.printStackTrace();
        }

        adapter = new InfoWindowAdapter();
        mMap.setInfoWindowAdapter(adapter);
        Logger.addLogAdapter(new AndroidLogAdapter());
        initLocation();
    }

    public void initLocation(){
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(false);

        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //关闭缓存机制
        mLocationOption.setLocationCacheEnable(false);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    public static String getNowDate(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

    public static String getNowTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }
    public void onLocationChanged(AMapLocation aLocation) {

        if (aLocation != null) {
            String currentLocation = aLocation.getCountry() + aLocation.getProvince() + aLocation.getCity() + aLocation.getDistrict();
            String latitude = String.valueOf(aLocation.getLatitude());
            String longitude = String.valueOf(aLocation.getLongitude());
            int studID = App.getUser().getStudID();
            String date = getNowDate();
            String time = getNowTime();
            if (isFirst) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("studID",studID);
                    jsonObject.put("currentLocation",currentLocation);
                    jsonObject.put("latitude",latitude);
                    jsonObject.put("longitude",longitude);
                    jsonObject.put("date",date);
                    jsonObject.put("time",time);
                    try {
                        HttpUtil.getInstance().post(ConfigUtil.POST_USER_LOCATION, jsonObject.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                isFirst = false;
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mMapView.onDestroy();
    }



    /*
    //获取身边的人
    private void getVincinity(double longitude,double latitude) throws IOException {
        int studID = 29184501;
        double longitude = 145.029449;
        double latitude = -37.883842;
        int studID = App.getUser().getStudID();
    }
    */
}
