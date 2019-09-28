package com.sczp.system.util.module;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ApiModel(description = "通用API出错时，返回的错误信息数据")
@Getter
@Setter
@ToString
public class CommonError {
    @ApiModelProperty(value = "错误信息", required = true)
    private String message;
    @ApiModelProperty(value = "异常堆栈信息")
    private String exception;
    @ApiModelProperty(value = "异常堆栈信息")
    private Integer code;

    public CommonError(String message) {
        this.message = message;
    }
    public CommonError(String message, String exception) {
        this.message = message;
        this.exception = exception;
    }
    public CommonError(String message, String exception,Integer code) {
        this.message = message;
        this.exception = exception;
        this.code = code;
    }

}