package com.gbdpcloud.bayes.config;
public enum ResultCode {


    /*** 接口调用成功*/
    SUCCESS(10000, "接口调用成功,调用结果请参考具体的业务返回参数"),
    /*** 服务不可用*/
    SERVICE_UNAVAILABLE(20000, "服务不可用"),
    /*** 授权权限不足（token相关）*/
    INSUFFICIENT_AUTHORIZATION(20001, "授权权限不足"),
    /*** 接口不存在 */
    NOT_FOUND(20002, "接口不存在"),
    /*** 失败 */
    FAIL(40000, "失败"),
    /*** 缺少必选参数*/
    MISSING_PARAMETERS(40001, "缺少必选参数"),
    /*** 非法的参数*/
    ILLEGAL_PARAMETER(40002, "非法的参数"),
    /*** 业务处理失败*/
    API_PROCESSING_FAILED(40004, "业务处理失败"),
    /*** 未认证（签名错误） */
    UNAUTHORIZED(40005, "签名错误"),
    /*** 权限不足*/
    INSUFFICIENT_PERMISSIONS(40006, "权限不足"),
    /*** 用户名不存在或者密码错误*/
    USERNAME_OR_PASSWORD_ERROR(40007, "用户名不存在或者密码错误");

    //SUCCESS(200),//成功
    //
    //INTERNAL_SERVER_ERROR(500);//服务器内部错误

    protected int code;
    private String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }
}
