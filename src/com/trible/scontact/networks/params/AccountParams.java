package com.trible.scontact.networks.params;

import java.net.URLEncoder;

import com.google.gson.Gson;
import com.trible.scontact.components.activity.SContactApplication;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.pojo.GroupInfo;
import com.trible.scontact.utils.Bog;
import com.trible.scontact.utils.StringUtil;

public class AccountParams {

	static String mAccountPath = "/account";
	public static String Login = SContactApplication.URL + mAccountPath + "/login";
	public static String Register = SContactApplication.URL + mAccountPath + "/register";
	public static String GetAccount = SContactApplication.URL + mAccountPath + "/get_account";
	public static String Delete = SContactApplication.URL + mAccountPath + "/delete";
	public static String GetFromGroup = SContactApplication.URL + mAccountPath + "/get_accounts_of_group";
	
	/**
	 * @param name
	 * @param password
	 * @return
	 */
	public static String getLoginParams(String name,String password){
		AccountInfo info = new AccountInfo();
		info.setDisplayName(name);
		info.setPassword(password);
		String v = new Gson().toJson(info);
		String url = Login + "?json=" + StringUtil.getEncodeURLParams(v);
		Bog.v("login url = " + url);
		return url;
	}
	
	/**
	 * @param name
	 * @param password
	 * @param phone
	 * @return 
	 */
	public static String getRegisterParams(String name,String password,String phone){
		AccountInfo info = new AccountInfo();
		info.setDisplayName(name);
		info.setPassword(password);
		info.setPhoneNumber(phone);
		String v = new Gson().toJson(info);
		String url = Register + "?json=" + StringUtil.getEncodeURLParams(v);
		Bog.v("register url = " + url);
		return url;
	}
	
	/**
	 * @param id for getting the AccountInfo
	 * @return  
	 */
	public static String getAccountByIdParams(Long id){
		AccountInfo info = new AccountInfo();
		info.setId(id);
		String v = new Gson().toJson(info);
		String url = GetAccount + "?json=" + StringUtil.getEncodeURLParams(v);
		return url;
	}
	
	public static String getDeleteAccountByIdParams(Long id){
		AccountInfo info = new AccountInfo();
		info.setId(id);
		String v = new Gson().toJson(info);
		String url = Delete + "?json=" + StringUtil.getEncodeURLParams(v);
		return url;
	}
	
	public static String getAccountByGroupIdParams(Long gid){
		GroupInfo info = new GroupInfo();
		info.setId(gid);
		String v = new Gson().toJson(info);
		String url = GetFromGroup + "?json=" + StringUtil.getEncodeURLParams(v);
		return url;
	}
}
