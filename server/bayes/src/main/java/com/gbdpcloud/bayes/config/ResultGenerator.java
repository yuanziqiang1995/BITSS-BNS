package com.gbdpcloud.bayes.config;

import org.springframework.util.StringUtils;

/**
 * 响应结果生成工具
 */
public class ResultGenerator {

    /**
     * default SuccessResult
     */
    public static Result genSuccessResult() {
        return new Result();
    }

    /**
     * SuccessResult with data
     */
    public static <T> Result<T> genSuccessResult(T data) {
        return new Result(ResultCode.SUCCESS, data);
    }

    /**
     * default FailResult
     */
    public static Result genFailResult() {
        return new Result(ResultCode.FAIL.code, ResultCode.FAIL.message());
    }

    /**
     * FailResult with message
     */
    public static Result genFailResult(String message) {
        return new Result(ResultCode.FAIL.code, message);
    }

    public static Result genFailResult(ResultCode resultCode) {
        return new Result(resultCode, resultCode.message());
    }

    public static Result genFailResult(ResultCode resultCode, ResultSubResultCode subResultCode) {
        return new Result(resultCode, subResultCode);
    }

    public static Result genFailResult(ResultCode resultCode, String message) {
        return new Result(resultCode.code, message);
    }

    public static <T> Result<T> genFailResult(ResultCode resultCode, ResultSubResultCode subResultCode, T data) {
        return new Result(resultCode, subResultCode, data);
    }

    public static Result ok(Object data) {
        return new Result(ResultCode.SUCCESS, data);
    }

    public static Result ok() {
        return new Result();
    }

    public static Result error(String message) {
        return error(message, null);
    }

    public static Result error(String message, Object data) {
        return new Result(ResultCode.FAIL.code, StringUtils.isEmpty(message) ? ResultCode.API_PROCESSING_FAILED.message() : message, data);
    }
}
