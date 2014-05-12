package com.trible.scontact.components.adpater;

import java.util.List;

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

public class MessageListAdapter extends BaseAdapter {

	Context mContext;
	LayoutInflater mInflater;
	List<ValidateInfo> mDatas;
	OnHandleMessageAction mHandleMessageAction;
	
	public MessageListAdapter(Context c){
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
			mHolder.username = (TextView) convertView.findViewById(R.id.user_name);
			convertView.setTag(mHolder);
		} else {
			mHolder = (ContactViewHolder) convertView.getTag();
		}
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
		return convertView;
	}

	class ContactViewHolder {
		ImageView img;
		TextView username,accept,reject;
	}
	
	public interface OnHandleMessageAction{
		void doReject(ValidateInfo info);
		void doAccept(ValidateInfo info);
	}
}
