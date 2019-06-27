package com.casstime.net

import java.io.Serializable

/**
 * Created by WenChang Mai on 2019/3/11 15:57.
 * Description: 统一数据实体封装
 * 所有的接口会包装成如下格式，为下面接口文档描述的 Response Model:
 *
 * {
 *     errorCode: number;
 *     data: any;
 *     message: string;
 *     teamCode: number;
 * }
 * 当errorCode为0 时，表示处理成功，否则请求失败，
 * 失败时的errorCode为TeamCode和ErrorType相加，默认1000，
 * message为出错时，给前端用户的提示信息
 * TeamCode
 * {
 *     TerminalTeam = 1000; // 终端组
 *     InquiryTeam = 2000; // 询价组
 *     OrderTeam = 3000; // 订单组
 *     FinancialTeam = 4000; // 结算组
 *     ProductTeam = 5000;  // 商品组
 *     MemberTeam = 6000;  // 会员组
 *     MainDataTeam = 7000; // 主数据组
 * }
 *
 * ErrorType
 * {
 *     SUCCESS = 0; // 请求成功
 *     ValidateError = 1; // 权限校验
 *     UNSUPPORTED_BRAND = 2;  // 不支持该VIN码对应的品牌
 *     Forbidden = 403; // 无权限访问
 *     NotFound = 404; // 资源不存在
 *     ServerError = 500, // 服务异常
 *     FrequencyLimit = 501; // 频率限制
 *     Timeout = 502; // 超时
 * }
 *
 */
data class BaseResponse<out T>(val errorCode: Int, val message: String, val data: T, val teamCode: Int) : Serializable