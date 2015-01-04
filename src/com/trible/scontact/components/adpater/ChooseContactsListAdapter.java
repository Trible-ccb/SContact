package com.trible.scontact.components.adpater;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
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

public class ChooseContactsListAdapter extends BaseAdapter {

	Context mContext;
	LayoutInflater mInflater;
	List<ContactInfo> mDatas;
	Set<ContactInfo> mCheckedData;
	
	boolean selectAll;
	
	public boolean isSelectAll() {
		return selectAll;
	}
	public void setSelectAll(boolean selectAll) {
		this.selectAll = selectAll;
	}
	public ChooseContactsListAdapter(Context c){
		mContext = c;
		mCheckedData = new HashSet<ContactInfo>();
		mInflater = LayoutInflater.from(mContext);
		selectAll = true;
	}
	public void setData(List<ContactInfo> data){
		mDatas = data;
		notifyDataSetChanged();
	}
	public void setSelectedData(List<ContactInfo> datas){
		if ( datas == null || mDatas == null )return;
		for ( ContactInfo info : datas ){
			for ( ContactInfo da : mDatas ){
				if ( da.getId().equals(info.getId()) ){
					mCheckedData.add(da);
					continue;
				}
			}
		}
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
//		return mDatas.get(position).getId();
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ContactViewHolder mHolder;
		final int pos = position;
		if ( convertView == null ){
			convertView = mInflater.inflate(R.layout.adapter_select_contacts_list_item, null);
			mHolder = new ContactViewHolder();
			mHolder.mContactName = (TextView) convertView.findViewById(R.id.contact_name);
			mHolder.check = (CheckBox) convertView.findViewById(R.id.check_contact);
			convertView.setTag(mHolder);
		} else {
			mHolder = (ContactViewHolder) convertView.getTag();
		}
		final ContactInfo info = mDatas.get(pos);
		mHolder.mContactName.setText(info.getContact());
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mHolder.check.toggle();
			}
		});
		mHolder.check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if ( isChecked ){
					mCheckedData.add(info);
				} else {
					mCheckedData.remove(info);
				}
				selectAll = false;
				notifyDataSetChanged();
			}
		});
		if ( selectAll ){
			mHolder.check.setChecked(true);
		}
		if ( mCheckedData.contains(info) ){
			mHolder.check.setChecked(true);
		} else {
			mHolder.check.setChecked(false);
		}
		return convertView;
	}

	class ContactViewHolder {
		TextView mContactName;
		CheckBox check;
	}
	
	public List<ContactInfo> getCheckedContactInfos(){
		List<ContactInfo> ret = new ArrayList<ContactInfo>();
		for ( ContactInfo i : mCheckedData ){
			ret.add(i);
		}
		return ret;
	}
}
