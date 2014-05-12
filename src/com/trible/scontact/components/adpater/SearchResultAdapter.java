package com.trible.scontact.components.adpater;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.trible.scontact.R;
import com.trible.scontact.models.Friendinfo;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.pojo.GroupInfo;
import com.trible.scontact.utils.ListUtil;
import com.trible.scontact.utils.TimeUtil;

public class SearchResultAdapter extends BaseAdapter {

	Context mContext;
	LayoutInflater mInflater;
	List<Object> mGroupListData,mFriendListData,mCurrentDisplayData;
	
	public SearchResultAdapter(Context c){
		mContext = c;
		mFriendListData = new ArrayList<Object>();
		mGroupListData = new ArrayList<Object>();
		mCurrentDisplayData = mFriendListData;
		mInflater = LayoutInflater.from(mContext);
	}

	public void addGroupSection(SectionData sdata,List<GroupInfo> belongToSectionData){
		if ( ListUtil.isEmpty(belongToSectionData) ){
			sdata.visiable = false;
		} else {
			sdata.visiable = true;
			mGroupListData.add(sdata);
			mGroupListData.addAll(belongToSectionData);
			notifyDataSetChanged();
		}
	}
	
	public void addFriendSection(SectionData sdata,List<AccountInfo> belongToSectionData){
		if ( ListUtil.isEmpty(belongToSectionData) ){
			sdata.visiable = false;
		} else {
			sdata.visiable = true;
			mFriendListData.add(sdata);
			mFriendListData.addAll(belongToSectionData);
			notifyDataSetChanged();
		}
	}
	
	public void displayGroup(){
		mCurrentDisplayData = mGroupListData;
		notifyDataSetChanged();
	}
	
	public void displayFriend(){
		mCurrentDisplayData = mFriendListData;
		notifyDataSetChanged();
	}
	
	public void clear(){
		mGroupListData.clear();
		mFriendListData.clear();
		mCurrentDisplayData.clear();
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		return mCurrentDisplayData == null ? 0 : mCurrentDisplayData.size();
	}
	@Override
	public Object getItem(int position) {
		return mCurrentDisplayData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Object obj = getItem(position);
		if ( obj instanceof SectionData ){
			return getSectionView(position, convertView, parent, (SectionData)obj);
		} else if ( obj instanceof AccountInfo ){
			return getFriendView(position, convertView, parent, (AccountInfo)obj);
		} else if ( obj instanceof GroupInfo ){
			return getGroupView(position, convertView, parent, (GroupInfo)obj);
		} else {
			throw new UnknownError("unsupport item view obj is " + obj.getClass().getSimpleName());
		}
	}
	
	public View getSectionView(int position, View convertView, ViewGroup parent,SectionData data){
		convertView = mInflater.inflate(R.layout.adapter_search_result_section_item, null);
		TextView name = (TextView) convertView.findViewById(R.id.section_name);
		name.setText(data.sectionName);
		return convertView;
	}
	
	public View getFriendView(int position, View convertView, ViewGroup parent,AccountInfo obj){
		FriendViewHolder holder;
		if ( convertView == null 
				|| !(convertView.getTag() instanceof FriendViewHolder) ){
			convertView = mInflater.inflate(R.layout.adapter_friends_list_item, null);
			holder = new FriendViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (FriendViewHolder) convertView.getTag();
		}
		holder.desp.setText(obj.getDescription());
		holder.title.setText(obj.getDisplayName());
		holder.time.setText(TimeUtil.toTimeString(obj.getCreateTime()));
		return convertView;
	}
	
	public View getGroupView(int position, View convertView, ViewGroup parent,GroupInfo obj){
		GroupViewHolder holder;
		if ( convertView == null 
				|| !(convertView.getTag() instanceof GroupViewHolder) ){
			convertView = mInflater.inflate(R.layout.adapter_friends_list_item, null);
			holder = new GroupViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (GroupViewHolder) convertView.getTag();
		}
		holder.desp.setText(obj.getDescription());
		holder.title.setText(obj.getDisplayName());
		holder.time.setText(TimeUtil.toTimeString(obj.getCreateTime()));
		return convertView;
	}
	
	public static class FriendViewHolder{
		public ImageView img;
		public TextView title,time,desp;
		public FriendViewHolder(View convertView){
			img = (ImageView) convertView.findViewById(R.id.users_list_item_image);
			title = (TextView) convertView.findViewById(R.id.users_list_item_name);
			time = (TextView) convertView.findViewById(R.id.users_list_item_time);
			desp = (TextView) convertView.findViewById(R.id.users_list_item_desc);
		}
	}
	public static class GroupViewHolder{
		public ImageView img;
		public TextView title,time,desp;
		public GroupViewHolder(View convertView){
			img = (ImageView) convertView.findViewById(R.id.users_list_item_image);
			title = (TextView) convertView.findViewById(R.id.users_list_item_name);
			time = (TextView) convertView.findViewById(R.id.users_list_item_time);
			desp = (TextView) convertView.findViewById(R.id.users_list_item_desc);
		}
	}
	public static class SectionData{
		public String sectionName;
		public boolean visiable;
		public SectionData(String sName){
			sectionName = sName;
		}
	}
}
