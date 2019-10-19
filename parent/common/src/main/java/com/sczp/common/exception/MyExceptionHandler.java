package com.sczp.common.exception;

import com.sczp.common.module.CommonError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@ResponseBody
public class MyExceptionHandler {
    
    /**
    * 自定义方法调用失败异常
    * @param exception 异常信息
    * @return 转换后错误对象
    */
   @ExceptionHandler(DataNotFoundException.class)
   @ResponseStatus(HttpStatus.BAD_REQUEST)
   public CommonError handle(DataNotFoundException exception) {
       return new CommonError(exception.getMessage(),exception.getCause()==null?" ":exception.getCause().toString(),400);
   }
}