package com.trible.scontact.components.adpater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.trible.scontact.R;
import com.trible.scontact.value.GlobalValue;

public class ContactTypeSpinnerAdapter extends BaseAdapter{

	String[] mTypes;
	Context mContext;
	LayoutInflater mInflater;
	
	public ContactTypeSpinnerAdapter(Context c) {
		mContext = c;
		mTypes = GlobalValue.CONTACT_TYPES;
		mInflater = LayoutInflater.from(mContext);
	}
	@Override
	public int getCount() {
		return mTypes == null ? 0 : mTypes.length;
	}

	@Override
	public Object getItem(int position) {
		return mTypes[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if ( convertView == null ){
			convertView = mInflater.inflate(R.layout.adapter_contact_type_spinner_item, null);
		}
		TextView ctv = (TextView) convertView;
		ctv.setText(mTypes[position]);
		return convertView;
	}

}
