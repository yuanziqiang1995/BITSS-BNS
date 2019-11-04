package com.gbdpcloud.bayes.config;

import com.alibaba.fastjson.JSON;


/**
 * 统一API响应结果封装
 */
public class Result<T> {

    /*** 网关返回码*/
    private int code;
    /*** 网关返回码描述*/
    private String message;
    /*** 业务返回码（非必需）*/
    private String subCode;
    /*** 业务返回码描述*/
    private String subMessage;
    /*** 返回数据*/
    private T data;

    Result() {
        this(ResultCode.SUCCESS.code, ResultCode.SUCCESS.message());
    }

    Result(int code, String message) {
        this(code, message, null);
    }

    Result(ResultCode resultCode, T data) {
        this(resultCode.code, resultCode.message(), data);
    }

    Result(int code, String message, T data) {
        super();
        this.setCode(code).setMessage(message).setData(data);
    }

    Result(int code, String message, String subCode, String subMessage) {
        super();
        this.setCode(code).setMessage(message).setSubCode(subCode).setSubMessage(subMessage);
    }

    Result(ResultCode resultCode, ResultSubResultCode subResultCode) {
        super();
        this.setCode(resultCode.code).setMessage(resultCode.message()).setSubCode(subResultCode.subCode()).setSubMessage(subResultCode.subMessage());
    }

    Result(ResultCode resultCode, ResultSubResultCode subResultCode, T data) {
        super();
        this.setCode(resultCode.code).setMessage(resultCode.message()).setSubCode(subResultCode.subCode()).setSubMessage(subResultCode.subMessage()).setData(data);
    }

    Result(int code, String message, String subCode, String subMessage, T data) {
        super();
        this.setCode(code).setMessage(message).setSubCode(subCode).setSubMessage(subMessage).setData(data);
    }

    public static boolean isFailResult(Result result) {
        return result.code != 10000;
    }

    public Result setCode(int code) {
        this.code = code;
        return this;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Result<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getSubCode() {
        return subCode;
    }

    public Result setSubCode(String subCode) {
        this.subCode = subCode;
        return this;
    }

    public String getSubMessage() {
        return subMessage;
    }

    public Result<T> setSubMessage(String subMessage) {
        this.subMessage = subMessage;
        return this;
    }

    public T getData() {
        return data;
    }

    public Result setData(T data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
