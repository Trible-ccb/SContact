package com.trible.scontact.components.adpater;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.trible.scontact.R;
import com.trible.scontact.pojo.GroupInfo;

public class GroupsListAdapter extends BaseAdapter {

	Context mContext;
	LayoutInflater mInflater;
	List<GroupInfo> mDatas;
	
	int selectedPos;
	
	public GroupsListAdapter(Context c){
		mContext = c;
		selectedPos = -1;
		mInflater = LayoutInflater.from(mContext);
	}
	public void setData(List<GroupInfo> data){
		mDatas = data;
		selectedPos = -1;
		notifyDataSetChanged();
	}
	public void setSelected(int pos){
		selectedPos = pos;
		notifyDataSetChanged();
	}
	public int getSeleted(){
		return selectedPos;
	}
	public GroupInfo getSeletedGroupInfo(){
		return getGroupInfoInPosition(selectedPos);
	}
	public void addGroup(GroupInfo info){
		if ( mDatas == null )mDatas = new ArrayList<GroupInfo>();
		mDatas.add(info);
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		int size = mDatas == null ? 0 : mDatas.size();
		return (size);
	}

	@Override
	public Object getItem(int position) {
		if ( position < 0 || position >= getCount() )return null;
		return mDatas.get(position);
	}

	public GroupInfo getGroupInfoInPosition(int pos){
		Object o = getItem(pos);
		return (GroupInfo) o;
	}
	@Override
	public long getItemId(int position) {
		return mDatas.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		GroupItemHolder mHolder;
		if ( convertView == null ){
			convertView = mInflater.inflate(R.layout.adapter_draw_group_list_item, null);
			mHolder = new GroupItemHolder();
			mHolder.mGroupName = (TextView) convertView.findViewById(R.id.drawer_group_name);
			mHolder.mGroupNum = (TextView) convertView.findViewById(R.id.drawer_group_number);
			convertView.setTag(mHolder);
		} else {
			mHolder = (GroupItemHolder) convertView.getTag();
		}
		
		GroupInfo info = (GroupInfo) getItem(position);
		mHolder.mGroupName.setText(info.getDisplayName());
		if ( info.getCapacity() == null ){
			mHolder.mGroupNum.setVisibility(View.GONE);
		} else {
			mHolder.mGroupNum.setText(info.getCapacity() + "");
		}
		
		if ( selectedPos == position ){
			convertView.setSelected(true);
			convertView.setBackgroundResource(R.color.blue_qq);
			mHolder.mGroupName.setTextColor(mContext.getResources().getColor(R.color.white));
		} else {
			convertView.setSelected(false);
			convertView.setBackgroundResource(R.drawable.pressor_blue_transparent);
			mHolder.mGroupName.setTextColor(mContext.getResources().getColor(R.color.black));
		}
		return convertView;
	}

	class GroupItemHolder {
		TextView mGroupName,mGroupNum;
	}
}
