package com.casstime.net


import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/*
    Retrofit工厂，单例
 */
class RetrofitFactory private constructor() {

    /*
        单例实现
     */
    companion object {
        val instance: RetrofitFactory by lazy { RetrofitFactory() }
//        const val BASE_URL = CECNetworkInitHelper.getBaseUrl()
        const val TERMINAL_API = "terminal-api"
        const val TERMINAL_API_V2 = "terminal-api-v2"
        const val READ_TIME_OUT: Long = 20*1000                             //读取超时
        const val CONNECT_TIME_OUT: Long = 20*1000                           //连接超时
        const val CACHE_STALE_SEC = (10 * 1024 * 1024).toLong()  //缓存大小
    }

    private val retrofit: Retrofit

    //初始化
    init {
        //Retrofit实例化
        retrofit = initRetrofit(CECNetworkInitHelper.getBaseUrl())
    }

    /*
        日志拦截器
     */
    private fun initRetrofit(baseUrl: String): Retrofit {
       return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(CECOkHttpClient.instance)
                .build()
    }

    /*
        具体服务实例化
     */
    fun <T> create(service: Class<T>): T {
        return retrofit.create(service)
    }

    /**
     * 使用自定义的Url实例化服务
     */
    fun <T> create(baseUrl: String, service: Class<T>): T {
        return initRetrofit(baseUrl).create(service)
    }
}
