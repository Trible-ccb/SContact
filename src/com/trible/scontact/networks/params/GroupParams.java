package com.trible.scontact.networks.params;

import com.google.gson.Gson;
import com.trible.scontact.components.activity.SContactApplication;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.pojo.ContactInfo;
import com.trible.scontact.pojo.GroupInfo;
import com.trible.scontact.pojo.UserGroupRelationInfo;
import com.trible.scontact.utils.Bog;
import com.trible.scontact.utils.StringUtil;

public class GroupParams {

	static String mGroupPath = "/group";
	
	public static String getUserGroupParams(Long uid){
		String GetUserGroups = SContactApplication.getURL() + mGroupPath + "/get_user_groups";
		AccountInfo info = new AccountInfo();
		info.setId(uid+"");
		String v = new Gson().toJson(info);
		String url = GetUserGroups + "?json=" + StringUtil.getEncodeURLParams(v);
		Bog.v("getUserGroupParams url = " + url);
		return url;
	}

	public static String getContactGroupParams(Long cid){
		String GetUserGroups = SContactApplication.getURL() + mGroupPath + "/get_contact_groups";
		ContactInfo info = new ContactInfo();
		info.setId(cid+"");
		String v = new Gson().toJson(info);
		String url = GetUserGroups + "?json=" + StringUtil.getEncodeURLParams(v);
		Bog.v("getContactGroupParams url = " + url);
		return url;
	}
	
	public static String getAllGroupParams( ){
		String GetAllGroups = SContactApplication.getURL() + mGroupPath + "/get_all_groups";
		String url = GetAllGroups;
		Bog.v("getAllGroupParams url = " + url);
		return url;
	}
	
	public static String getGroupParams(Long gid){
		String GetGroupById = SContactApplication.getURL() + mGroupPath + "/get_groups";
		GroupInfo info = new GroupInfo();
//		info.setId(gid);
		String v = new Gson().toJson(info);
		String url = GetGroupById + "?json=" + StringUtil.getEncodeURLParams(v);
		Bog.v("getGroupParams url = " + url);
		return url;
	}
	
	public static String getDeleteGroupParams(Long gid){
		String GetDeleteGroup = SContactApplication.getURL() + mGroupPath + "/delete_group";
		GroupInfo info = new GroupInfo();
//		info.setId(gid);
		String v = new Gson().toJson(info);
		String url = GetDeleteGroup + "?json=" + StringUtil.getEncodeURLParams(v);
		Bog.v("getDeleteGroupParams url = " + url);
		return url;
	}
	
	public static String getUpdateParams(GroupInfo info){
		String GetUpdateGroup = SContactApplication.getURL() + mGroupPath + "/update_group";
		String v = new Gson().toJson(info);
		String url = GetUpdateGroup + "?json=" + StringUtil.getEncodeURLParams(v);
		Bog.v("getUpdateParams url = " + url);
		return url;
	}
	
	public static String getAddParams(GroupInfo info,String contactIds){
		String GetAddGroup = SContactApplication.getURL() + mGroupPath + "/add_group";
		String v = new Gson().toJson(info);
		UserGroupRelationInfo info2 = new UserGroupRelationInfo();
//		info2.setContactIds(contactIds);
		String v2 = new Gson().toJson(info2);
		String url = GetAddGroup 
				+ "?json=" + StringUtil.getEncodeURLParams(v)
				+ "&contactIds=" + StringUtil.getEncodeURLParams(v2);
		Bog.v("getAddParams url = " + url);
		return url;
	}
	
	public static String getSearchGroupParams(String query){
		String GetSearchGroup = SContactApplication.getURL() + mGroupPath + "/search_group";
		GroupInfo info = new GroupInfo();
		info.setDisplayName(query);
		String v = new Gson().toJson(info);
		String url = GetSearchGroup + "?json=" + StringUtil.getEncodeURLParams(v);
		Bog.v("getSearchGroupParams url = " + url);
		return url;
	}
}
