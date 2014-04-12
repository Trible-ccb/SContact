package com.trible.scontact.components.adpater;

import java.util.ArrayList;
import java.util.List;

import com.trible.scontact.R;
import com.trible.scontact.models.Groupsinfo;
import com.trible.scontact.pojo.GroupInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GroupsListAdapter extends BaseAdapter {

	Context mContext;
	LayoutInflater mInflater;
	List<GroupInfo> mDatas;
	
	public GroupsListAdapter(Context c){
		mContext = c;
		mInflater = LayoutInflater.from(mContext);
	}
	public void setData(List<GroupInfo> data){
		mDatas = data;
		notifyDataSetChanged();
	}
	public void addGroup(GroupInfo info){
		if ( mDatas == null )mDatas = new ArrayList<GroupInfo>();
		mDatas.add(info);
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
		return mDatas.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		GroupItemHolder mHolder;
		if ( convertView == null ){
			convertView = mInflater.inflate(R.layout.draw_group_list_item, null);
			mHolder = new GroupItemHolder();
			mHolder.mGroupName = (TextView) convertView.findViewById(R.id.drawer_group_name);
			mHolder.mGroupNum = (TextView) convertView.findViewById(R.id.drawer_group_number);
			convertView.setTag(mHolder);
		} else {
			mHolder = (GroupItemHolder) convertView.getTag();
		}
		GroupInfo info = mDatas.get(position);
		mHolder.mGroupName.setText(info.getDisplayName());
		mHolder.mGroupNum.setText(info.getCapacity() + "");
		return convertView;
	}

	class GroupItemHolder {
		TextView mGroupName,mGroupNum;
	}
}
