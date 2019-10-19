package com.sczp.common.aspect;

import com.sczp.common.constant.OperationType;

import java.lang.annotation.*;
/**
 * SystemLog
 * 标识需要记录日志的Controller方法, 方法参数自动通过@PathVariable, @RequestParam, @RequestBody, @RequestHeader来获取，
 自动生成操作日志。成功时将调用结果写入日志的content，
 * 失败时，则异常信息会写入content，如果定义了success或failure，则缺省的content有可能会被改写。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SystemLog {

    /**
     * 数据类型
     */
    String dataType();
    /**
     * 数据类型
     */
    String user() default "";

    /**
     * 操作类型
     */
    OperationType operationType() default OperationType.RETRIEVE;

    /**
     * 成功时进行的操作, 使用SpEL表达式
     * 可使用变量:
     * 参数：arg0, arg1, ...
     * 日志：log
     * 返回值：result
     */
    String[] success() default {};

    /**
     * 失败时进行的操作, 使用SpEL表达式
     * 可使用变量:
     * 参数：arg0, arg1, ...
     * 日志：log
     * 异常：exception
     */
    String[] failure() default {};

    /**
     * 需要纪录的参数，从0开始计数，如果不记录，则自动记录使用@RequestParam, @PathVariable, @RequestBody的参数
     */
    int[] parameters() default {};

    /**
     * 是否插入到数据库
     */
    boolean persist() default true;
}