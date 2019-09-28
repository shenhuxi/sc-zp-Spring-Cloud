package com.sczp.system.util.result;

/**
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

    public static final Integer UN_LOGIN = 403;

    public static final Integer NOT_MODIFIED = 304;


    /** 是否成功 */
    private Boolean success;
    /** 返回码 */
    private Integer code;
    /** 返回信息 */
    private String msg;
    /** 返回数据 */
    private E data;

    public static ResultObject ok(){
    	ResultObject r = new ResultObject();
    	r.setCode(OK);
    	r.setSuccess(true);
    	return r;
    }
    
    public static ResultObject ok(String msg){
    	ResultObject r = ok();
    	r.setMsg(msg);
    	return r;
    }
    
    public static ResultObject ok(Object data){
    	ResultObject r = ok();
    	r.setData(data);
    	return r;
    }
    
    public static ResultObject ok(Object data,String msg){
    	ResultObject r = ok();
    	r.setData(data);
    	r.setMsg(msg);
    	return r;
    }
    
	public static ResultObject error(String msg) {
		ResultObject r = new ResultObject();
		r.setCode(FAIL);
    	r.setMsg(msg);
		return r;
	}
    
	public static ResultObject unAuthorized() {
		ResultObject r = new ResultObject();
		r.setCode(UN_AUTHORIZED);
		return r;
	}
	
	public static ResultObject unAuthorized(String msg) {
		ResultObject r = new ResultObject();
		r.setCode(UN_AUTHORIZED);
    	r.setMsg(msg);
		return r;
	}

    public static ResultObject unAuthorized(String msg,Object data) {
        ResultObject r = new ResultObject();
        r.setCode(UN_AUTHORIZED);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }
	
	public static ResultObject unLogin() {
		ResultObject r = new ResultObject();
		r.setCode(UN_LOGIN);
		return r;
	}
	
	public static ResultObject unLogin(String msg) {
		ResultObject r = new ResultObject();
		r.setCode(UN_LOGIN);
    	r.setMsg(msg);
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

    public Object getData() {
        return data;
    }

    public void setData(E data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResultObject{" +
                "success=" + success +
                ", code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data.toString() +
                '}';
    }
}
