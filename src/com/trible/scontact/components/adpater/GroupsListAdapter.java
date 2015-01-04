package com.trible.scontact.components.adpater;

import java.security.acl.Owner;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.trible.scontact.R;
import com.trible.scontact.networks.NetWorkEvent;
import com.trible.scontact.networks.SimpleAsynTask;
import com.trible.scontact.networks.SimpleAsynTask.AsynTaskListner;
import com.trible.scontact.pojo.AccountInfo;
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
		
		SimpleAsynTask.doTask2(new AsynTaskListner() {
			@Override
			public void onTaskDone(NetWorkEvent event) {
				
			}
			@Override
			public void doInBackground() {
				List<GroupInfo> tmp = GroupInfo.getGroupsFromSpf();
				if ( mDatas != null && tmp != null ){
					HashSet<String> nowids = new HashSet<String>();
					for ( GroupInfo g : mDatas ){
						nowids.add(g.getId());
					}
					for ( GroupInfo i : tmp ){
						if ( !nowids.contains(i.getId()) ){
							i.deleteSpf();
						}
					}
				}
				GroupInfo.saveGroupsFromSpf(mDatas);
			}
		});
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
	public boolean editedGroupBy(GroupInfo info){
		if ( info == null )return false;
		for ( int i = 0 ; i < getCount() ; i++){
			if ( info.getId().equals(getGroupInfoInPosition(i).getId()) ){
				mDatas.set(i, info);
				notifyDataSetChanged();
				return true;
			}
		}
		return false;
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
		if ( position >= getCount() )return Integer.MIN_VALUE;
//		return mDatas.get(position).getId();
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		GroupItemHolder mHolder;
		if ( convertView == null ){
			convertView = mInflater.inflate(R.layout.adapter_draw_group_list_item, null);
			mHolder = new GroupItemHolder();
			mHolder.mGroupName = (TextView) convertView.findViewById(R.id.drawer_group_name);
			mHolder.mGroupNum = (TextView) convertView.findViewById(R.id.drawer_group_number);
			mHolder.mGroupColor = (TextView) convertView.findViewById(R.id.drawer_group_color);
			convertView.setTag(mHolder);
		} else {
			mHolder = (GroupItemHolder) convertView.getTag();
		}
		
		GroupInfo info = (GroupInfo) getItem(position);
		AccountInfo owner = info.getOwner();
		mHolder.mGroupName.setText(info.getDisplayName());
		mHolder.mGroupNum.setVisibility(View.GONE);
		if ( owner == null ){
			mHolder.mGroupColor.setBackgroundResource(R.color.yellow);
		} else if ( AccountInfo.getInstance().getId().equals(info.getOwner().getId()) ){
			mHolder.mGroupColor.setBackgroundResource(R.color.pink);
		} else {
			mHolder.mGroupColor.setBackgroundResource(R.color.green);
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
		TextView mGroupName,mGroupNum,mGroupColor;
	}
}
