package com.trible.scontact.pojo;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.reflect.TypeToken;




public class ContactInfo extends BaseInfo implements Serializable{
	private static final long serialVersionUID = 2279560755875633905L;

	private Long id;
	private Long userId;
	private String contact;
	private Integer status;
	private Long lastestUsedTime;
	
	

	
	
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
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
	
	public Long getLastestUsedTime() {
		return lastestUsedTime;
	}
	public void setLastestUsedTime(Long lastestUsedTime) {
		this.lastestUsedTime = lastestUsedTime;
	}
	
	@Override
	public Type listType() {
		return new TypeToken<List<ContactInfo>>(){}.getType();
	}
}
