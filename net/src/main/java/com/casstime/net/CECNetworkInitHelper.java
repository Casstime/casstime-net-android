package com.casstime.net;

import android.app.Application;

import okhttp3.Interceptor;

/**
 * Created by maiwenchang at 2019-06-27 09:55
 * Description ：初始化网络模块
 */
public class CECNetworkInitHelper {

    private static Application mApplication;

    private static String mBaseUrl;

    private static boolean mIsProduction;

    /**
     * 初始化入口
     * @param interceptor 拦截器
     */
    public static void init(Application application,String baseUrl,boolean isProduction, Interceptor[] interceptor) {

        mApplication = application;
        mBaseUrl = baseUrl;
        mIsProduction = isProduction;
        CECOkHttpClient.Companion.init(interceptor);

    }


    public static Application getApplication() {
        return mApplication;
    }

    public static String getBaseUrl() {
        return mBaseUrl;
    }

    public static boolean isIsProduction() {
        return mIsProduction;
    }
}
