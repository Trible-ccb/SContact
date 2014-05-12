package com.trible.scontact.components.adpater;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.trible.scontact.R;
import com.trible.scontact.components.widgets.PropertyKeyValue;
import com.trible.scontact.pojo.ContactInfo;

public class FriendContactsListAdapter extends BaseAdapter {

	Context mContext;
	LayoutInflater mInflater;
	List<ContactInfo> mDatas;
	
	boolean selectAll;
	
	public boolean isSelectAll() {
		return selectAll;
	}
	public void setSelectAll(boolean selectAll) {
		this.selectAll = selectAll;
	}
	public FriendContactsListAdapter(Context c){
		mContext = c;
		mInflater = LayoutInflater.from(mContext);
		selectAll = true;
	}
	public void setData(List<ContactInfo> data){
		mDatas = data;
		notifyDataSetChanged();
	}
	public void addData(ContactInfo data){
		if ( data == null || mDatas.contains(data) )return;
		for ( ContactInfo info : mDatas ){
			if ( info.getId().equals(data.getId()) ){
				return;
			}
		}
		mDatas.add(data);
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		return mDatas == null ? 0 : mDatas.size();
	}

	@Override
	public Object getItem(int position) {
		if ( position < 0 || position >= getCount() )return null;
		return mDatas.get(position);
	}
	
	public ContactInfo getContact(int position) {
		return (ContactInfo) getItem(position);
	}
	@Override
	public long getItemId(int position) {
		ContactInfo c = getContact(position);
		return c == null ? -1 : c.getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final PropertyKeyValue mHolder;
		final int pos = position;
		if ( convertView == null ){
			convertView = mInflater.inflate(R.layout.layout_property_key_value, null);
			mHolder = new PropertyKeyValue(convertView);
			convertView.setTag(mHolder);
		} else {
			mHolder = (PropertyKeyValue) convertView.getTag();
		}
		final ContactInfo info = mDatas.get(pos);
		mHolder.setValueText(info.getContact());
		String type = "Unkown";
		if ( !TextUtils.isEmpty(info.getType() )){
			type = info.getType();
		}
		mHolder.setKeyText(type);
		return convertView;
	}

	class ContactViewHolder {
		TextView mContactName,mContactType;
	}
	
}
