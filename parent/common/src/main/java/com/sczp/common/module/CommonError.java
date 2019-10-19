package com.sczp.common.module;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

//description = "通用API出错时，返回的错误信息数据")
@Getter
@Setter
@ToString
public class CommonError {
   //value = "错误信息", required = true)
    private String message;
   //value = "异常堆栈信息")
    private String exception;
   //value = "异常堆栈信息")
    private Integer code;

    public CommonError(String message) {
        this.message = message;
    }
    public CommonError(String message, String exception) {
        this.message = message;
        this.exception = exception;
    }
    public CommonError(String message, String exception, Integer code) {
        this.message = message;
        this.exception = exception;
        this.code = code;
    }

}