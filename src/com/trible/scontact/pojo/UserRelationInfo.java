package com.trible.scontact.pojo;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.reflect.TypeToken;

/**
 * 
 * this class is the middle mapping for users and users
 * 
 */



public class UserRelationInfo extends BaseInfo implements Serializable{

	private static final long serialVersionUID = 1L;
	private Long id;
	private Long followUserId;
	private Long contactId;
	
	private Long userId;
	private String contactIds;
//	List<Long> contactIds;
	
	
	public Long getId() {
		return id;
	}
	public Long getFollowUserId() {
		return followUserId;
	}
	public void setFollowUserId(Long followUserId) {
		this.followUserId = followUserId;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	
	public Long getContactId() {
		return contactId;
	}
	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}
	
	
//	public List<Long> getContactIds() {
//		return contactIds;
//	}
//	public void setContactIds(List<Long> contactIds) {
//		this.contactIds = contactIds;
//	}
	
	@Override
	public Type listType() {
		return new TypeToken<List<UserRelationInfo>>(){}.getType();
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getContactIds() {
		return contactIds;
	}
	public void setContactIds(String contactIds) {
		this.contactIds = contactIds;
	}
}
