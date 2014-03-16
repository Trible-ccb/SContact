package com.trible.scontact.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import android.os.SystemClock;

//a locale cache for store users who had login
@DatabaseTable (tableName = "usersinfo")
public class AccountInfo extends Friendinfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@DatabaseField (generatedId = true)
	private int mUserId;
	
	@DatabaseField
	private String mUserName;
	
	
	@DatabaseField(canBeNull = true)
	private String mPhoneNumber;
	
	@DatabaseField
	private String mUserPassword;
	
	//if null,an unknown user
	@DatabaseField(canBeNull = true)
	private String mUserType;
	
	
	@DatabaseField(canBeNull = true)
	private String mUserImageUrl;
	
	@DatabaseField(canBeNull = true)
	private String mUserDescription;
	
	@DatabaseField(canBeNull = true)
	private long mUserUpdateTime;
	
	public String getmUserImageUrl() {
		return mUserImageUrl;
	}
	public void setmUserImageUrl(String mUserImageUrl) {
		this.mUserImageUrl = mUserImageUrl;
	}
	public String getmUserDescription() {
		return mUserDescription;
	}
	public void setmUserDescription(String mUserDescription) {
		this.mUserDescription = mUserDescription;
	}
	public long getmUserUpdateTime() {
		return mUserUpdateTime;
	}
	public void setmUserUpdateTime(long mUserUpdateTime) {
		this.mUserUpdateTime = mUserUpdateTime;
	}
	public int getmUserId() {
		return mUserId;
	}
	public void setmUserId(int mUserId) {
		this.mUserId = mUserId;
	}
	public String getmUserName() {
		return mUserName;
	}
	public void setmUserName(String mUserName) {
		this.mUserName = mUserName;
	}
	public String getmUserType() {
		return mUserType;
	}
	public void setmUserType(String mUserType) {
		this.mUserType = mUserType;
	}
	
	public String getmUserPassword() {
		return mUserPassword;
	}
	public void setmUserPassword(String mUserPassword) {
		this.mUserPassword = mUserPassword;
	}
	
	
	public String getmPhoneNumber() {
		return mPhoneNumber;
	}
	public void setmPhoneNumber(String mPhoneNumber) {
		this.mPhoneNumber = mPhoneNumber;
	}
	
	private static AccountInfo sAccountInfo;
	
	public static AccountInfo getInstance(){
		if ( sAccountInfo == null ){
			synchronized (AccountInfo.class) {
				if ( sAccountInfo == null ){
					sAccountInfo = new AccountInfo();
				}
			}
		}
		return sAccountInfo;
	}
	public static List<AccountInfo> getTestAccountData(){
		int count = 20;
		List<AccountInfo> ret = new ArrayList<AccountInfo>();
		
		for ( int i = 0 ; i < count ; i++ ){
			AccountInfo g = new AccountInfo();
			g.setmUserName("User" + i);
			g.setmUserId(i + 1);
			g.setmUserDescription("Desc" + i);
			g.setmUserImageUrl(null);
			g.setmUserUpdateTime(SystemClock.currentThreadTimeMillis());
			if (i % 2 == 0)
				g.setmUserType("premium");
			else
				g.setmUserType("normal");

			ret.add(g);
		}
		return ret;
	}
}
