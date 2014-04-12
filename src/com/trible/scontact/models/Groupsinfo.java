package com.trible.scontact.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@Deprecated
@DatabaseTable(tableName="groupsinfo")
public class Groupsinfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@DatabaseField(generatedId = true)
	private Long groupId;
	private String mGroupName;
	private int mInGroupNumber;
	private int mGroupCapacity;
	private String mGroupType;
	private boolean isPublic;
	private int mVerifyType;
	private long createTime;
	
	@DatabaseField(foreign = true,foreignAutoCreate = true)
	private ForeignCollection<Friendinfo> owner;
	
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public ForeignCollection<Friendinfo> getOwner() {
		return owner;
	}
	public void setOwner(ForeignCollection<Friendinfo> owner) {
		this.owner = owner;
	}
	public boolean isPublic() {
		return isPublic;
	}
	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}
	public int getmVerifyType() {
		return mVerifyType;
	}
	public void setmVerifyType(int mVerifyType) {
		this.mVerifyType = mVerifyType;
	}
	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
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
			g.setGroupId(Long.valueOf(i));
			g.setmInGroupNumber(20 + i);
			ret.add(g);
		}
		return ret;
	}
	
	public enum VerifyType{
		kVerifyType_Anyone,
		kVerifyType_Indentified,
		kVerifyTYpe_None
	}
}
