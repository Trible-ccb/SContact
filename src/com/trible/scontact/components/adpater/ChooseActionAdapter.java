package com.trible.scontact.components.adpater;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.trible.scontact.R;
import com.trible.scontact.value.GlobalValue;

public class ChooseActionAdapter extends BaseAdapter{

	List<String> mActions;
	Context mContext;
	LayoutInflater mInflater;
	
	public ChooseActionAdapter(Context c) {
		mContext = c;
		mInflater = LayoutInflater.from(mContext);
	}
	
	public void setDatas(List<String> actions){
		mActions = actions;
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		return mActions == null ? 0 : mActions.size();
	}

	@Override
	public Object getItem(int position) {
		return mActions.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if ( convertView == null ){
			convertView = mInflater.inflate(R.layout.adapter_choose_action_item, null);
		}
		TextView ctv = (TextView) convertView;
		ctv.setText((String)getItem(position));
		return convertView;
	}

}
