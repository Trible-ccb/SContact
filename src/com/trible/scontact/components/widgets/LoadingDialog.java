package com.trible.scontact.components.widgets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.trible.scontact.R;

public class LoadingDialog {
	
	PopupDialogger dialogger;

	Context mContext;
	String mTitleString;
	View contentView;
	TextView mTipTextView;
	
	public PopupDialogger getDialog() {
		return dialogger;
	}


	public  LoadingDialog(Context context,String title) {
		mContext = context;
		mTitleString = title;
		contentView = createContentView();
	}
	
	private View createContentView(){
		
		View view = LayoutInflater.from(mContext).inflate(R.layout.popup_loading, null);
		mTipTextView = (TextView) view.findViewById(R.id.loading_tip_content);
		dialogger = PopupDialogger.createDialog(mContext);
		return view;
	}
	public void show(){
		dialogger.setTitleText(mTitleString);
		dialogger.showDialog(mContext,contentView);
	}
	public void setTipText(String text){
		if ( mTipTextView == null )return;
		mTipTextView.setText(text);
	}
}
