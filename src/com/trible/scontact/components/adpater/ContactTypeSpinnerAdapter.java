package com.trible.scontact.components.adpater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.trible.scontact.R;
import com.trible.scontact.pojo.ContactTypes;

public class ContactTypeSpinnerAdapter extends BaseAdapter{

	String[] mTypes;
	Context mContext;
	LayoutInflater mInflater;
	Spinner mSpinner;
	
	public ContactTypeSpinnerAdapter(Spinner s) {
		mContext = s.getContext();
		mSpinner = s;
		mInflater = LayoutInflater.from(mContext);
		refresh();
	}
	public void refresh(){
		mTypes = ContactTypes.getInstance().getTypesValue();
		notifyDataSetChanged();
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

	public String getSelected(){
		return ContactTypes.getInstance().getTypeByTypeValue(mTypes[mSpinner.getSelectedItemPosition()]);
	}
	public void setSelected(int pos){
		mSpinner.setSelection(pos);
	}
	public void setSelectedType(String type){
		for ( int i = 0 ; i < getCount() ; i++ ){
			if ( mTypes[i].equals(type) ){
				mSpinner.setSelection(i);
				break;
			}
		}
		
	}
}
