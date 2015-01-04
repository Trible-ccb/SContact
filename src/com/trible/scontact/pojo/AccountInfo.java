package com.trible.scontact.pojo;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.CollationKey;
import java.text.Collator;
import java.util.List;
import java.util.Locale;

import com.alibaba.fastjson.annotation.JSONField;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVUser;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.table.DatabaseTable;
import com.trible.scontact.database.DBConstants;
import com.trible.scontact.utils.StringUtil;

 
 
@DatabaseTable(tableName=DBConstants.AccountFieldName.table_name) 
public class AccountInfo extends AVUser implements Serializable,Comparable<AccountInfo>{

	private static final long serialVersionUID = 1L;
	
	public String getInstallationId() {
		return getString(FieldName.INSTALLATION);
	}
	public void setInstallationId(String installation) {
		if ( installation == null ){
			put(FieldName.INSTALLATION, "");
		} else {
			put(FieldName.INSTALLATION, installation);
		}
	}

	public String getId(){
		return getObjectId();
	}
	public void setId(String id){
		setObjectId(id);
	}
	public String getDisplayName(){
		return getUsername();
	}
	public void setDisplayName(String username){
		setUsername(username);
	}
	//临时存放别人对用户的可见的联系方式
	@JSONField(serialize=false)
	private List<ContactInfo> contactsList;
	
	public String getPinyinname() {
		String pinyinname = getString(FieldName.PINYINNAME);
		String displayName = getUsername();
		if ( pinyinname == null ){
			setPinyinname(StringUtil.converterToSpell(displayName));
		}
		return pinyinname == null ? displayName : pinyinname;
	}
	public void setPinyinname(String pinyinname) {
		put(FieldName.PINYINNAME,pinyinname);
		
	}
	
	public Long getBirthday() {
		return getLong(FieldName.BIRTHDAY);
	}
	public void setBirthday(Long birthday) {
		put(FieldName.BIRTHDAY, birthday);
	}
	
	public Integer getStatus() {
		return getInt(FieldName.STATUS);
	}
	/**
	 *  
	 */
	public void setStatus(Integer status) {
		put(FieldName.STATUS, status);
	}
	
	 
	public Integer getGender() {
		return getInt(FieldName.GENDER);
	}
	/**
	 *  
	 */
	public void setGender(Integer gender) {
		put(FieldName.GENDER, gender);
	}
	
	 
	public String getPhoneNumber() {
		return getMobilePhoneNumber();
	}
	/**
	 *  
	 */
	public void setPhoneNumber(String phoneNumber) {
		setMobilePhoneNumber(phoneNumber);
	}
	
	 
	public String getRealName() {
		return getString(FieldName.REALNAME);
	}
	public void setRealName(String realName) {
		put(FieldName.REALNAME,realName);
	}
	
	public String getDescription() {
		return getString(FieldName.DESCRIPTION);
	}
	public void setDescription(String description) {
		put(FieldName.DESCRIPTION,description);
	}
	
	 
	public String getType() {
		return getString(FieldName.TYPE);
	}
	/**
	 *  
	 */
	public void setType(String type) {
		put(FieldName.TYPE, type);
	}
	public String getPhotoUrl() {
		return getString(FieldName.PHOTOURL);
	}
	public void setPhotoUrl(String photoUrl) {
		put(FieldName.PHOTOURL, photoUrl);
	}
	
	static AccountInfo sInstance;
	public static AccountInfo getInstance(){
		if ( sInstance == null ){
			sInstance = AccountInfo.getCurrentUser(AccountInfo.class);
		}
		if (sInstance == null)
		sInstance = new AccountInfo();
		return sInstance;
	}
	public AccountInfo copy(){
		AccountInfo tmp = new AccountInfo();
		tmp.setDisplayName(getDisplayName());
		tmp.setBirthday(getBirthday());
		tmp.setDescription(getDescription());
		tmp.setEmail(getEmail());
		tmp.setGender(getGender());
		tmp.setStatus(getStatus());
		tmp.setPhoneNumber(getMobilePhoneNumber());
		tmp.setPhotoUrl(getPhotoUrl());
		tmp.setRealName(getRealName());
		tmp.setType(getType());
		tmp.setId(getId());
		return tmp;
	}
	public static void setAccountInfo(AccountInfo info){
		sInstance = info;
		AccountInfo.changeCurrentUser(info, true);
	}
	
	public void saveToPref(){
	}
	
	public static AccountInfo getFromPref(){
		return getInstance();
	}

	public List<ContactInfo> getContactsList() {
		return contactsList;
	}
	public void setContactsList(List<ContactInfo> mContactsList) {
		this.contactsList = mContactsList;
	}
	
	public Type listType() {
		return new TypeToken<List<AccountInfo>>(){}.getType();
	}
	public boolean isSignOut(){
//		return GlobalValue.USTATUS_SIGN_OUT.equals(status);
		return getInstance() == null ||  getUsername() == null  ;
	}
	public void signOut(){
		setInstallationId(null);
		saveInBackground();
		logOut();
	}
	@Override
	public int compareTo(AccountInfo another) {
		Locale l = Locale.US;
		Collator collator = Collator.getInstance(l);
		String n1 = getPinyinname();
		String n2 = another.getPinyinname();
		if ( n1 == null )n1 ="";
		if ( n2 == null )n2 = "";
		CollationKey k1 = collator.getCollationKey(n1);
		CollationKey k2 = collator.getCollationKey(n2);
		return k1.compareTo(k2);
	}
	
	public interface FieldName{
		String INSTALLATION = "installationId";
		String GENDER = "gender"; 
		String BIRTHDAY = "birthday";
		String STATUS = "status";
		String REALNAME = "realname";
		String PHOTOURL = "photo_url";
		String TYPE = "type";
		String DESCRIPTION = "description";
		String PINYINNAME = "pinyinname";
		
	}
}
