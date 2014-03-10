package com.trible.scontact.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import android.os.SystemClock;

@DatabaseTable (tableName = "user_group_info")
public class UserMixGroup implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@DatabaseField (generatedId = true)
	private int mId;
	
	
	@DatabaseField (generatedId = true,throwIfNull=true)
	private int mGroupId;
	
	@DatabaseField (generatedId = true,throwIfNull=true)
	private int mFriendId;
	
	public int getmId() {
		return mId;
	}

	public void setmId(int mId) {
		this.mId = mId;
	}

	public int getmGroupId() {
		return mGroupId;
	}

	public void setmGroupId(int mGroupId) {
		this.mGroupId = mGroupId;
	}

	public int getmFriendId() {
		return mFriendId;
	}

	public void setmFriendId(int mFriendId) {
		this.mFriendId = mFriendId;
	}

	public static List<UserMixGroup> getTestData(){
		int count = 20;
		List<UserMixGroup> ret = new ArrayList<UserMixGroup>();
		return ret;
	}
}
