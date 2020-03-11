package com.casstime.net


import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.IllegalArgumentException

/**
 *  Retrofit工厂，实例构造
 *
 */
class CTRetrofitFactory private constructor() {

    /*
        单例实现
     */
    companion object {

        /**
         * 全局单例模式
         */
        val instance: CTRetrofitFactory by lazy { CTRetrofitFactory() }

        /**
         * 构建新实例
         */
        fun newInstance(baseUrl: String, okHttpClient: OkHttpClient): Retrofit {
            return initRetrofit(baseUrl, okHttpClient)
        }

        /**
         *  初始化方法
         */
        private fun initRetrofit(baseUrl: String, okHttpClient: OkHttpClient): Retrofit {
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .apply {
                    CTOkHttpClient.config?.let {
                        for (factory in it.convertFactories) {
                            addConverterFactory(factory)
                        }
                    }
                }
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build()
        }
    }

    private val retrofit: Retrofit

    //初始化
    init {
        //Retrofit实例化
        val config = CTOkHttpClient.config
            ?: throw IllegalArgumentException("CTOkHttpClient.Companion.init() never been called or the config is null")
        retrofit = initRetrofit(config.baseUrl, CTOkHttpClient.instance)
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
        return initRetrofit(baseUrl, CTOkHttpClient.instance).create(service)
    }
}
