package com.trible.scontact.pojo;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;
import com.google.gson.reflect.TypeToken;
import com.trible.scontact.pojo.ValidateInfo.FieldName;



@AVClassName(FieldName.TABLE_NAME)
public class ValidateInfo extends AVObject implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
//	private Long id;
//	private Long start_user_id;
//	private String contact_ids;
//	private Long groupId;
//	private Long end_user_id;
//	private int is_group_to_user;
//	private long createTime;

//	private AccountInfo startUser,endUser;
//	private GroupInfo groupInfo;
	List<ContactInfo> contactsList;

	public String getId() {
		return getObjectId();
	}
	public void setId(String id) {
		setObjectId(id);
	}
//	public long getCreateTime() {
//		return createTime;
//	}
//	public void setCreateTime(long createTime) {
//		this.createTime = createTime;
//	}
//	
//	public Long getStart_user_id() {
//		return start_user_id;
//	}
//	public void setStart_user_id(Long start_user_id) {
//		this.start_user_id = start_user_id;
//	}
//	
//	
//	public Long getEnd_user_id() {
//		return end_user_id;
//	}
//	public void setEnd_user_id(Long end_user_id) {
//		this.end_user_id = end_user_id;
//	}
//	
//	
//	public int getIs_group_to_user() {
//		return is_group_to_user;
//	}
//	public void setIs_group_to_user(int is_group_to_user) {
//		this.is_group_to_user = is_group_to_user;
//	}
	
	
//	public String getContact_ids() {
//		return contact_ids;
//	}
//	public void setContact_ids(String contact_ids) {
//		this.contact_ids = contact_ids;
//	}
	
	public AccountInfo getFromUser() {
		return getAVUser(FieldName.FROM_USER, AccountInfo.class);
	}
	public void setFromUser(AccountInfo startUser) {
		put(FieldName.FROM_USER, startUser);
	}
	public boolean isGroupToUser(){
		return getBoolean(FieldName.IS_GROUP_TO_USER);
	}
	public void setIsGroupToUser(boolean isgrouptouser){
		put(FieldName.IS_GROUP_TO_USER, isgrouptouser);
	}
	public AccountInfo getToUser() {
		return getAVUser(FieldName.TO_USER, AccountInfo.class);
	}
	public void setToUser(AccountInfo endUser) {
		put(FieldName.TO_USER, endUser);
	}
	public GroupInfo getGroupInfo() {
		return getAVObject(FieldName.FROM_GROUP);
	}
	public void setGroupInfo(GroupInfo groupInfo) {
		put(FieldName.FROM_GROUP, groupInfo);
	}
	public String getMessage(){
		return getString(FieldName.MESSAGE);
	}
	public void setMessage(String message){
		put(FieldName.MESSAGE, message);
	}
	public List<ContactInfo> getContactsList() {
		return getList(FieldName.WITH_CONTACTS, ContactInfo.class);
	}
	public void setContactsList(List<ContactInfo> contactsList) {
		put(FieldName.WITH_CONTACTS, contactsList);
	}
	public Type listType() {
		return new TypeToken<List<ValidateInfo>>(){}.getType();
	}
	
	public interface FieldName{
		String TABLE_NAME = "valid_message_infos";//验证信息表名字
		String FROM_USER = "from_user";//信息来源用户
		String TO_USER = "to_user";//信息的目标用户
		String FROM_GROUP = "from_group";//如果存在，表示群邀请，或者加群
		String IS_GROUP_TO_USER = "is_group_to_user";//true 表示群邀请；false 申请加入群
		String WITH_CONTACTS = "with_contacts";//附带的联系方式列表 
		String MESSAGE = "message";
	}
}
