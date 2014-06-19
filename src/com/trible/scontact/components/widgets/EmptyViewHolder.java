package com.trible.scontact.components.widgets;

import com.trible.scontact.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class EmptyViewHolder {

	public TextView mEmptyText;
	public Button mEmptyBtn;
	public View root;
	public EmptyViewHolder(Context c){
		root = LayoutInflater.from(c).inflate(R.layout.empty_adapter_view, null);
		mEmptyText = (TextView) root.findViewById(R.id.empty_text);
		mEmptyBtn = (Button) root.findViewById(R.id.empty_btn);
		mEmptyBtn.setVisibility(View.GONE);
	}
	public void setOnclickListener(OnClickListener l ){
		mEmptyBtn.setVisibility(View.VISIBLE);
		mEmptyBtn.setOnClickListener(l);
	}
	public void setViewData(EmptyViewData data){
		mEmptyBtn.setText(data.mBtnText);
		mEmptyText.setText(data.mText);
		if ( data.mListener != null ){
			setOnclickListener(data.mListener);
		}
	}
	public static class EmptyViewData{
		public String mText,mBtnText;
		public OnClickListener mListener;
		
	}
}
