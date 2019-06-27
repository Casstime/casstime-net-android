package com.casstime.net

import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.net.Proxy
import java.util.concurrent.TimeUnit

/**
 * Created by maiwenchang at 2019/3/23 4:27 PM
 * Description ：Okhttp单例实现
 */
class CECOkHttpClient private constructor() {

    private object SingletonHolder {

        val holder: OkHttpClient = generateOkHttpClient(mInterceptors)

        private fun generateOkHttpClient(interceptors: Array<Interceptor>): OkHttpClient {
            val cacheFile = File(CECNetworkInitHelper.getApplication().cacheDir, "cache")
            val cache = Cache(cacheFile, RetrofitFactory.CACHE_STALE_SEC)
            val client = OkHttpClient.Builder()
                    .readTimeout(RetrofitFactory.READ_TIME_OUT, TimeUnit.MILLISECONDS)
                    .connectTimeout(RetrofitFactory.CONNECT_TIME_OUT, TimeUnit.MILLISECONDS)
                    .apply {
                        if (!interceptors.isEmpty()) {
                            for (interceptor in interceptors) {
                                addInterceptor(interceptor)
                            }
                        }
                    }
                    .apply {
                        val mLogInterceptor = HttpLoggingInterceptor()
                        if (CECNetworkInitHelper.isIsProduction() && !BuildConfig.DEBUG) { // 非生产环境模式关闭Log
                            mLogInterceptor.level = HttpLoggingInterceptor.Level.NONE
                        } else {
                            mLogInterceptor.level = HttpLoggingInterceptor.Level.BODY
                        }
                        addNetworkInterceptor(mLogInterceptor)
                    }
                    .apply {
                        //生产环境关闭抓包代理
                        if (CECNetworkInitHelper.isIsProduction() && !BuildConfig.DEBUG) {
                            proxy(Proxy.NO_PROXY)
                        }
                    }
                    .cookieJar(CECCookieJarManager.cookieJar)
                    .cache(cache)
                    .build()
//            LogUtil.d("createOkHttpClient")
            return client
        }
    }

    companion object {

        private var mInterceptors: Array<Interceptor> = emptyArray()

        fun init(interceptors: Array<Interceptor>) {
            mInterceptors = interceptors
        }

        val instance by lazy { SingletonHolder.holder }

    }
}
