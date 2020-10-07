package com.shui.kill.api.response;

import com.shui.kill.api.enums.StatusCode;
import lombok.Data;

/**
 *  Result
 */
@Data
public class BaseResponse<T> {

    private Integer code;   // 状态码
    private String msg;     // 消息
    private T data;         // 返回前端的数据

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
