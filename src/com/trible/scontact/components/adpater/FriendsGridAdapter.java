package com.trible.scontact.components.adpater;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trible.scontact.R;
import com.trible.scontact.pojo.AccountInfo;

public class FriendsGridAdapter extends FriendsListAdapter {

	
	public FriendsGridAdapter(Context c){
		super(c);
	}
	
	@Override
	public View getDataView(int position, View convertView, ViewGroup parent) {
		UserItemHolder mHolder;
		if ( convertView == null || !(convertView.getTag() instanceof UserItemHolder)){
			convertView = mInflater.inflate(R.layout.adapter_friends_grid_item, null);
			mHolder = new UserItemHolder();
			mHolder.mUserName = (TextView) convertView.findViewById(R.id.users_list_item_name);
			convertView.setTag(mHolder);
		} else {
			mHolder = (UserItemHolder) convertView.getTag();
		}
		AccountInfo info = mDatas.get(position);
		mHolder.mUserName.setText(info.getDisplayName());
		if ( (position + position / 2) % 2 == 0 ){
			((View)mHolder.mUserName.getParent()).setBackgroundResource(R.color.green);
			mHolder.mUserName.setTextColor(mContext.getResources().getColor(R.color.black));
		} else {
			((View)mHolder.mUserName.getParent()).setBackgroundResource(R.color.blue_qq);
			mHolder.mUserName.setTextColor(mContext.getResources().getColor(R.color.white));
		}
		return convertView;
	}

}
