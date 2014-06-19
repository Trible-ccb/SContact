package com.trible.scontact.pojo;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trible.scontact.R;
import com.trible.scontact.managers.PrefManager;

import android.content.Context;

public class ContactTypes {

	String[] types;
	//key:key of type,value:display name of type
	Map<String, String> mTypes;
	
	static Context mContext;
	static ContactTypes mInstance;
	
	public static void init(Context c){
		mContext = c;
		mInstance = getInstance();
		mInstance.mTypes = mInstance.getFromTypes();
		if ( mInstance.mTypes == null 
				|| mInstance.mTypes.size() == 0 ){
			mInstance.initWithDefault();
		}
	}
	public void saveToSpf(){
		mTypes.remove("custom");
		mTypes.put("custom", mContext.getString(R.string.custom));
		PrefManager.getInstance("Types")
		.putString("types", new Gson().toJson(mTypes));
	}
	protected Map<String, String> getFromTypes(){
		TypeToken<Map<String, String>> t =  new TypeToken<Map<String, String>>(){};
		Map<String, String> mTypes = GsonHelper
				.getInfosFromJson(
						PrefManager.getInstance("Types").getString("types"),
						t.getType());
		return mTypes;
	}
	private void initWithDefault(){
		mTypes = new LinkedHashMap<String, String>();
		mTypes.put("cellphone", mContext.getString(R.string.cellphone));
		mTypes.put("telephone", mContext.getString(R.string.telephone));
		mTypes.put("email", mContext.getString(R.string.email));
		mTypes.put("custom", mContext.getString(R.string.custom));
	}
	private ContactTypes(){};
	public static ContactTypes getInstance(){
		if ( mContext == null ){
			throw new IllegalAccessError();
		}
		if ( mInstance == null ){
			mInstance = new ContactTypes();
		}
		return mInstance;
	}
//	public String[] getTypesKey(){
//		String[] ret = new String[mTypes.size()];
//		mTypes.keySet().toArray(ret);
//		return ret;
//	}
	public String[] getTypesValue(){
		mTypes.remove("custom");
		mTypes.put("custom", mContext.getString(R.string.custom));
		String[] ret = new String[mTypes.size()];
		mTypes.values().toArray(ret);
		return ret;
	}
	public String getCellPhoneType(){
		return mTypes.get("cellphone");
	}
	public String getTelephoneType(){
		return mTypes.get("telephone");
	}
	public String getEmailType(){
		return mTypes.get("email");
	}
	public String getCustomType(){
		return mTypes.get("custom");
	}
	public void addType(String type){
		mTypes.put(type, type);
		saveToSpf();
	}
	
}
