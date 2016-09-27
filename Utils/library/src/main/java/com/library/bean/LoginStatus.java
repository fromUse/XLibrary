package com.library.bean;

/**
 * Created by chen on 2016/9/26.
 */

public class LoginStatus {
	public static final int NAME_ERROR = 100;
	public static final int PASSWD_ERROR = 101;
	public static final int PARAMET_ERROR = 110;
	public static final int SUCCESS = 200;
   

	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	private int code;
    private String msg;
    private boolean success;

}
