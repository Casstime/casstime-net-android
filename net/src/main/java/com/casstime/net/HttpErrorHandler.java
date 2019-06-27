package com.casstime.net;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.HttpException;

/**
 * Created by WenChang Mai on 2019/1/19 17:15.
 * Description:
 */
public class HttpErrorHandler {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    public static ErrorResponseBody handle(Throwable throwable) {
        if (throwable instanceof HttpException) { // 非200
            HttpException error = (HttpException) throwable;
            try {
                ResponseBody body = error.response().errorBody();
                if (body != null) {
                    String responseBody = getBodyString(body);
                    return new Gson().fromJson(responseBody, ErrorResponseBody.class);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else { //网络异常
            throwable.printStackTrace();
        }
        return null;
    }

    /**
     * 获取ResponseBody 字符串
     *
     * @param responseBody ResponseBody
     * @return String
     * @throws IOException
     */
    private static String getBodyString(ResponseBody responseBody) throws IOException {
        if (responseBody == null) return "";
        BufferedSource source = responseBody.source();
        source.request(Long.MAX_VALUE); // Buffer the entire body.
        Buffer buffer = source.buffer();

        Charset charset = UTF8;
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            charset = contentType.charset(UTF8);
        }

        if (charset == null) {
            charset = UTF8;
        }

        return buffer.clone().readString(charset);

    }
}
