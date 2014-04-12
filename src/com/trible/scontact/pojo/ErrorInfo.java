package com.trible.scontact.pojo;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.trible.scontact.utils.GlobalValue;

 
public class ErrorInfo extends BaseInfo implements Serializable {

	private static final long serialVersionUID = 7631954908857734023L;
	int code;
	String messgae;
	
	public ErrorInfo(){};
	public ErrorInfo(int code, String messgae) {
		super();
		this.code = code;
		this.messgae = messgae;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessgae() {
		return messgae;
	}
	public void setMessgae(String messgae) {
		this.messgae = messgae;
	}
	
	@Override
	public String toString() {
		return "code:" + getCode() 
				+ "\nmessage:"+ getMessgae();
	}
	
	public static ErrorInfo getUnkownErr(){
		ErrorInfo err = new ErrorInfo(GlobalValue.CODE_UNKOWN_ERROR, GlobalValue.STR_UNKOWN_ERROR);
		return err;
	}
	
	@Override
	public Type listType() {
		return new TypeToken<List<ErrorInfo>>(){}.getType();
	}
}
