package com.casstime.net;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by WenChang Mai on 2019/1/19 16:45.
 * Description: 线程调度过程的封装
 */
public class BaseObserver<E> implements Observer<BaseResponse<E>> {

    @Override
    public void onSubscribe(Disposable d) {
    }

    @Override
    public void onNext(BaseResponse<E> baseResponse) {
        if (baseResponse == null) {
            return;
        }
        if (baseResponse.getErrorCode() == 0) {
            onResponse(baseResponse, baseResponse.getData());
        } else {
            onFailure(baseResponse, baseResponse.getErrorCode(), baseResponse.getMessage());
        }
    }

    @Override
    public void onError(Throwable e) {
        // 拦截错误信息
        BaseResponse<E> baseResponse = new CECHttpErrorResponse<E>(e).getBaseResponse();
        onFailure(baseResponse, baseResponse.getErrorCode(), baseResponse.getMessage());
    }

    @Override
    public void onComplete() {
    }

    /**
     * 请求成功时回调
     *
     * @param baseResponse BaseResponse{errorCode: number;data: any;message: string;teamCode: number;}
     * @param data         数据实体
     */
    public void onResponse(@NonNull BaseResponse<E> baseResponse, @Nullable E data) {

    }

    /**
     * 请求失败时回调
     *
     * @param baseResponse BaseResponse{errorCode: number;data: any;message: string;teamCode: number;}
     * @param errorCode    错误代码
     * @param message      错误提示
     */
    public void onFailure(@NonNull BaseResponse<E> baseResponse, int errorCode, String message) {
        Log.e(getClass().getSimpleName(), "Http Error : " + message);
        if (TextUtils.isEmpty(message)) {
            return;
        }
        Toast.makeText(CECNetworkInitHelper.getApplication(), "", Toast.LENGTH_SHORT).show();
    }
}
