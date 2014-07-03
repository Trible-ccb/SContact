package com.trible.scontact.components.adpater;

import java.sql.Time;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.trible.scontact.R;
import com.trible.scontact.pojo.ValidateInfo;
import com.trible.scontact.utils.TimeUtil;

public class InboxListAdapter extends BaseAdapter {

	Context mContext;
	LayoutInflater mInflater;
	List<ValidateInfo> mDatas;
	OnHandleMessageAction mHandleMessageAction;
	
	public InboxListAdapter(Context c){
		mContext = c;
		if ( mContext instanceof OnHandleMessageAction ){
			mHandleMessageAction = (OnHandleMessageAction) mContext;
		}
		mInflater = LayoutInflater.from(mContext);
	}
	public void setData(List<ValidateInfo> data){
		mDatas = data;
		notifyDataSetChanged();
	}
	public void remove(ValidateInfo info){
		if ( mDatas != null && info != null){
			if (!mDatas.remove(info)){
				for ( ValidateInfo i : mDatas ){
					if ( i.getId().equals(info.getId())){
						mDatas.remove(i);
						break;
					}
				}
			}
			notifyDataSetChanged();
		}
	}
	@Override
	public int getCount() {
		return mDatas == null ? 0 : mDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return mDatas.get(position);
	}
	public ValidateInfo getValidateInfoItem(int position) {
		return (ValidateInfo) getItem(position);
	}
	@Override
	public long getItemId(int position) {
		return mDatas.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ContactViewHolder mHolder;
		final int pos = position;
		if ( convertView == null ){
			convertView = mInflater.inflate(R.layout.adapter_view_messages_list_item, null);
			mHolder = new ContactViewHolder();
			mHolder.img = (ImageView) convertView.findViewById(R.id.user_image);
			mHolder.accept = (TextView) convertView.findViewById(R.id.accept);
			mHolder.reject = (TextView) convertView.findViewById(R.id.rejuect);
			mHolder.desp = (TextView) convertView.findViewById(R.id.message_description);
			mHolder.time = (TextView) convertView.findViewById(R.id.message_time);
			convertView.setTag(mHolder);
		} else {
			mHolder = (ContactViewHolder) convertView.getTag();
		}
		mHolder.img.setVisibility(View.GONE);
		final ValidateInfo info = mDatas.get(pos);
		mHolder.reject.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if ( mHandleMessageAction != null ){
					mHandleMessageAction.doReject(info);
				}
			}
		});
		mHolder.accept.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if ( mHandleMessageAction != null ){
					mHandleMessageAction.doAccept(info);
				}
			}
		});
		String desp = null;
		if ( info.getGroupId() == null ){
			if ( info.getStartUser() != null 
					&& info.getEndUser() != null )
			desp = mContext.getString(
					R.string.format_user_as_friend,
					info.getStartUser().getDisplayName());
		} else if ( info.getIs_group_to_user() == 0 ){
			if (info.getStartUser() != null 
					&& info.getGroupInfo() != null )
			desp = mContext.getString(
					R.string.format_user_join_group,
					info.getStartUser().getDisplayName(),
					info.getGroupInfo().getDisplayName());
		} else if (info.getStartUser() != null 
				&& info.getGroupInfo() != null ){
			desp = mContext.getString(
					R.string.format_invite_user_join_group,
					info.getStartUser().getDisplayName(),
					info.getGroupInfo().getDisplayName());
		}
		mHolder.desp.setText(desp);
		mHolder.time.setText(TimeUtil.toTimeString(info.getCreateTime()));
		return convertView;
	}

	class ContactViewHolder {
		ImageView img;
		TextView desp,accept,reject,time;
	}
	
	public interface OnHandleMessageAction{
		void doReject(ValidateInfo info);
		void doAccept(ValidateInfo info);
	}
}
