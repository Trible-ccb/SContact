package com.trible.scontact.components.adpater;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.trible.scontact.R;
import com.trible.scontact.components.adpater.SearchResultAdapter.FriendViewHolder;
import com.trible.scontact.components.adpater.SearchResultAdapter.GroupViewHolder;
import com.trible.scontact.components.adpater.SearchResultAdapter.SectionData;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.pojo.ContactInfo;
import com.trible.scontact.pojo.GroupInfo;
import com.trible.scontact.utils.TimeUtil;

public class MixFriendsGroupsListAdapter extends BaseAdapter {

	Context mContext;
	LayoutInflater mInflater;
	List<Object> mDatas;
	
	public MixFriendsGroupsListAdapter(Context c){
		mContext = c;
		mInflater = LayoutInflater.from(mContext);
	}
	public void setData(List<Object> data){
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
}
