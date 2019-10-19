package com.sczp.common.module;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 接口请求日志记录VO
 * @author shixh
 *
 */
@Data
public class SysOperateLogVo implements Serializable{
	
	private static final long serialVersionUID = 886985461115276855L;
	//"日志记录表编号")
	protected Long id;
	
	//"操作功能,参照OperateConstant")
	private String operateName;  
	
	//"操作具体业务")
	private String operateBusiness;
	
	//"操作内容(用来比较修改之前和修改之后)")
	private String operateContent;
	
	//"访问路径")	
	private String url;     
	
	//"用户IP")	
	private String ip;       
	
	//"用户编号")
	private Long userId;    
	
	//"用户姓名")
	private String userName;

	//"返回结果")
	private String operateResult;   
	
	//"用户操作时间")
	private Date operateDate;
	
	//"请求响应时间 ")
	private long time;           


}
