package com.trible.scontact.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="groupsinfo")
public class Groupsinfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int mGroupId;
	private String mGroupName;
	private int mInGroupNumber;
	private int mGroupCapacity;
	private String mGroupType;
	
	public int getmGroupId() {
		return mGroupId;
	}
	public void setmGroupId(int mGroupId) {
		this.mGroupId = mGroupId;
	}
	public String getmGroupName() {
		return mGroupName;
	}
	public void setmGroupName(String mGroupName) {
		this.mGroupName = mGroupName;
	}
	public int getmInGroupNumber() {
		return mInGroupNumber;
	}
	public void setmInGroupNumber(int mInGroupNumber) {
		this.mInGroupNumber = mInGroupNumber;
	}
	public int getmGroupCapacity() {
		return mGroupCapacity;
	}
	public void setmGroupCapacity(int mGroupCapacity) {
		this.mGroupCapacity = mGroupCapacity;
	}
	public String getmGroupType() {
		return mGroupType;
	}
	public void setmGroupType(String mGroupType) {
		this.mGroupType = mGroupType;
	}
	
	public static List<Groupsinfo> getTestData(){
		int count = 20;
		List<Groupsinfo> ret = new ArrayList<Groupsinfo>();
		
		for ( int i = 0 ; i < count ; i++ ){
			Groupsinfo g = new Groupsinfo();
			g.setmGroupCapacity(100 + i);
			g.setmGroupName("Group" + i);
			g.setmGroupType("premium");
			g.setmGroupId(i);
			g.setmInGroupNumber(20 + i);
			ret.add(g);
		}
		return ret;
	}
}
