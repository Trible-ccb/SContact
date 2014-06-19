package com.trible.scontact.components.adpater;

import android.content.Context;

import com.trible.scontact.pojo.ContactTypes;

public abstract class TypeHandler {
	Context mContext;
	public TypeHandler(Context c){
		mContext = c;
	}
	public void handle(String type){
		if ( ContactTypes.getInstance().getCellPhoneType().equals(type) 
				|| ContactTypes.getInstance().getTelephoneType().equals(type) ){
			onPhone();
		} else if ( ContactTypes.getInstance().getEmailType().equals(type) ){
			onEmail();
		} else if ( ContactTypes.getInstance().getCustomType().equals(type) ){
			onCustom();
		}
	}
	protected void onPhone(){
	}
	protected void onEmail(){
	}
	protected void onCustom(){
	}
}
