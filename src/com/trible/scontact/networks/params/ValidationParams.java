package com.trible.scontact.networks.params;

import com.google.gson.Gson;
import com.trible.scontact.components.activity.SContactApplication;
import com.trible.scontact.pojo.ValidateInfo;
import com.trible.scontact.utils.StringUtil;

public class ValidationParams {

	static String mPath = "/group_validate";
	
	@Deprecated
	public static String getAddOneValidateMessageParams(ValidateInfo info){
		String v = new Gson().toJson(info);
		String url = SContactApplication.getURL() + mPath 
				+ "/addOneValidateMessage"
				+ "?json=" 
				+ StringUtil.getEncodeURLParams(v);
		return url;
	}
	
	public static String getAddRelationshipParams(ValidateInfo info){
		String v = new Gson().toJson(info);
		String url = SContactApplication.getURL() + mPath 
				+ "/addRelationship"
				+ "?json=" 
				+ StringUtil.getEncodeURLParams(v);
		return url;
	}
	
	public static String getRemoveRelationshipParams(ValidateInfo info){
		String v = new Gson().toJson(info);
		String url = SContactApplication.getURL() + mPath 
				+ "/removeRelationship"
				+ "?json=" 
				+ StringUtil.getEncodeURLParams(v);
		return url;
	}
	
	public static String getMyValidateListParams(Long uid){
		ValidateInfo info = new ValidateInfo();
		info.setEnd_user_id(uid);
		String v = new Gson().toJson(info);
		String url = SContactApplication.getURL() + mPath 
				+ "/getMyValidateList"
				+ "?json=" 
				+ StringUtil.getEncodeURLParams(v);
		return url;
	}
	
	public static String getAcceptOneValidateParams(
			ValidateInfo info,String optionContactids){
		String v = new Gson().toJson(info);
		String url = SContactApplication.getURL() + mPath 
				+ "/AcceptOneValidate"
				+ "?json=" 
				+ StringUtil.getEncodeURLParams(v)
				+ "&optionContactids="
				+ StringUtil.getEncodeURLParams(optionContactids);
		return url;
	}
	
	public static String getNotAcceptOneValidateParams(ValidateInfo info){
		String v = new Gson().toJson(info);
		String url = SContactApplication.getURL() + mPath 
				+ "/NotAcceptOneValidate"
				+ "?json=" 
				+ StringUtil.getEncodeURLParams(v);
		return url;
	}
}
