package com.shui.kill.api.response;

import com.shui.kill.api.enums.StatusCode;
import lombok.Data;

/**
 *  Result
 */
@Data
public class BaseResponse<T> {

    // 状态码
    private Integer code;
    // 消息
    private String msg;
    // 返回前端的数据
    private T data;

    public BaseResponse(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public BaseResponse(StatusCode statusCode) {
        this.code = statusCode.getCode();
        this.msg = statusCode.getMsg();
    }

    public BaseResponse(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

}
