package com.trible.scontact.pojo;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.reflect.TypeToken;




public class GroupInfo extends BaseInfo implements Serializable{

	private static final long serialVersionUID = 5992369539881658675L;
	private Integer status,capacity,identify;

	private Long createTime,updateTime,id,ownerId;
	private String displayName,description,type;

	
	
	
	
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
	public Type listType() {
		return new TypeToken<List<GroupInfo>>(){}.getType();
	}
}
