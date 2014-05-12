package com.trible.scontact.components.widgets;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.trible.scontact.R;
import com.trible.scontact.utils.InputUtil;

public class YesOrNoTipDialog {
	
	PopupDialogger dialogger;

	Context mContext;
	String mTitleString;
	String mTipString;
	
	TextView mTipTextView;
	
	public String getmTitleString() {
		return mTitleString;
	}

	public void setmTitleString(String mTitleString) {
		this.mTitleString = mTitleString;
	}

	public String getmTipString() {
		return mTipString;
	}

	public void setmTipString(String mTipString) {
		this.mTipString = mTipString;
	}
	View view;
	
	OnButtonClickListner btnListner;
	
	public interface OnButtonClickListner{
		void onNoButton();
		void onYesButton();
	}
	
	public void setOnButtonClickListner(OnButtonClickListner listner) {
		btnListner = listner;
	}
	
	public PopupDialogger getDialog() {
		return dialogger;
	}


	public  YesOrNoTipDialog(Context context,String title,String tip) {
		mTipString = tip;
		mContext = context;
		mTitleString = title;
		view = createContentView();
	}
	
	private View createContentView(){
		
		View view = LayoutInflater.from(mContext).inflate(R.layout.popup_yes_or_no_tip, null);
		Button cancelButton = (Button) view.findViewById(R.id.btn_no);
		Button yesButton = (Button) view.findViewById(R.id.btn_yes);
		mTipTextView = (TextView) view.findViewById(R.id.tips_text);
		
		OnClickListener listener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.btn_no:
					if ( btnListner != null ){
						btnListner.onNoButton();
					}
					InputUtil.hideIME((Activity)mContext);
					dialogger.dismissDialogger();
					break;
				case R.id.btn_yes:
					if ( btnListner != null ){
						btnListner.onYesButton();
					}
					break;
				default:
					break;
				}
			}
		};
		cancelButton.setOnClickListener(listener);
		yesButton.setOnClickListener(listener);
		
		dialogger = PopupDialogger.createDialog(mContext);
		return view;
	}
	public void show(){
		dialogger.setTitleText(mTitleString);
		mTipTextView.setText(mTipString);
		if ( TextUtils.isEmpty(mTipString) ){
			mTipTextView.setVisibility(View.GONE);
		} else {
			mTipTextView.setVisibility(View.VISIBLE);
		}
		dialogger.showDialog(mContext,view);
	}
}
