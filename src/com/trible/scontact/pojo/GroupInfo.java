package com.trible.scontact.pojo;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.trible.scontact.database.DBConstants;
import com.trible.scontact.database.impl.GroupDaoImpl;
import com.trible.scontact.managers.PrefManager;
import com.trible.scontact.utils.ListUtil;



@DatabaseTable(tableName=DBConstants.GroupFieldName.table_name,daoClass=GroupDaoImpl.class)
public class GroupInfo extends BaseInfo implements Serializable{

	private static final long serialVersionUID = 5992369539881658675L;
	
	@DatabaseField(dataType=DataType.INTEGER_OBJ,columnName=DBConstants.GroupFieldName.group_status)
	private Integer status;
	
	@DatabaseField(dataType=DataType.INTEGER_OBJ,columnName=DBConstants.GroupFieldName.group_capacity)
	Integer capacity;
	
	@DatabaseField(dataType=DataType.INTEGER_OBJ,columnName=DBConstants.GroupFieldName.group_identify)
	Integer identify;
	
	private int groupMembers;
	
	@DatabaseField(dataType=DataType.LONG_OBJ,columnName=DBConstants.GroupFieldName.group_create_time)
	private Long createTime;
	
	@DatabaseField(dataType=DataType.LONG_OBJ,columnName=DBConstants.GroupFieldName.group_update_time)
	Long updateTime;
	
	@DatabaseField(dataType=DataType.LONG_OBJ,columnName=DBConstants.GroupFieldName.id)
	Long id;
	
	@DatabaseField(dataType=DataType.LONG_OBJ,columnName=DBConstants.GroupFieldName.owner_user_id)
	Long ownerId;
	
	@DatabaseField(dataType=DataType.STRING,columnName=DBConstants.GroupFieldName.displayName)
	private String displayName;
	
	@DatabaseField(dataType=DataType.STRING,columnName=DBConstants.GroupFieldName.description)
	String description;
	
	@DatabaseField(dataType=DataType.STRING,columnName=DBConstants.GroupFieldName.type)
	String type;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	
	public Long getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}
	public int getGroupMembers() {
		return groupMembers;
	}
	public void setGroupMembers(int groupMembers) {
		this.groupMembers = groupMembers;
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
	
	
	public Integer getCapacity() {
		return capacity;
	}
	/**
	 * 
	 */
	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}
	
	
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
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
	
	
	public Long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}
	
	
	public Long getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}
	
	
	public Integer getIdentify() {
		return identify;
	}
	/**
	 * 
	 */
	public void setIdentify(Integer identify) {
		this.identify = identify;
	}
	
	@Override
	public boolean equals(Object o) {
		if ( o == null )return false;
		if ( o instanceof GroupInfo ){
			return ((GroupInfo)o).getId().equals(id);
		}
		return super.equals(o);
	}
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	@Override
	public Type listType() {
		return new TypeToken<List<GroupInfo>>(){}.getType();
	}
	
	public static List<GroupInfo> getGroupsFromSpf(){
		PrefManager m = PrefManager.getInstance("groups"+AccountInfo.getInstance().getId() );
		String ason = m.getString("groups");
		List<GroupInfo> infos = new Gson().fromJson(ason,new GroupInfo().listType());
		return infos;
	}
	public static void saveGroupsFromSpf(List<GroupInfo> groups){
		if ( ListUtil.isEmpty(groups) )return;
		PrefManager m = PrefManager.getInstance("groups"+AccountInfo.getInstance().getId() );
		String ason = new Gson().toJson(groups);
		m.putString("groups", ason);
	}
	public List<AccountInfo> getMembersFromSpf(){
		PrefManager m = PrefManager.getInstance(getSpfNameForSaveGroupMem() );
		String ason = m.getString(getDisplayName()+"_"+getId());
		List<AccountInfo> infos = new Gson().fromJson(ason,new AccountInfo().listType());
		return infos;
	}
	
	public void saveMembersToSpf(List<AccountInfo> accounts){
		PrefManager m = PrefManager.getInstance(getSpfNameForSaveGroupMem() );
		String ason = new Gson().toJson(accounts);
		m.putString(getDisplayName()+"_"+getId(), ason);
	}
	private String getSpfNameForSaveGroupMem(){
		return "membersIn_" + getDisplayName() 
				+ "_" + getId()
				+ "_" + AccountInfo.getInstance().getId();
	}
	public static void clearSpf(){
		List<GroupInfo> infos = getGroupsFromSpf();
		if ( ListUtil.isEmpty(infos) )return;
		for ( GroupInfo info : infos ){
			PrefManager.getInstance().deleteFile(info.getSpfNameForSaveGroupMem());
		}
		PrefManager.getInstance().deleteFile("groups"+AccountInfo.getInstance().getId());
	}
	public void deleteSpf(){
		PrefManager.getInstance().deleteFile(getSpfNameForSaveGroupMem());
	}
}
