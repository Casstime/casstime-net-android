package com.casstime.net

import android.app.Application
import android.net.Uri

import okhttp3.Interceptor

/**
 * Created by maiwenchang at 2019-06-27 09:55
 * Description ：初始化网络模块
 */
object CTNetworkConfigInitHelper {

    /**
     * 初始化入口
     * @param interceptor 拦截器
     */
    fun initWithApplication(
        application: Application,
        baseUrl: String,
        isProduction: Boolean
    ): Config {
        val config = Config()
        config.application = application
        config.baseUrl = baseUrl
        config.isIsProduction = isProduction
        return config
    }

    class Config {

        lateinit var application: Application

        var baseUrl: String = Uri.EMPTY.toString()

        var isIsProduction: Boolean = false

        var convertFactories: Array<retrofit2.Converter.Factory> = emptyArray()

        var interceptors: Array<Interceptor> = emptyArray()

        var networkInterceptors: Array<Interceptor> = emptyArray()

        var cacheStateSec: Long = (10 * 1024 * 1024).toLong()

        var readTimeOut: Long = 20 * 1000

        var connectTimeOut: Long = 20 * 1000

    }
}
