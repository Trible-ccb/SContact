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
import com.trible.scontact.pojo.AccountInfo;

public class FriendsListAdapter extends BaseAdapter {

	Context mContext;
	LayoutInflater mInflater;
	List<AccountInfo> mDatas;
	
	public FriendsListAdapter(Context c){
		mContext = c;
		mInflater = LayoutInflater.from(mContext);
	}
	public void setData(List<AccountInfo> data){
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
		return mDatas.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		UserItemHolder mHolder;
		if ( convertView == null ){
			convertView = mInflater.inflate(R.layout.fragment_friends_list_item, null);
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
		mHolder.mUserDesc.setText(info.getDescription());
		
		Calendar cal = Calendar.getInstance(Locale.CHINA);
		cal.setTimeInMillis(info.getCreateTime());
		mHolder.mUserUpdateTime.setText(
				cal.get(cal.YEAR) + "/" + cal.get(cal.MONTH) + "/" + cal.get(cal.DAY_OF_MONTH));
		return convertView;
	}

	class UserItemHolder {
		TextView mUserName,mUserDesc,mUserUpdateTime;
		ImageView mUserImage;
		
	}
}
