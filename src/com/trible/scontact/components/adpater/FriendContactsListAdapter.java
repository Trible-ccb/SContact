package com.trible.scontact.components.adpater;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.trible.scontact.R;
import com.trible.scontact.components.activity.SContactMainActivity;
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
	@Override
	public int getCount() {
		return mDatas == null ? 0 : mDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ContactViewHolder mHolder;
		final int pos = position;
		if ( convertView == null ){
			convertView = mInflater.inflate(R.layout.adapter_view_contacts_list_item, null);
			mHolder = new ContactViewHolder();
			mHolder.mContactName = (TextView) convertView.findViewById(R.id.contact_name);
			mHolder.mContactType = (TextView) convertView.findViewById(R.id.contact_type);
			convertView.setTag(mHolder);
		} else {
			mHolder = (ContactViewHolder) convertView.getTag();
		}
		final ContactInfo info = mDatas.get(pos);
		mHolder.mContactName.setText(info.getContact());
		String type = "Unkown Type";
		if ( !TextUtils.isEmpty(info.getType() )){
			type = info.getType();
		}
		mHolder.mContactType.setText(type);
		return convertView;
	}

	class ContactViewHolder {
		TextView mContactName,mContactType;
	}
	
}
