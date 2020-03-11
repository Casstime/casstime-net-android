package com.casstime.net

import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import java.lang.IllegalArgumentException


/**
 * Created by maiwenchang at 2019/3/23 4:27 PM
 * Description ：CookieJar管理类，负责CookieJar的创建和清除
 */
class CTCookieJarManager private constructor() {

    private object SingletonHolder {
        val holder: PersistentCookieJar by lazy {
            val config = (CTOkHttpClient.config
                ?: throw IllegalArgumentException("CTOkHttpClient.Companion.init() never been called"))
            PersistentCookieJar(
                SetCookieCache(),
                SharedPrefsCookiePersistor(config.application)
            )
        }
    }

    companion object {
        val cookieJar = SingletonHolder.holder
        fun clear() {
            cookieJar.clear()
        }
    }


}
