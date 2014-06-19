package com.trible.scontact.components.adpater;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.trible.scontact.R;
import com.trible.scontact.managers.SDCardManager;
import com.trible.scontact.networks.ListImageAsynTask;
import com.trible.scontact.networks.ListImageAsynTask.ItemImageLoadingListner;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.utils.DeviceUtil;
import com.trible.scontact.utils.ImageUtil;
import com.trible.scontact.utils.TimeUtil;

public class FriendsListAdapter extends EmptyBaseAdapter {

	Context mContext;
	LayoutInflater mInflater;
	List<AccountInfo> mDatas;

	
	public boolean mUsePhoto;
	
	public FriendsListAdapter(Context c){
		super(c);
		mContext = c;
		mUsePhoto = true;
		mInflater = LayoutInflater.from(mContext);

	}
	public void setData(List<AccountInfo> data){
		mDatas = data;
		notifyDataSetChanged();
	}
	
	@Override
	public View getDataView(int position, View convertView, ViewGroup parent) {
		UserItemHolder mHolder;
		if ( convertView == null  || !(convertView.getTag() instanceof UserItemHolder)){
			convertView = mInflater.inflate(R.layout.adapter_friends_list_item, null);
			mHolder = new UserItemHolder();
			mHolder.mUserName = (TextView) convertView.findViewById(R.id.users_list_item_name);
			mHolder.mUserDesc = (TextView) convertView.findViewById(R.id.users_list_item_desc);
			mHolder.mUserImage = (ImageView) convertView.findViewById(R.id.users_list_item_image);
			mHolder.mUserUpdateTime = (TextView) convertView.findViewById(R.id.users_list_item_time);
			convertView.setTag(mHolder);
		} else {
			mHolder = (UserItemHolder) convertView.getTag();
		}
		AccountInfo info = mDatas.get(position);
		mHolder.mUserName.setText(info.getDisplayName());
		if ( mUsePhoto ){
			mHolder.mUserImage.setVisibility(View.VISIBLE);
		} else {
			mHolder.mUserImage.setVisibility(View.GONE);
		}
		mHolder.mUserImage.setImageResource(R.drawable.ic_action_person);
		int tw = DeviceUtil.dip2px(mContext, 64);
		int th = DeviceUtil.dip2px(mContext, 64);
		String url = info.getPhotoUrl();
			Bitmap bm = ImageUtil.getOptionBitmap(
					SDCardManager.getInstance().getFileOf(
							SDCardManager.PATH_IMAGECACHE, url)
							, tw, th);
		if ( bm != null ){
			mHolder.mUserImage.setImageBitmap(bm);
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
		String desc = info.getDescription();
		if ( desc == null ){
//			desc = ContactInfo.arrayToString(info.getContactsList());
		}
		mHolder.mUserDesc.setText(desc);
		Long time = info.getCreateTime();
		if ( time != null ){
			mHolder.mUserUpdateTime.setVisibility(View.VISIBLE);
			mHolder.mUserUpdateTime.setText(
					TimeUtil.toTimeString(info.getCreateTime()));
		} else {
			mHolder.mUserUpdateTime.setVisibility(View.GONE);
		}

		
		return convertView;
	}

	class UserItemHolder {
		TextView mUserName,mUserDesc,mUserUpdateTime;
		ImageView mUserImage;
		
	}

	@Override
	public int getDataCount() {
		return mDatas == null ? 0 : mDatas.size();
	}
	@Override
	public Object getDataItem(int position) {
		return mDatas.get(position);
	}
	@Override
	public long getDataId(int position) {
		return mDatas.get(position).getId();
	}
}
