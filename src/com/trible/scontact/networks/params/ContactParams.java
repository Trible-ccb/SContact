package com.trible.scontact.networks.params;

import com.google.gson.Gson;
import com.trible.scontact.components.activity.SContactApplication;
import com.trible.scontact.pojo.ContactInfo;
import com.trible.scontact.utils.Bog;
import com.trible.scontact.utils.StringUtil;

public class ContactParams {

	static String mPath = "/contact";
	
	public static String getContactByIdParams(Long cid){
		ContactInfo info = new ContactInfo();
		info.setId(cid);
		String v = new Gson().toJson(info);
		String url = SContactApplication.getURL() + mPath 
				+ "/get_contact"
				+ "?json=" 
				+ StringUtil.getEncodeURLParams(v);
		return url;
	}
	
	public static String getSearchContactParams(String query){
		ContactInfo info = new ContactInfo();
		info.setContact(query);
		String v = new Gson().toJson(info);
		String url = SContactApplication.getURL() + mPath 
				+ "/search_contact"
				+ "?json=" 
				+ StringUtil.getEncodeURLParams(v);
		Bog.v("getSearchContactParams url = " + url);
		return url;
	}
	
	public static String getAddContactParams(ContactInfo info){
		String v = new Gson().toJson(info);
		String url = SContactApplication.getURL() + mPath 
				+ "/add_contact"
				+ "?json=" 
				+ StringUtil.getEncodeURLParams(v);
		Bog.v("getAddContactParams url = " + url);
		return url;
	}
	
	public static String getUpdateContactParams(ContactInfo info){
		String v = new Gson().toJson(info);
		String url = SContactApplication.getURL() + mPath 
				+ "/update_contact"
				+ "?json=" 
				+ StringUtil.getEncodeURLParams(v);
		Bog.v("getUpdateContactParams url = " + url);
		return url;
	}
	
	public static String getDeleteContactParams(Long cid){
		ContactInfo info = new ContactInfo();
		info.setId(cid);
		String v = new Gson().toJson(info);
		String url = SContactApplication.getURL() + mPath 
				+ "/delete_contact"
				+ "?json=" 
				+ StringUtil.getEncodeURLParams(v);
		Bog.v("getDeleteContactParams url = " + url);
		return url;
	}
	
	public static String getUserContactsParams(Long uid){
		ContactInfo info = new ContactInfo();
		info.setUserId(uid);
		String v = new Gson().toJson(info);
		String url = SContactApplication.getURL() + mPath 
				+ "/get_user_contacts"
				+ "?json=" 
				+ StringUtil.getEncodeURLParams(v);
		Bog.v("getUserContactsParams url = " + url);
		return url;
	}
	
	public static String getAllContactParams( ){
		String url = SContactApplication.getURL() + mPath 
				+ "/get_all_contacts"
				;
		Bog.v("getAllContactParams url = " + url);
		return url;
	}
}
