package com.monash.app.utils;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by abner on 2018/4/5.
 *
 */
public class HttpUtil {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static HttpUtil instance;
    private OkHttpClient client;

    private HttpUtil() {
        client = new OkHttpClient();
    }

    public static HttpUtil getInstance() {
        return instance == null ? instance = new HttpUtil() : instance;
    }


    /**
     * 提交GET请求
     *
     * @param url 地址
     * @return
     */
    public void get(final String url, final int eventType){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Request request = new Request.Builder().url(url).build();
                    Response response = client.newCall(request).execute();

                    if (response.isSuccessful()) {
                        EventBus.getDefault().post(new EventUtil(eventType, response.body().string(), response.code()));
                    } else {
                        Log.d("error", response.message());
                        Log.d("errorCode", String.valueOf(response.code()));
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 提交POST请求
     *
     * @param url     地址
     * @param content 请求体
     * @return
     * @throws IOException
     */
    public void post(final String url, final int eventType, final String content) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestBody body = RequestBody.create(JSON, content);
                Request.Builder builder = new Request.Builder().url(url);

                try {
                    Request request = builder.post(body).build();
                    Response response = client.newCall(request).execute();

                    EventBus.getDefault().post(new EventUtil(eventType, response.body().string(), response.code()));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void post(final String url, final String content) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestBody body = RequestBody.create(JSON, content);
                Request.Builder builder = new Request.Builder().url(url);

                try {
                    Request request = builder.post(body).build();
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        Log.d("success",response.body().string());
                    } else {
                        Log.d("error", response.message());
                        Log.d("errorCode", String.valueOf(response.code()));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

//
//    /**
//     * 提交put请求
//     * @param url
//     * @param content
//     * @return
//     */
//    public void put(final String url, final String content, final int eventType) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                RequestBody body = RequestBody.create(JSON, content);
//                Request.Builder builder = new Request.Builder().url(url);
//                Request request = builder.put(body).build();
//                try {
//                    Response response = client.newCall(request).execute();
//                    if (response.isSuccessful()) {
//                        EventBus.getDefault().post(new EventUtil(eventType, response.body().string()));
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }

//
//    /**
//     * 提交删除请求
//     * @param url
//     * @param content
//     * @return
//     */
//    public void delete(final String url, final String content, final int eventType){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                RequestBody body = RequestBody.create(JSON, content);
//                Request.Builder builder = new Request.Builder().url(url);
//                Request request = builder.delete(body).build();
//                try {
//                    Response response = client.newCall(request).execute();
//                    if (response.isSuccessful()) {
//                        EventBus.getDefault().post(new EventUtil(eventType, response.body().string()));
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }
}