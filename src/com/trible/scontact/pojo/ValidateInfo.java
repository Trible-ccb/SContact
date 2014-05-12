package com.trible.scontact.pojo;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.reflect.TypeToken;




public class ValidateInfo extends BaseInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private Long start_user_id;
	private String contact_ids;
	private Long groupId;
	private Long end_user_id;
	private String is_group_to_user;
	
	
	
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	
	public Long getStart_user_id() {
		return start_user_id;
	}
	public void setStart_user_id(Long start_user_id) {
		this.start_user_id = start_user_id;
	}
	
	
	public Long getEnd_user_id() {
		return end_user_id;
	}
	public void setEnd_user_id(Long end_user_id) {
		this.end_user_id = end_user_id;
	}
	
	
	public String getIs_group_to_user() {
		return is_group_to_user;
	}
	public void setIs_group_to_user(String is_group_to_user) {
		this.is_group_to_user = is_group_to_user;
	}
	
	
	public String getContact_ids() {
		return contact_ids;
	}
	public void setContact_ids(String contact_ids) {
		this.contact_ids = contact_ids;
	}
	
	
	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	
	public Type listType() {
		return new TypeToken<List<ValidateInfo>>(){}.getType();
	}
}
