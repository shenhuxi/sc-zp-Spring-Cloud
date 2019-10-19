package com.sczp.common.aspect;

import com.alibaba.fastjson.JSON;
import com.sczp.common.constant.OperateConstant;
import com.sczp.common.module.ResultObject;
import com.sczp.common.module.SysOperateLogVo;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zengpeng
 * @date 2019/6/26
 */
@Order(5)
@Aspect
@Component
@Slf4j
public class SystemLogAspect {
    private long startTime = 0;
    private ThreadLocal<SysOperateLogVo> operateLogThreadLocal = new ThreadLocal<>();

    @Pointcut("@annotation(com.sczp.common.aspect.SystemLog)")
    public  void SystemLogAspect(){
    }

    @Before("@annotation(systemLog)")
    public void doBefore(JoinPoint joinPoint, SystemLog systemLog) throws Exception{
        startTime = System.currentTimeMillis();
        if(joinPoint.getArgs().length>0) {
            log.info("日志：接口参数Args: {}", joinPoint.getArgs()[0] != null ? joinPoint.getArgs()[0].toString() : "");
        }
        SysOperateLogVo operateLog = new SysOperateLogVo();
        log.info("日志：接口操作userOperate: {}",systemLog.dataType());
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
       /* SysUserVo user = getLoginUser(request);
        operateLog.setOperateName(userOperate.name());
        operateLog.setUserId(user.getId());
        operateLog.setUserName(user.getUserName());
        operateLog.setIp(ClientUtil.getRequestIp(request));
        operateLog.setUrl(request.getRequestURL().toString());
        operateLog.setOperateBusiness(userOperate.business());*/
        operateLogThreadLocal.set(operateLog);
    }


    @AfterReturning(pointcut = "@annotation(userOperate)",returning="returnValue")
    public void SystemLog(Object returnValue, SystemLog userOperate){
        SysOperateLogVo operateLog = operateLogThreadLocal.get();
        long endTime = System.currentTimeMillis();
        //请求失败记录失败日志
        if(returnValue instanceof ResultObject) {
            ResultObject resultObject = (ResultObject)returnValue;
            if(!ResultObject.OK.equals(resultObject.getCode()) || OperateConstant.DELETE.equals(userOperate.dataType())) {
                operateLog.setOperateResult(JSON.toJSONString(returnValue));
            }
        }
        operateLog.setTime(endTime-startTime);
        try {
            log.info("日志：接口执行结果：operateLog: {}",operateLog);
//    		executorService.submit(new SysOperateLogThread(operateLog));//使用异步线程去发送
        } catch(Exception e) {
            log.error("Rabbit消息发送失败:    " + e.getMessage());
        } finally {
            operateLogThreadLocal.remove();
        }
        operateLogThreadLocal.remove();
    }

}