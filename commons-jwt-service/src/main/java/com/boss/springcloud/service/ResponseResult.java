package com.boss.springcloud.service;

import com.boss.springcloud.util.ResponseCodeEnum;
import lombok.Data;

@Data
public class ResponseResult<T> {

    private int code;

    private String message;

    private T data;

    public ResponseResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResponseResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static ResponseResult success() {
        return new ResponseResult(ResponseCodeEnum.SUCCESS.getCode(), ResponseCodeEnum.SUCCESS.getMessage());
    }

    public static <T>ResponseResult<T> success(T data) {
        return new ResponseResult(ResponseCodeEnum.SUCCESS.getCode(), ResponseCodeEnum.SUCCESS.getMessage(), data);
    }

    public static ResponseResult error(int code, String message) {
        return new ResponseResult(code, message);
    }

    public static <T>ResponseResult<T> error(int code, String message, T data) {
        return new ResponseResult(code, message, data);
    }


}
