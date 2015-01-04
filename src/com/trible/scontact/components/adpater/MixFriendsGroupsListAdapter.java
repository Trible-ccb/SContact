package com.trible.scontact.components.adpater;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.trible.scontact.R;
import com.trible.scontact.components.widgets.EmptyViewHolder;
import com.trible.scontact.components.widgets.EmptyViewHolder.EmptyViewData;
import com.trible.scontact.managers.SDCardManager;
import com.trible.scontact.networks.ListImageAsynTask;
import com.trible.scontact.networks.ListImageAsynTask.ItemImageLoadingListner;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.pojo.GroupInfo;
import com.trible.scontact.utils.DeviceUtil;
import com.trible.scontact.utils.ImageUtil;
import com.trible.scontact.utils.TimeUtil;

public class MixFriendsGroupsListAdapter extends EmptyBaseAdapter {

	Context mContext;
	LayoutInflater mInflater;
	List<Object> mDatas;
	
	public MixFriendsGroupsListAdapter(Context c){
		super(c);
		mContext = c;
		mInflater = LayoutInflater.from(mContext);

	}
	public void setData(List<Object> data){
		mDatas = data;
		notifyDataSetChanged();
	}
	public void clear(){
		if ( mDatas != null )
		mDatas.clear();
		notifyDataSetChanged();
	}
	@Override
	public int getDataCount(){
		return mDatas == null ? 0 : mDatas.size();
	}

	@Override
	public View getDataView(int position, View convertView, ViewGroup parent) {
		Object obj = getItem(position);
		if ( obj instanceof SectionData ){
			return getSectionView(position, convertView, parent, (SectionData)obj);
		} else if ( obj instanceof AVUser ){
			return getFriendView(position, convertView, parent, AVUser.cast((AVUser)obj,AccountInfo.class));
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
		holder.time.setText(TimeUtil.dateToSimpleString(obj.getCreatedAt()));
		holder.img.setImageResource(R.drawable.ic_action_person);
		String url = obj.getPhotoUrl();
		int tw = DeviceUtil.dip2px(mContext, 64);
		int th = DeviceUtil.dip2px(mContext, 64);
		Bitmap bm = ImageUtil.getOptionBitmap(
				SDCardManager.getInstance().getFileOf(
						SDCardManager.PATH_IMAGECACHE, url)
						, tw, th);
		if ( bm != null ){
			holder.img.setImageBitmap(bm);
		} else {
			ListImageAsynTask mImageTask;
			mImageTask = new ListImageAsynTask();
			mImageTask.setmLoadingListner(new ItemImageLoadingListner() {
				@Override
				public void onPreLoad() {
				}
				@Override
				public void onLoadDone(Bitmap doneBm) {
					if ( doneBm != null ){
						notifyDataSetChanged();
					}
				}
			});
			mImageTask.loadBitmapByScaleOfWinWidthForWidget(mContext, url , 1f);
		}
		
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
		holder.time.setText(TimeUtil.dateToSimpleString(obj.getCreatedAt()));
		holder.img.setImageResource(R.drawable.ic_action_group);
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
	@Override
	public Object getDataItem(int position) {
		return mDatas.get(position);
	}
	@Override
	public long getDataId(int position) {
		return position;
	}
}
