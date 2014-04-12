package com.trible.scontact.networks.params;

import java.net.URLEncoder;

import com.google.gson.Gson;
import com.trible.scontact.components.activity.SContactApplication;
import com.trible.scontact.pojo.ContactInfo;
import com.trible.scontact.utils.Bog;

public class ContactParams {

	static String mAccountPath = "/contact";
	public static String FullAccountPath = SContactApplication.URL + mAccountPath;
	
	public static String getContactByIdParams(Long cid){
		ContactInfo info = new ContactInfo();
		info.setId(cid);
		String v = new Gson().toJson(info);
		String url = FullAccountPath 
				+ "/get_contact"
				+ "?json=" 
				+ URLEncoder.encode(v);
		Bog.v("getContactByIdParams url = " + url);
		return url;
	}
	
	public static String getSearchContactParams(String query){
		ContactInfo info = new ContactInfo();
		info.setContact(query);
		String v = new Gson().toJson(info);
		String url = FullAccountPath 
				+ "/search_contact"
				+ "?json=" 
				+ URLEncoder.encode(v);
		Bog.v("getSearchContactParams url = " + url);
		return url;
	}
	
	public static String getAddContactParams(ContactInfo info){
		String v = new Gson().toJson(info);
		String url = FullAccountPath 
				+ "/add_contact"
				+ "?json=" 
				+ URLEncoder.encode(v);
		Bog.v("getAddContactParams url = " + url);
		return url;
	}
	
	public static String getUpdateContactParams(ContactInfo info){
		String v = new Gson().toJson(info);
		String url = FullAccountPath 
				+ "/update_contact"
				+ "?json=" 
				+ URLEncoder.encode(v);
		Bog.v("getAddContactParams url = " + url);
		return url;
	}
	
	public static String getDeleteContactParams(Long cid){
		ContactInfo info = new ContactInfo();
		info.setId(cid);
		String v = new Gson().toJson(info);
		String url = FullAccountPath 
				+ "/add_contact"
				+ "?json=" 
				+ URLEncoder.encode(v);
		Bog.v("getDeleteContactParams url = " + url);
		return url;
	}
	
	public static String getUserContactsParams(Long uid){
		ContactInfo info = new ContactInfo();
		info.setUserId(uid);
		String v = new Gson().toJson(info);
		String url = FullAccountPath 
				+ "/get_user_contacts"
				+ "?json=" 
				+ URLEncoder.encode(v);
		Bog.v("getDeleteContactParams url = " + url);
		return url;
	}
	
	public static String getAllContactParams( ){
		String url = FullAccountPath 
				+ "/get_all_contacts"
				;
		Bog.v("getAllContactParams url = " + url);
		return url;
	}
}
