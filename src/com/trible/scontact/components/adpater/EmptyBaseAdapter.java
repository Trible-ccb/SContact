package com.trible.scontact.components.adpater;

import com.trible.scontact.R;
import com.trible.scontact.components.widgets.EmptyViewHolder;
import com.trible.scontact.components.widgets.EmptyViewHolder.EmptyViewData;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class EmptyBaseAdapter extends BaseAdapter {

	EmptyViewHolder mEmptyViewHolder;
	public EmptyViewData mEmptyData;
	public boolean mShowEmpty;
	public Context mContext;
	
	public abstract int getDataCount();
	public abstract Object getDataItem(int position);
	public abstract long getDataId(int position);
	public abstract View getDataView(int position, View convertView, ViewGroup parent);
	
	public EmptyBaseAdapter(Context c) {
		mContext = c;
		mEmptyData = new EmptyViewData();
		mEmptyData.mText = c.getString(R.string.empty_search_result);
	}
	public void showEmptyView(boolean show){
		mShowEmpty = show;
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		if ( getDataCount() < 1 && mShowEmpty ){
			return 1;
		} else {
			return getDataCount();
		}
	}

	@Override
	public Object getItem(int position) {
		if ( getDataCount() == 0 )return null;
		return getDataItem(position);
	}
	@Override
	public long getItemId(int position) {
		if ( getDataCount() == 0 )return -1;
		return getDataId(position);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if ( mShowEmpty && getDataCount() == 0 ){
			mEmptyViewHolder = new EmptyViewHolder(mContext);
			mEmptyViewHolder.setViewData(mEmptyData);
			return mEmptyViewHolder.root;
		} else {
			return getDataView(position, convertView, parent);
		}
	}

}
