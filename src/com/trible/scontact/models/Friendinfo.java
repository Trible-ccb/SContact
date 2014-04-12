package com.trible.scontact.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import android.os.SystemClock;

@DatabaseTable(tableName = "friendinfo")
public class Friendinfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@DatabaseField(generatedId = true)
	private long mFriendId;
	
	@DatabaseField
	private String mFriendName;
	
	
	@DatabaseField(defaultValue = "")
	private String mFriendNumber;
	
	//A normal level user by default
	@DatabaseField
	private String mFriendType;
	
	@DatabaseField(canBeNull = true)
	private String mFriendImageUrl;
	
	@DatabaseField(canBeNull = true)
	private String mFriendDescription;
	
	@DatabaseField(canBeNull = true)
	private long mFriendUpdateTime;
	
	public String getmFriendImageUrl() {
		return mFriendImageUrl;
	}
	public void setmFriendImageUrl(String mFriendImageUrl) {
		this.mFriendImageUrl = mFriendImageUrl;
	}
	public String getmFriendDescription() {
		return mFriendDescription;
	}
	public void setmFriendDescription(String mFriendDescription) {
		this.mFriendDescription = mFriendDescription;
	}
	public long getmFriendUpdateTime() {
		return mFriendUpdateTime;
	}
	public void setmFriendUpdateTime(long mFriendUpdateTime) {
		this.mFriendUpdateTime = mFriendUpdateTime;
	}
	public long getmFriendId() {
		return mFriendId;
	}
	public void setmFriendId(long mFriendId) {
		this.mFriendId = mFriendId;
	}
	public String getmFriendName() {
		return mFriendName;
	}
	public void setmFriendName(String mFriendName) {
		this.mFriendName = mFriendName;
	}
	public String getmFriendType() {
		return mFriendType;
	}
	public void setmFriendType(String mFriendType) {
		this.mFriendType = mFriendType;
	}
	
	public static List<Friendinfo> getTestData(){
		int count = 20;
		List<Friendinfo> ret = new ArrayList<Friendinfo>();
		
		for ( int i = 0 ; i < count ; i++ ){
			Friendinfo g = new Friendinfo();
			g.setmFriendName("Friend" + i);
			g.setmFriendId(i);
			g.setmFriendDescription("Desc" + i);
			g.setmFriendImageUrl(null);
			g.setmFriendUpdateTime(SystemClock.currentThreadTimeMillis());
			if (i % 2 == 0)
				g.setmFriendType("premium");
			else
				g.setmFriendType("normal");

			ret.add(g);
		}
		return ret;
	}
	public String getmFriendNumber() {
		return mFriendNumber;
	}
	public String[] getmFriendNumbers() {
		String[] t = {};
		if ( mFriendNumber != null ){
			t = mFriendNumber.split("\\|");
		}
		return t;
	}
	public void setmFriendNumber(String mFriendNumber) {
		this.mFriendNumber = mFriendNumber;
	}
}
