package com.casstime.net

import android.app.Application

import okhttp3.Interceptor

/**
 * Created by maiwenchang at 2019-06-27 09:55
 * Description ：初始化网络模块
 */
object CTNetworkInitHelper {

    lateinit var application: Application
        private set

    lateinit var baseUrl: String
        private set

    var isIsProduction: Boolean = false
        private set

    /**
     * 初始化入口
     * @param interceptor 拦截器
     */
    fun initWithApplication(
        application: Application,
        baseUrl: String,
        isProduction: Boolean
    ): CTNetworkInitHelper.Builder {
        this.application = application
        this.baseUrl = baseUrl
        isIsProduction = isProduction
        return CTNetworkInitHelper.Builder
    }

    object Builder {

        var interceptors: Array<Interceptor> = emptyArray()

        var cacheStateSec: Long = (10 * 1024 * 1024).toLong()

        var readTimeOut: Long = 20 * 1000

        var connectTimeOut: Long = 20 * 1000

    }
}
