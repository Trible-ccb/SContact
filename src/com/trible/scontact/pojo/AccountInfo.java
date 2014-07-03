package com.trible.scontact.pojo;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.CollationKey;
import java.text.Collator;
import java.util.List;
import java.util.Locale;

import u.aly.an;

import ccb.java.android.utils.encoder.SecurityMethod;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.trible.scontact.database.DBConstants;
import com.trible.scontact.managers.PrefManager;
import com.trible.scontact.utils.StringUtil;
import com.trible.scontact.value.GlobalValue;
import com.trible.scontact.value.PrefKeys;

 
 
@DatabaseTable(tableName=DBConstants.AccountFieldName.table_name) 
public class AccountInfo extends BaseInfo implements Serializable,Comparable<AccountInfo>{

	private static final long serialVersionUID = 1L;
	
	private Integer status;
	
	@DatabaseField(dataType=DataType.INTEGER_OBJ,columnName=DBConstants.AccountFieldName.user_gender)
	Integer gender;
	
	Long birthday;
	
	@DatabaseField(dataType=DataType.LONG_OBJ,columnName=DBConstants.AccountFieldName.user_create_time)
	Long createTime;
	
	@DatabaseField(dataType=DataType.LONG_OBJ,columnName=DBConstants.AccountFieldName.user_id)
	Long id;
	
	@DatabaseField(dataType=DataType.STRING,columnName=DBConstants.AccountFieldName.user_display_name)
	private String displayName;
	
	String phoneNumber;
	
	@DatabaseField(dataType=DataType.STRING,columnName=DBConstants.AccountFieldName.user_photo_url)
	String photoUrl;

	@DatabaseField(dataType=DataType.STRING,columnName=DBConstants.AccountFieldName.user_email)
	String email;
	
	@DatabaseField(dataType=DataType.STRING,columnName=DBConstants.AccountFieldName.user_real_name)
	String realName;
	
	@DatabaseField(dataType=DataType.STRING,columnName=DBConstants.AccountFieldName.user_description)
	String description;
	
	@DatabaseField(dataType=DataType.STRING,columnName=DBConstants.AccountFieldName.user_type)
	String type;
	
	String password;
	String cookie;
	
	@DatabaseField(dataType=DataType.STRING,columnName=DBConstants.AccountFieldName.user_notify_id)
	String notifyId;
	
	String thirdPartyId;

	String pinyinname;
	
	private List<ContactInfo> contactsList;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
		
	}
	
	public String getPinyinname() {
		if ( pinyinname == null ){
			setPinyinname(StringUtil.converterToSpell(displayName));
		}
		return pinyinname == null ? displayName : pinyinname;
	}
	public void setPinyinname(String pinyinname) {
		this.pinyinname = pinyinname;
	}
	
	public Long getBirthday() {
		return birthday;
	}
	public void setBirthday(Long birthday) {
		this.birthday = birthday;
	}
	
	public String getThirdPartyId() {
		return thirdPartyId;
	}
	public void setThirdPartyId(String thirdPartyId) {
		this.thirdPartyId = thirdPartyId;
	}
	
	public Long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
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
	
	 
	public Integer getGender() {
		return gender;
	}
	/**
	 *  
	 */
	public void setGender(Integer gender) {
		this.gender = gender;
	}
	
	 
	public String getPhoneNumber() {
		return SecurityMethod.getAESInstance().Decryptor(phoneNumber);
	}
	/**
	 *  
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = SecurityMethod.getAESInstance().Encrytor(phoneNumber);
	}
	
	 
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	 
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	
	 
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	 
	public String getType() {
		return type;
	}
	/**
	 *  
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	 
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getNotifyId() {
		return notifyId;
	}
	public void setNotifyId(String notifyId) {
		this.notifyId = notifyId;
	}
	 
	public String getCookie() {
		return cookie;
	}
	public void setCookie(String cookie) {
		this.cookie = cookie;
	}
	public String getPhotoUrl() {
		return photoUrl;
	}
	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}
	
	static AccountInfo sInstance;
	public static AccountInfo getInstance(){
		if (sInstance == null)
		sInstance = new AccountInfo();
		return sInstance;
	}
	public AccountInfo copy(){
		AccountInfo tmp = new AccountInfo();
		tmp.setBirthday(birthday);
		tmp.setContactsList(contactsList);
		tmp.setCookie(cookie);
		tmp.setCreateTime(createTime);
		tmp.setDescription(description);
		tmp.setDisplayName(displayName);
		tmp.setEmail(email);
		tmp.setGender(gender);
		tmp.setStatus(status);
		tmp.setId(id);
		tmp.setPassword(password);
		tmp.setPhoneNumber(phoneNumber);
		tmp.setPhotoUrl(photoUrl);
		tmp.setRealName(realName);
		tmp.setType(type);
		tmp.setNotifyId(notifyId);
		tmp.setThirdPartyId(thirdPartyId);
		return tmp;
	}
	public static void setAccountInfo(AccountInfo info){
		sInstance = info;
	}
	
	public void saveToPref(){
		PrefManager pref = PrefManager.getInstance();
		String v = new Gson().toJson(this);
		pref.putString(AccountInfo.class.getSimpleName() + id, v);
		pref.putLong(PrefKeys.CURRENT_USER_ID, id);
	}
	
	public static AccountInfo getFromPref(){
		PrefManager pref = PrefManager.getInstance();
		Long id = pref.getLong(PrefKeys.CURRENT_USER_ID);
		AccountInfo info = new Gson().fromJson(
				pref.getString(AccountInfo.class.getSimpleName() + id), AccountInfo.class);
		return info;
	}
	
	public List<ContactInfo> getContactsList() {
		return contactsList;
	}
	public void setContactsList(List<ContactInfo> mContactsList) {
		this.contactsList = mContactsList;
	}
	
	@Override
	public Type listType() {
		return new TypeToken<List<AccountInfo>>(){}.getType();
	}
	
	public boolean isSignOut(){
		return GlobalValue.USTATUS_SIGN_OUT.equals(status);
	}
	@Override
	public int compareTo(AccountInfo another) {
		Locale l = Locale.US;
		Collator collator = Collator.getInstance(l);
		String n1 = getPinyinname();
		String n2 = another.getPinyinname();
		CollationKey k1 = collator.getCollationKey(n1);
		CollationKey k2 = collator.getCollationKey(n2);
		return k1.compareTo(k2);
	}
	
}
