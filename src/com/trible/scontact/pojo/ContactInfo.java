package com.trible.scontact.pojo;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.trible.scontact.database.DBConstants;
import com.trible.scontact.managers.PrefManager;
import com.trible.scontact.pojo.ContactInfo.FieldName;
import com.trible.scontact.utils.Base64Util;
import com.trible.scontact.utils.ListUtil;
import com.trible.scontact.value.PrefKeys;


@AVClassName(FieldName.table_name)
@DatabaseTable(tableName=FieldName.table_name)
public class ContactInfo extends AVObject implements Serializable{
	
	private static final long serialVersionUID = 2279560755875633905L;
	
//	@DatabaseField(dataType=DataType.LONG_OBJ,columnName=DBConstants.RawContactFieldName.id)
//	private Long id;
//	
//	@DatabaseField(dataType=DataType.LONG_OBJ,columnName=DBConstants.RawContactFieldName.user_id)
//	private Long userId;
//	
//	@DatabaseField(dataType=DataType.STRING,columnName=DBConstants.RawContactFieldName.contact_string)
//	private String contact;
//	
//	@DatabaseField(dataType=DataType.INTEGER_OBJ,columnName=DBConstants.RawContactFieldName.status)
//	private Integer status;
//	
//	@DatabaseField(dataType=DataType.LONG,columnName=DBConstants.RawContactFieldName.latest_used_time)
//	private long lastestUsedTime;
//	
//	@DatabaseField(dataType=DataType.STRING,columnName=DBConstants.RawContactFieldName.type)
//	private String type;
	
	public String getType() {
		return getString(FieldName.type);
	}
	public void setType(String type) {
		put(FieldName.type, type);
	}
	public String getId() {
		return getObjectId();
	}
	public void setId(String id) {
		setObjectId(id);
	}
	public AccountInfo getOwner(){
		return getAVUser(FieldName.owner,AccountInfo.class);
	}
	public void setOwner(AccountInfo owner){
		put(FieldName.owner, owner);
	}
	public String getContact() {
//		return Base64Util.decode(contact);
		return getString(FieldName.contact);
	}
	public void setContact(String contact) {
		put(FieldName.contact, contact);
	}
	
	public Integer getStatus() {
		return getInt(FieldName.status);
	}
	/**
	 * 
	 */
	public void setStatus(Integer status) {
		put(FieldName.status, status);
	}
	public ContactInfo copy(){
		
		ContactInfo n = new ContactInfo();
//		try {
//			BeanUtils.copyProperties(n, this);
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			e.printStackTrace();
//		}
		n.setContact(getContact());
		n.setObjectId(getId());
		n.setStatus(getStatus());
		n.setType(getType());
		n.setOwner(getOwner());
		return n;
	}
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
	public interface FieldName{
		String table_name = "Contacts_info";//联系方式表名字
//		String id = "id";
		String owner = "owner";//联系方式的拥有者
		String contact = "contact";//联系方式字符串
		String status = "status";//联系方式状态
//		String latest_used_time = "latest_used_time";
		String type = "type";//联系方式类型
	}
}
