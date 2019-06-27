package com.casstime.net

import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor


/**
 * Created by maiwenchang at 2019/3/23 4:27 PM
 * Description ：CookieJar管理类，负责CookieJar的创建和清除
 */
class CECCookieJarManager private constructor() {

    private object SingletonHolder {
        val holder: PersistentCookieJar = PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(CECNetworkInitHelper.getApplication()))
    }

    companion object {
        val cookieJar = SingletonHolder.holder
        fun clear(){
            cookieJar.clear()
        }
    }


}
