package com.trible.scontact.pojo;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.trible.scontact.database.DBConstants;
import com.trible.scontact.pojo.UserGroupRelationInfo.FieldName;

/**
 * 
 * this class is the middle mapping for users and users
 * 
 */

@AVClassName(UserRelationInfo.FieldName.table_name)
@DatabaseTable(tableName=DBConstants.UserRelationFieldName.table_name)
public class UserRelationInfo extends AVObject implements Serializable{

	private static final long serialVersionUID = 1L;
	
//	@DatabaseField(dataType=DataType.LONG_OBJ,columnName=DBConstants.UserRelationFieldName.id)
//	private Long id;
//	
//	@DatabaseField(dataType=DataType.LONG_OBJ,columnName=DBConstants.UserRelationFieldName.follow_user_id)
//	private Long followUserId;
//	
//	@DatabaseField(dataType=DataType.LONG_OBJ,columnName=DBConstants.UserRelationFieldName.user_id)
//	private Long userId;
//	
//	@DatabaseField(dataType=DataType.STRING,columnName=DBConstants.UserRelationFieldName.contact_ids)
//	private String contactIds;
//	List<Long> contactIds;
	
//	public Long getContactId() {
//		return contactId;
//	}
//	public void setContactId(Long contactId) {
//		this.contactId = contactId;
//	}
	
	
//	public List<Long> getContactIds() {
//		return contactIds;
//	}
//	public void setContactIds(List<Long> contactIds) {
//		this.contactIds = contactIds;
//	}
	
	public Type listType() {
		return new TypeToken<List<UserRelationInfo>>(){}.getType();
	}
	public String getId() {
		return getObjectId();
	}
	public void setId(String id) {
		setObjectId(id);
	}
	
	public AccountInfo getFollower() {
		return getAVObject(FieldName.follower);
	}
	public void setFollower(AccountInfo follower) {
		put(FieldName.follower, follower);
	}
	public AccountInfo getUser() {
		return getAVUser(FieldName.user, AccountInfo.class);
	}
	public void setUser(AccountInfo account) {
		put(FieldName.user, account);
	}
	public List<ContactInfo> getContacts() {
		return getList(FieldName.contacts, ContactInfo.class);
	}
	public void setContacts(List<ContactInfo> contactInfos) {
		put(FieldName.contacts, contactInfos);
	}
	
	public interface FieldName{
		String table_name = "User_relation";//好友关系表名字
		String follower = "follower";//用户的好友
		String user = "user"; //用户自己
		String contacts = "contacts";//向好友展示的联系方式列表
	}
}
