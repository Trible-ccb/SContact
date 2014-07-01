package com.trible.scontact.pojo;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.trible.scontact.database.DBConstants;

/**
 * 
 * this class is the middle mapping for phone numbers and groups
 * 
 */


@DatabaseTable(tableName=DBConstants.PhoneGroupInfoFieldName.table_name)
public class PhoneAndGroupInfo extends BaseInfo implements Serializable{

	private static final long serialVersionUID = 1L;
	
	
	@DatabaseField(dataType=DataType.LONG_OBJ,columnName=DBConstants.PhoneGroupInfoFieldName.id)
	private Long id;
	
	@DatabaseField(dataType=DataType.LONG_OBJ,columnName=DBConstants.PhoneGroupInfoFieldName.group_id)
	private Long groupId;
	
	@DatabaseField(dataType=DataType.LONG_OBJ,columnName=DBConstants.PhoneGroupInfoFieldName.user_id)
	private Long userId;
	
	@DatabaseField(dataType=DataType.STRING,columnName=DBConstants.PhoneGroupInfoFieldName.contact_ids)
	private String contactIds;
//	List<Long> contactIds;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	
	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	
	
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
	
	@Override
	public Type listType() {
		return new TypeToken<List<PhoneAndGroupInfo>>(){}.getType();
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getContactIds() {
		return contactIds;
	}
	public void setContactIds(String contactIds) {
		this.contactIds = contactIds;
	}
}
