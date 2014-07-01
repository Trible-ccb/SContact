package com.trible.scontact.pojo;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

import ccb.java.android.utils.encoder.SecurityMethod;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.trible.scontact.database.DBConstants;
import com.trible.scontact.managers.PrefManager;
import com.trible.scontact.utils.ListUtil;
import com.trible.scontact.value.PrefKeys;



@DatabaseTable(tableName=DBConstants.RawContactFieldName.table_name)
public class ContactInfo extends BaseInfo implements Serializable{
	
	private static final long serialVersionUID = 2279560755875633905L;
	
	@DatabaseField(dataType=DataType.LONG_OBJ,columnName=DBConstants.RawContactFieldName.id)
	private Long id;
	
	@DatabaseField(dataType=DataType.LONG_OBJ,columnName=DBConstants.RawContactFieldName.user_id)
	private Long userId;
	
	@DatabaseField(dataType=DataType.STRING,columnName=DBConstants.RawContactFieldName.contact_string)
	private String contact;
	
	@DatabaseField(dataType=DataType.INTEGER_OBJ,columnName=DBConstants.RawContactFieldName.status)
	private Integer status;
	
	@DatabaseField(dataType=DataType.LONG,columnName=DBConstants.RawContactFieldName.latest_used_time)
	private long lastestUsedTime;
	
	@DatabaseField(dataType=DataType.STRING,columnName=DBConstants.RawContactFieldName.type)
	private String type;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public String getContact() {
		return SecurityMethod.getAESInstance().Decryptor(contact);
	}
	public void setContact(String contact) {
		this.contact = SecurityMethod.getAESInstance().Encrytor(contact);
	}
	
	public Integer getStatus() {
		return status;
	}
	/**
	 * 
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	public long getLastestUsedTime() {
		return lastestUsedTime;
	}
	public void setLastestUsedTime(long lastestUsedTime) {
		this.lastestUsedTime = lastestUsedTime;
	}
	public ContactInfo copy(){
		ContactInfo n = new ContactInfo();
		n.setContact(getContact());
		n.setId(getId());
		n.setLastestUsedTime(getLastestUsedTime());
		n.setStatus(getStatus());
		n.setType(getType());
		n.setUserId(getUserId());
		return n;
	}
	@Override
	public Type listType() {
		return new TypeToken<List<ContactInfo>>(){}.getType();
	}
	
	public static void saveToPref(List<ContactInfo> contacts){
		PrefManager pref = PrefManager.getInstance(PrefKeys.ALL_CONTACTS);
		String v = new Gson().toJson(contacts);
		pref.putString(PrefKeys.ALL_CONTACTS + AccountInfo.getInstance().getId(), v);
	}
	public static void clear(){
		PrefManager pref = PrefManager.getInstance(PrefKeys.ALL_CONTACTS);
		pref.putString(PrefKeys.ALL_CONTACTS + AccountInfo.getInstance().getId(), null);
	}
	public static List<ContactInfo> getFromPref(){
		return GsonHelper.getInfosFromJson(
				PrefManager.getInstance(PrefKeys.ALL_CONTACTS).getString(PrefKeys.ALL_CONTACTS + AccountInfo.getInstance().getId()),
				new ContactInfo().listType());
	}
	
	public static String arrayToString(List<ContactInfo> infos){
		if (ListUtil.isEmpty(infos))return null;
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for ( ContactInfo c : infos ){
//			cids.add(c.getId());
			sb.append(c.getId() + ",");
		}
		int idx = sb.lastIndexOf(",");
		sb.replace(idx, sb.length(), "]");
		return sb.toString();
	}
}
