package com.trible.scontact.pojo;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.table.DatabaseTable;
import com.trible.scontact.database.DBConstants;
import com.trible.scontact.database.impl.GroupDaoImpl;
import com.trible.scontact.managers.PrefManager;
import com.trible.scontact.utils.ListUtil;


@AVClassName(GroupInfo.FieldName.TABLE_NAME)
@DatabaseTable(tableName=DBConstants.GroupFieldName.table_name,daoClass=GroupDaoImpl.class)
public class GroupInfo extends AVObject implements Serializable{

	private static final long serialVersionUID = 5992369539881658675L;
	
//	@DatabaseField(dataType=DataType.INTEGER_OBJ,columnName=DBConstants.GroupFieldName.group_status)
//	private Integer status;
//	
//	@DatabaseField(dataType=DataType.INTEGER_OBJ,columnName=DBConstants.GroupFieldName.group_capacity)
//	Integer capacity;
//	
//	@DatabaseField(dataType=DataType.INTEGER_OBJ,columnName=DBConstants.GroupFieldName.group_identify)
//	Integer identify;
	
	private int groupMembers;
	
//	@DatabaseField(dataType=DataType.LONG_OBJ,columnName=DBConstants.GroupFieldName.group_create_time)
//	private Long createTime;
	
//	@DatabaseField(dataType=DataType.LONG_OBJ,columnName=DBConstants.GroupFieldName.group_update_time)
//	Long updateTime;
	
//	@DatabaseField(dataType=DataType.LONG_OBJ,columnName=DBConstants.GroupFieldName.id)
//	Long id;
//	
//	@DatabaseField(dataType=DataType.LONG_OBJ,columnName=DBConstants.GroupFieldName.owner_user_id)
//	Long ownerId;
	
//	@DatabaseField(dataType=DataType.STRING,columnName=DBConstants.GroupFieldName.displayName)
//	private String displayName;
	
//	@DatabaseField(dataType=DataType.STRING,columnName=DBConstants.GroupFieldName.description)
//	String description;
	
//	@DatabaseField(dataType=DataType.STRING,columnName=DBConstants.GroupFieldName.type)
//	String type;
	
	public String getId() {
		return getObjectId();
	}
	public void setId(String id) {
		setObjectId(id);
	}
	public AccountInfo getOwner(){
		return getAVUser(FieldName.OWNER, AccountInfo.class);
	}
	public void setOwner(AccountInfo account){
		put(FieldName.OWNER, account);
	}
	public int getGroupMembers() {
		return groupMembers;
	}
	public void setGroupMembers(int groupMembers) {
		this.groupMembers = groupMembers;
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
	
	
	public Integer getCapacity() {
		return getInt(FieldName.CAPACITY);
	}
	/**
	 * 
	 */
	public void setCapacity(Integer capacity) {
		put(FieldName.CAPACITY,capacity);
	}
	
	
	public String getDisplayName() {
		return getString(FieldName.NAME);
	}
	public void setDisplayName(String displayName) {
		put(FieldName.NAME, displayName);
	}
	
	public String getDescription() {
		return getString(FieldName.DESCRIPTION);
	}
	public void setDescription(String description) {
		put(FieldName.DESCRIPTION, description);
	}
	public String getPinyinName() {
		return getString(FieldName.PINYINNAME);
	}
	public void setPinyinName(String pinyin) {
		put(FieldName.PINYINNAME, pinyin);
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
	
	public Integer getIdentify() {
		return getInt(FieldName.IDENTIFY);
	}
	/**
	 * 
	 */
	public void setIdentify(Integer identify) {
		put(FieldName.IDENTIFY, identify);
	}
	
	@Override
	public boolean equals(Object o) {
		if ( o == null )return false;
		if ( o instanceof GroupInfo ){
			return ((GroupInfo)o).getId().equals(getId());
		}
		return super.equals(o);
	}
	@Override
	public int hashCode() {
		return getId().hashCode();
	}
	public Type listType() {
		return new TypeToken<List<GroupInfo>>(){}.getType();
	}
	
	public static List<GroupInfo> getGroupsFromSpf(){
//		PrefManager m = PrefManager.getInstance("groups"+AccountInfo.getInstance().getId() );
//		String ason = m.getString("groups");
//		List<GroupInfo> infos = new Gson().fromJson(ason,new GroupInfo().listType());
//		return infos;
		return null;
	}
	public static void saveGroupsFromSpf(List<GroupInfo> groups){
		if ( ListUtil.isEmpty(groups) )return;
//		PrefManager m = PrefManager.getInstance("groups"+AccountInfo.getInstance().getId() );
//		String ason = new Gson().toJson(groups);
//		m.putString("groups", ason);
	}
	public List<AccountInfo> getMembersFromSpf(){
//		PrefManager m = PrefManager.getInstance(getSpfNameForSaveGroupMem() );
//		String ason = m.getString(getDisplayName()+"_"+getId());
//		List<AccountInfo> infos = new Gson().fromJson(ason,new AccountInfo().listType());
//		return infos;
		return null;
	}
	
	public void saveMembersToSpf(List<AccountInfo> accounts){
//		PrefManager m = PrefManager.getInstance(getSpfNameForSaveGroupMem() );
//		String ason = new Gson().toJson(accounts);
//		m.putString(getDisplayName()+"_"+getId(), ason);
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
	public interface FieldName{
		String TABLE_NAME = "Group_infos";
		String NAME = "group_name"; //群组名字
		String CAPACITY = "capacity";//群组容量
		String STATUS = "status";//群状态
		String IDENTIFY = "identify";//加群验证方式
		String PHOTOURL = "photo_url";//群头像
		String TYPE = "type";//群类型
		String DESCRIPTION = "description";//群描述
		String PINYINNAME = "pinyinname";//群组名字的拼音
		String OWNER = "creator";//群创建者
	}
}
