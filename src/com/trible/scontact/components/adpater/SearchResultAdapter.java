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
import com.trible.scontact.models.Friendinfo;

public class SearchResultAdapter extends BaseAdapter {

	Context mContext;
	LayoutInflater mInflater;
	List<Friendinfo> mDatas;
	
	public SearchResultAdapter(Context c){
		mContext = c;
		mInflater = LayoutInflater.from(mContext);
	}
	public void setData(List<Friendinfo> data){
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
		return mDatas.get(position).getmFriendId();
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
		Friendinfo info = mDatas.get(position);
		mHolder.mUserName.setText(info.getmFriendName());
		mHolder.mUserDesc.setText(info.getmFriendNumber());
		
		Calendar cal = Calendar.getInstance(Locale.CHINA);
		cal.setTimeInMillis(info.getmFriendUpdateTime());
		mHolder.mUserUpdateTime.setText(
				cal.get(cal.YEAR) + "/" + cal.get(cal.MONTH) + "/" + cal.get(cal.DAY_OF_MONTH));
		return convertView;
	}

	class UserItemHolder {
		TextView mUserName,mUserDesc,mUserUpdateTime;
		ImageView mUserImage;
		
	}
}
