package com.casstime.net;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.reactivex.disposables.Disposable;

/**
 * Created by maiwenchang at 2019/3/23 5:42 PM
 * Description ：BaseObserver的代理类
 */
public class CECObserverProxy<E> extends BaseObserver<E> {

    private BaseObserver<E> mObserver;

    public CECObserverProxy(BaseObserver<E> observer) {
        this.mObserver = observer;
    }

    @Override
    public void onSubscribe(Disposable d) {
        if (mObserver == null) {
            return;
        }
        mObserver.onSubscribe(d);
    }

    @Override
    public void onNext(BaseResponse<E> baseResponse) {
        super.onNext(baseResponse);
        if (mObserver == null) {
            return;
        }
        mObserver.onNext(baseResponse);
    }

    @Override
    public void onError(Throwable e) {
        if (mObserver == null) {
            return;
        }
        mObserver.onError(e);
    }

    @Override
    public void onComplete() {
        if (mObserver == null) {
            return;
        }
        mObserver.onComplete();
    }

    @Override
    public void onResponse(@NonNull BaseResponse<E> baseResponse, @Nullable E data) {

    }

    @Override
    public void onFailure(@NonNull BaseResponse<E> baseResponse, int errorCode, String message) {

    }
}
