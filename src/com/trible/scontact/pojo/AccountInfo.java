package com.trible.scontact.pojo;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trible.scontact.managers.PrefManager;
import com.trible.scontact.value.GlobalValue;
import com.trible.scontact.value.PrefKeys;

 
 
 
public class AccountInfo extends BaseInfo implements Serializable{

	private static final long serialVersionUID = 1L;
	private Integer status,gender;
	Long birthday,createTime,id;
	private String displayName,phoneNumber,photoUrl,
	email,realName,description,type,password,cookie;

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
	 
	public Long getBirthday() {
		return birthday;
	}
	public void setBirthday(Long birthday) {
		this.birthday = birthday;
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
		return phoneNumber;
	}
	/**
	 *  
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
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
}
