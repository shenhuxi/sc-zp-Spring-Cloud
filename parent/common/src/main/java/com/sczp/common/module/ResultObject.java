/**
 * Project Name:UnionApp
 * File Name:ResultObject.java
 * Package Name:com.unionapp.common
 * Date:2017年12月15日上午10:06:55
 * Copyright (c) 2017, chenzhou1025@126.com All Rights Reserved.
 */

package com.sczp.common.module;

/**
 * ClassName:ResultObject <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2017年12月15日 上午10:06:55 <br/>
 *
 * @author javan
 * @version
 * @see
 */
public class ResultObject<E> {

    public static final Integer OK = 200;

    public static final Integer FAIL = 400;

    public static final Integer UN_AUTHORIZED = 401;

    public static final Integer FORBIDDEN = 403;

    public static final Integer NOT_MODIFIED = 304;
    
    public static final String SUCCESS = "success";

    /** 是否成功 */
	//value = "是否成功")
    private Boolean success;
    /** 返回码 */
	//value = "状态码")
    private Integer code;
    /** 返回信息 */
	//value = "返回信息提示")
	private String msg;
    /** 返回数据 */
	//value = "返回数据集合")
    private E data;

    public static ResultObject ok(){
    	ResultObject r = new ResultObject();
    	r.setMsg(SUCCESS);
    	r.setCode(OK);
    	r.setData("");
    	r.setSuccess(true);
    	return r;
    }
    
    public static ResultObject ok(Object data){
    	ResultObject r = ok();
    	r.setMsg(SUCCESS);
    	r.setData(data);
    	return r;
    }
    
    public static ResultObject ok(String msg){
    	ResultObject r = ok();
    	r.setMsg(msg);
    	return r;
    }

    public static ResultObject ok(String msg,Object data){
    	ResultObject r = ok();
    	r.setMsg(msg);
    	r.setData(data);
    	return r;
    }
    
	public static ResultObject fail() {
		ResultObject r = new ResultObject();
		r.setCode(FAIL);
		r.setSuccess(false);
		return r;
	}
	public static ResultObject fail(String msg) {
		ResultObject r = ResultObject.fail();
		r.setMsg(msg);
		r.setData("");
		return r;
	}
	public static ResultObject fail(String msg,Object data) {
		ResultObject r = ResultObject.fail();
		r.setMsg(msg);
		r.setData(data);
		return r;
	}
    
    public static ResultObject unAuthorized() {
    	ResultObject r = new ResultObject();
		r.setCode(UN_AUTHORIZED);
		r.setSuccess(false);
		return r;
    }
	public static ResultObject unAuthorized(Object data, String msg) {
		ResultObject r = ResultObject.fail();
    	r.setMsg(msg);
    	r.setData(data);
		return r;
	}
	
	public static ResultObject forbidden() {
		ResultObject r = new ResultObject();
		r.setCode(FORBIDDEN);
		r.setSuccess(false);
		return r;
	}
	public static ResultObject forbidden(Object data, String msg) {
		ResultObject r = ResultObject.fail();
		r.setMsg(msg);
		r.setData(data);
		return r;
	}

    
    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public E getData() {
        return data;
    }

    public void setData(E data) {
        this.data = data;
    }

}
