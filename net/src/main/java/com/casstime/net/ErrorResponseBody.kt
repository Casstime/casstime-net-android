package com.casstime.net

import java.io.Serializable

/**
 * Created by WenChang Mai on 2019/1/19 17:06.
 * Description: 网络请求错误时服务器返回的Response Body
 */
data class ErrorResponseBody(val statusCode: Int,
                             val error: String,
                             var message: String,
                             val errorCode: String,
                             val errState: String,
                             var errorMsg: String) : Serializable
