package com.casstime.net

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by WenChang Mai on 2019/2/13 9:53.
 * Description: 网络请求默认的线程调度
 */
class CTHttpTransformer<T> : ObservableTransformer<T, T> {


    override fun apply(upstream: Observable<T>): ObservableSource<T> {
        return upstream.subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()) //主线程订阅
    }
}
