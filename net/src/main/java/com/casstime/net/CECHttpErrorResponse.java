package com.casstime.net;

import android.text.TextUtils;


/**
 * Created by maiwenchang at 2019-06-06 16:06
 * Description ：
 */
public class CECHttpErrorResponse<E> {

    private BaseResponse<E> baseResponse;

    private Throwable e;

    public CECHttpErrorResponse(Throwable e) {
        this.e = e;
    }

    public BaseResponse<E> getBaseResponse() {
        if (baseResponse != null) {
            return baseResponse;
        }
        if (e instanceof NoNetworkException) {
            String noNetworkMessage = "网络不可用，请检查网络设置";
            baseResponse = new BaseResponse<>(-1, noNetworkMessage, null, 1000);
            return baseResponse;
        }
        ErrorResponseBody responseBody = HttpErrorHandler.handle(e);
        String unknownMessage = "服务器异常";
        if (responseBody == null) {
            baseResponse = new BaseResponse<>(-1, unknownMessage, null, 1000);
            return baseResponse;
        }
        // 服务器有返回错误信息
        String responseErrorMessage;
        if (!TextUtils.isEmpty(responseBody.getErrorMsg())) {
            responseErrorMessage = responseBody.getErrorMsg();
        } else if (!TextUtils.isEmpty(responseBody.getError())) {
            responseErrorMessage = responseBody.getError();
        } else if (!TextUtils.isEmpty(responseBody.getMessage())) {
            responseErrorMessage = responseBody.getMessage();
        } else {
            responseErrorMessage = unknownMessage;
        }
        baseResponse = new BaseResponse<>(responseBody.getStatusCode(), responseErrorMessage, null, 1000);
        return baseResponse;
    }
}
