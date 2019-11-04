package com.gbdpcloud.bayes.config;

/**
 * 业务具体返回码
 *
 * @author wchkong
 */

public enum ResultSubResultCode {

    /**
     * 服务暂不可用(业务系统不可用)
     */
    API_UNKNOW_ERROR("api-unknow-error", "服务暂不可用(业务系统不可用)"),
    /**
     * 服务暂不可用(网关自身的未知错误)
     */
    AOP_UNKNOW_ERROR("aop-unknow-error", "服务暂不可用(网关自身的未知错误)"),
    /**
     * 无效的访问令牌
     */
    INVALID_AUTH_TOKEN("invalid-auth-token", "无效的访问令牌，请刷新授权令牌或重新授权获取新的令牌"),
    /**
     * 访问令牌已过期
     */
    AUTH_TOKEN_TIME_OUT("auth-token-time-out", "访问令牌已过期，请刷新授权令牌或重新授权获取新的令牌"),
    /**
     * 无效的应用授权令牌
     */
    INVALID_APP_AUTH_TOKEN("invalid-app-auth-token", "无效的应用授权令牌"),
    /**
     * 应用授权令牌已过期
     */
    INVALID_APP_AUTH_TOKEN_TIME_OUT("invalid-app-auth-token-time-out", "应用授权令牌已过期"),
    /**
     * 参数无效
     */
    INVALID_PARAMETER("invalid-parameter", "参数无效"),
    /**
     * 缺少方法名参数
     */
    MISSING_METHOD("missing-method", "缺少方法名参数"),
    /**
     * 缺少时间戳参数
     */
    MISSING_TIMESTAMP("missing-timestamp", "缺少时间戳参数"),
    /**
     * 用户权限不足
     */
    INSUFFICIENT_USER_PERMISSIONS("insufficient-user-permissions", "用户权限不足");


    private String subCode;
    private String subMessage;

    ResultSubResultCode(String subCode, String subMessage) {
        this.subCode = subCode;
        this.subMessage = subMessage;
    }

    public String subCode() {
        return subCode;
    }

    public String subMessage() {
        return subMessage;
    }
}

